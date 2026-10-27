package net.myriantics.kinetic_weaponry.mechanics.kinetic_charge.storage;

import net.fabricmc.fabric.api.lookup.v1.block.BlockApiLookup;
import net.fabricmc.fabric.api.lookup.v1.item.ItemApiLookup;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.core.Direction;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.phys.AABB;
import net.myriantics.kinetic_weaponry.KWCommon;
import net.myriantics.kinetic_weaponry.block.retention_module.KineticRetentionModuleBlockEntity;
import net.myriantics.kinetic_weaponry.entity.minecart.MinecartKineticChargingBusEntity;
import net.myriantics.kinetic_weaponry.mechanics.kinetic_charge.block.SimpleKineticBlock;
import net.myriantics.kinetic_weaponry.mechanics.kinetic_charge.SimpleKineticItem;
import net.myriantics.kinetic_weaponry.registry.block.KWBlockEntityTypes;
import net.myriantics.kinetic_weaponry.registry.item.KWDataComponents;
import net.myriantics.kinetic_weaponry.tag.KWBlockTags;

import java.util.List;

public interface KineticChargeStorage {

    BlockApiLookup<KineticChargeStorage, KineticChargeSidedTransferContext> SIDED = BlockApiLookup.get(
            KWCommon.locate("sided_kinetic_charge_storage"),
            KineticChargeStorage.class,
            KineticChargeSidedTransferContext.class
    );

    ItemApiLookup<KineticChargeStorage, ContainerItemContext> ITEM = ItemApiLookup.get(
            KWCommon.locate("item_kinetic_charge_storage"),
            KineticChargeStorage.class,
            ContainerItemContext.class
    );

    default boolean supportsInsertion() {
        return true;
    }

    long insert(long maxAmount, TransactionContext tx);

    default boolean supportsExtraction() {
        return true;
    }

    long extract(long maxAmount, TransactionContext tx);

    static void init() {
        SIDED.registerForBlockEntity(
                KineticRetentionModuleBlockEntity::getStorage,
                KWBlockEntityTypes.KINETIC_RETENTION_MODULE.value()
        );

        SIDED.registerFallback(((level, pos, state, blockEntity, context) -> {
            if (context.getImpactedFace() == Direction.UP && state.is(KWBlockTags.KINETIC_MINECART_INTERFACE_ENABLING_RAILS)) {
                double limiter = 0.2;
                AABB searchAABB = new AABB(pos.getX() + limiter, pos.getY(), pos.getZ() + limiter, pos.getX() + 1 - limiter, pos.getY() + 1 - limiter, pos.getZ() + 1 - limiter);
                List<MinecartKineticChargingBusEntity> minecarts = level.getEntitiesOfClass(MinecartKineticChargingBusEntity.class, searchAABB);
                
                return minecarts.get(level.getRandom().nextInt(minecarts.size())).getStorage();
            }

            return null;
        }));
        SIDED.registerFallback((level, pos, state, blockEntity, context) -> {
            if (state.getBlock() instanceof SimpleKineticBlock kineticBlock) {
                return new SimpleKineticChargeStorage() {
                    long charge = kineticBlock.getCharge(level, pos, state);

                    @Override
                    public long getAmount() {
                        return this.charge = kineticBlock.getCharge(level, pos, state);
                    }

                    @Override
                    public void setCharge(long newCharge) {
                        this.charge = newCharge;
                    }

                    @Override
                    public long getMaxCharge() {
                        return kineticBlock.getMaxCharge(level, pos, state);
                    }

                    @Override
                    public boolean supportsInsertion() {
                        return kineticBlock.allowsInsertion(level, pos, state, context);
                    }

                    @Override
                    public boolean supportsExtraction() {
                        return kineticBlock.allowsExtraction(level, pos, state, context);
                    }

                    @Override
                    protected void onFinalCommit() {
                        super.onFinalCommit();
                        kineticBlock.setCharge(level, pos, state, charge);
                    }
                };
            }
            return null;
        });

        ITEM.registerFallback((itemStack, context) -> itemStack.has(KWDataComponents.INFINITE_KINETIC_CHARGE.value()) ? InfiniteKineticChargeStorage.INSTANCE : null);
        ITEM.registerFallback((itemStack, context) -> {
            if (itemStack.getItem() instanceof SimpleKineticItem kineticItem) {
                return new SimpleKineticChargeStorage() {
                    private long kineticCharge = kineticItem.getCharge(itemStack);

                    @Override
                    public void setCharge(long newCharge) {
                        this.kineticCharge = newCharge;
                    }

                    @Override
                    public long getMaxCharge() {
                        return kineticItem.getMaxCharge(itemStack);
                    }

                    @Override
                    public long getAmount() {
                        return this.kineticCharge;
                    }

                    @Override
                    protected void onFinalCommit() {
                        super.onFinalCommit();
                        kineticItem.setCharge(itemStack, this.kineticCharge);
                    }
                };
            }
            return null;
        });
    }
}
