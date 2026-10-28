package net.myriantics.kinetic_weaponry.block.detonator;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.myriantics.kinetic_weaponry.mechanics.kinetic_charge.impact.KineticImpactListener;
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
                        KineticDetonatorBlock.this.detonate(level, pos, state, context.getSource(), this.charge * KINETIC_CHARGE_TRANSFER_CONVERSION_EFFICIENCY);
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
        return this.defaultBlockState().setValue(FACING, context.getClickedFace());
    }


    protected void detonate(Level level, BlockPos pos, BlockState state, @Nullable Entity source, float explosionPower) {
        if (level instanceof ServerLevel serverLevel) {
            // so the explosion actually goes through the block
            serverLevel.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
            // low effort special effects go brrt
            serverLevel.addDestroyBlockEffect(pos, state);
            // kaboom? yes rico, kaboom.
            serverLevel.explode(
                    source,
                    pos.getCenter().x,
                    pos.getCenter().y,
                    pos.getCenter().z,
                    Math.clamp(explosionPower, 0, EXPLOSION_POWER_CAP),
                    false,
                    Level.ExplosionInteraction.BLOCK
            );
        }
    }

    @Override
    public void onMeleeKineticImpact(Level level, BlockPos pos, BlockState state, Player player, ItemStack weaponStack, Direction impactedFace, float impactDamage) {
        KineticImpactListener.super.onMeleeKineticImpact(level, pos, state, player, weaponStack, state.getValue(FACING), impactDamage);
    }

    @Override
    public void onKineticImpact(Level level, BlockPos pos, BlockState state, float impactDamage, KineticChargeSidedTransferContext context) {
        if (state.getValue(FACING) == context.getImpactedFace()) {
            this.detonate(level, pos, state, context.getSource(), impactDamage);
        }
    }
}
