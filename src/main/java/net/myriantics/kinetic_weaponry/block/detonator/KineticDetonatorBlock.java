package net.myriantics.kinetic_weaponry.block.detonator;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.myriantics.kinetic_weaponry.entity.misc.PrimedKineticDetonatorEntity;
import net.myriantics.kinetic_weaponry.mechanics.kinetic_charge.impact.KineticImpactListener;
import net.myriantics.kinetic_weaponry.mechanics.kinetic_charge.impact.MeleeKineticImpactResult;
import net.myriantics.kinetic_weaponry.mechanics.kinetic_charge.storage.KineticChargeSidedTransferContext;
import net.myriantics.kinetic_weaponry.mechanics.kinetic_charge.storage.KineticChargeStorage;
import net.myriantics.kinetic_weaponry.mechanics.kinetic_charge.storage.SimpleKineticChargeStorage;
import org.jetbrains.annotations.Nullable;

public class KineticDetonatorBlock extends Block implements KineticImpactListener {

    public static final DirectionProperty FACING = BlockStateProperties.FACING;

    public static float DEFAULT_IMPACT_CONVERSION_EFFICIENCY = 0.65f;
    public static float KINETIC_CHARGE_TRANSFER_CONVERSION_EFFICIENCY = 2.67f;
    public static int EXPLOSION_POWER_CAP = 32;

    public KineticDetonatorBlock(Properties properties) {
        super(properties);

        registerDefaultState(stateDefinition.any()
                .setValue(FACING, Direction.UP)
        );

        this.registerStorage();
    }

    protected void registerStorage() {
        KineticChargeStorage.SIDED.registerForBlocks(
                (level, pos, state, blockEntity, context) -> state.getValue(FACING) != context.getImpactedFace() ? null : new SimpleKineticChargeStorage() {
                    private long charge = 0;

                    @Override
                    public void setCharge(long newCharge) {
                        this.charge = newCharge;
                    }

                    @Override
                    public long getMaxCharge() {
                        return Long.MAX_VALUE;
                    }

                    @Override
                    public long getAmount() {
                        return 0;
                    }

                    @Override
                    protected void onFinalCommit() {
                        super.onFinalCommit();
                        KineticDetonatorBlock.this.prime(level, pos, state, context.getSource() instanceof LivingEntity livingEntity ? livingEntity : null, this.charge * KINETIC_CHARGE_TRANSFER_CONVERSION_EFFICIENCY);
                    }
                },
                this
        );
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getNearestLookingDirection().getOpposite());
    }

    protected PrimedKineticDetonatorEntity prime(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity owner, float explosionPower) {
        if (level.isClientSide()) {
            return null;
        }

        level.destroyBlock(pos, false);
        level.addDestroyBlockEffect(pos, state);
        PrimedKineticDetonatorEntity entity = new PrimedKineticDetonatorEntity(level, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, owner, Math.clamp(explosionPower, 0, EXPLOSION_POWER_CAP), state.getValue(FACING));
        if (level.addFreshEntity(entity)) {
            level.gameEvent(owner, GameEvent.PRIME_FUSE, pos);
            return entity;
        } else {
            return null;
        }
    }

    @Override
    public MeleeKineticImpactResult onMeleeKineticImpact(Level level, BlockPos pos, BlockState state, Player player, ItemStack weaponStack, Direction impactedFace, float impactDamage) {
        PrimedKineticDetonatorEntity entity = this.prime(level, pos, state, player, impactDamage);
        return MeleeKineticImpactResult.of(true, entity);
    }

    @Override
    public void onKineticImpact(Level level, BlockPos pos, BlockState state, float impactDamage, KineticChargeSidedTransferContext context) {
        if (state.getValue(FACING) == context.getImpactedFace() && context.getSource() instanceof LivingEntity livingEntity) {
            this.prime(level, pos, state, livingEntity, impactDamage);
        }
    }
}
