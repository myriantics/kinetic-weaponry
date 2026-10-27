package net.myriantics.kinetic_weaponry.block.retention_module;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.myriantics.kinetic_weaponry.KWUtil;
import net.myriantics.kinetic_weaponry.block.retention_module.headgear.AbstractKineticRetentionHeadgearBlock;
import net.myriantics.kinetic_weaponry.mechanics.kinetic_charge.block.KineticChargeVisualizer;
import net.myriantics.kinetic_weaponry.mechanics.kinetic_charge.storage.KineticChargeSidedTransferContext;
import net.myriantics.kinetic_weaponry.mechanics.kinetic_charge.storage.KineticChargeStorage;
import net.myriantics.kinetic_weaponry.mechanics.kinetic_charge.storage.SimpleKineticChargeStorage;
import net.myriantics.kinetic_weaponry.registry.block.KWBlockEntityTypes;
import net.myriantics.kinetic_weaponry.registry.item.KWDataComponents;
import net.myriantics.kinetic_weaponry.registry.misc.KWNbtIds;
import org.jetbrains.annotations.Nullable;

public class KineticRetentionModuleBlockEntity extends BlockEntity {

    private long charge = 0;
    private long maxCharge = 0;
    private float kineticImpactConversionEfficiency = 1f;
    private final KineticChargeStorage storage = new SimpleKineticChargeStorage() {
        @Override
        public void setCharge(long newCharge) {
            KineticRetentionModuleBlockEntity.this.charge = newCharge;
        }

        @Override
        public long getMaxCharge() {
            return KineticRetentionModuleBlockEntity.this.maxCharge;
        }

        @Override
        public long getAmount() {
            return KineticRetentionModuleBlockEntity.this.charge;
        }

        @Override
        protected void onFinalCommit() {
            super.onFinalCommit();
            KineticRetentionModuleBlockEntity.this.setChanged();
            KineticRetentionModuleBlockEntity.this.updateState();
        }
    };

    public KineticRetentionModuleBlockEntity(BlockPos pos, BlockState blockState) {
        super(KWBlockEntityTypes.KINETIC_RETENTION_MODULE.value(), pos, blockState);
    }

    public void updateState() {
        long charge = this.getCharge();
        long maxCharge = this.getMaxCharge();
        if (this.level != null && this.getBlockState().getBlock() instanceof KineticChargeVisualizer visualizer) {
            this.level.setBlockAndUpdate(
                    this.worldPosition,
                    visualizer.withCharge(this.getBlockState(), maxCharge == 0 ? 0 : (float) charge / maxCharge)
            );
        }
    }

    @Override
    protected void applyImplicitComponents(DataComponentInput componentInput) {
        super.applyImplicitComponents(componentInput);
        this.maxCharge = componentInput.getOrDefault(KWDataComponents.MAX_KINETIC_CHARGE.value(), 0);
        this.charge = componentInput.getOrDefault(KWDataComponents.KINETIC_CHARGE.value(), 0);
        this.kineticImpactConversionEfficiency = componentInput.getOrDefault(KWDataComponents.KINETIC_IMPACT_CONVERSION_EFFICIENCY.value(), 1f);
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder components) {
        super.collectImplicitComponents(components);
        components.set(KWDataComponents.MAX_KINETIC_CHARGE.value(), KWUtil.clampLong(this.getMaxCharge()));
        components.set(KWDataComponents.KINETIC_CHARGE.value(), KWUtil.clampLong(this.getCharge()));
        components.set(KWDataComponents.KINETIC_IMPACT_CONVERSION_EFFICIENCY.value(), this.kineticImpactConversionEfficiency);
    }

    public KineticChargeStorage getStorage(KineticChargeSidedTransferContext context) {
        @Nullable Direction impactedFace = context.getImpactedFace();

        if (impactedFace == null) {
            return this.storage;
        }
        boolean flipped = this.getBlockState().hasProperty(AbstractKineticRetentionHeadgearBlock.INVERTED) && this.getBlockState().getValue(AbstractKineticRetentionHeadgearBlock.INVERTED);

        return (flipped ? impactedFace : impactedFace.getOpposite()) == this.getBlockState().getValue(AbstractKineticRetentionModuleBlock.FACING) ? this.storage : null;
    }

    private void setCharge(long charge) {
        this.charge = Math.max(charge, 0);
        this.setChanged();
        if (this.level != null) {
            this.level.updateNeighbourForOutputSignal(this.getBlockPos(), this.level.getBlockState(this.getBlockPos()).getBlock());
        }
    }

    private void setMaxCharge(long maxCharge) {
        this.maxCharge = Math.abs(maxCharge);
    }

    public float getKineticImpactConversionEfficiency() {
        return this.kineticImpactConversionEfficiency;
    }

    public void setChargeWithSFX(long charge) {
        long initialCharge = this.getCharge();
        BlockPos pos = this.getBlockPos();
        this.setCharge(charge);
        if (this.level != null) {
            // play sound if necessary
            // ooo XOR moment
            if (initialCharge == 0 ^ charge == 0) {
                level.playSound(null, pos, initialCharge == 0 ? SoundEvents.COPPER_BULB_TURN_ON : SoundEvents.COPPER_BULB_TURN_OFF, SoundSource.BLOCKS);
            }
        }
    }

    public long getCharge() {
        return this.charge;
    }

    public long getMaxCharge() {
        return this.maxCharge;
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putLong(KWNbtIds.KINETIC_CHARGE, this.charge);
        tag.putLong(KWNbtIds.MAX_KINETIC_CHARGE, this.maxCharge);
        tag.putFloat(KWNbtIds.KINETIC_IMPACT_CONVERSION_EFFICIENCY, this.kineticImpactConversionEfficiency);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains(KWNbtIds.MAX_KINETIC_CHARGE, CompoundTag.TAG_LONG)) {
            this.setMaxCharge(Math.max(0, tag.getLong(KWNbtIds.MAX_KINETIC_CHARGE)));
        }
        if (tag.contains(KWNbtIds.KINETIC_CHARGE, CompoundTag.TAG_LONG)) {
            this.setCharge(Math.clamp(tag.getLong(KWNbtIds.KINETIC_CHARGE), 0, this.maxCharge));
            if (this.level != null) {
                this.updateState();
            }
        }
        if (tag.contains(KWNbtIds.KINETIC_IMPACT_CONVERSION_EFFICIENCY, CompoundTag.TAG_FLOAT)) {
            this.kineticImpactConversionEfficiency = tag.getFloat(KWNbtIds.KINETIC_IMPACT_CONVERSION_EFFICIENCY);
        }
    }
}
