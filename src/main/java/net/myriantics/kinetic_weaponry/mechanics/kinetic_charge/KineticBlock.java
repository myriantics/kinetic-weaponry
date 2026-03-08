package net.myriantics.kinetic_weaponry.mechanics.kinetic_charge;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public interface KineticBlock {
    float getImpactConversionEfficiency(BlockState state, @Nullable KineticImpactType impactType);

    int getCharge(Level level, BlockPos pos, BlockState state);

    int getMaxCharge(Level level, BlockPos pos);

    void setCharge(Level level, BlockPos pos, int charge);

    BlockState withCharge(BlockState state, float ratio);

    default int addCharge(Level level, BlockPos pos, BlockState state, int inboundCharge) {
        int initialCharge = this.getCharge(level, pos, state);
        int maxCharge = this.getMaxCharge(level, pos);

        if (initialCharge >= maxCharge) {
            this.handleOverload(level, pos, state, inboundCharge);
            return 0;
        } else {
            int newCharge = Math.clamp((long) initialCharge + inboundCharge, 0, maxCharge);
            this.setCharge(level, pos, newCharge);
            return newCharge;
        }
    }

    boolean acceptsInput(Level level, BlockPos pos, BlockState state, KineticImpactType impactType, Direction inputDir);

    default void onImpact(Level level, BlockPos pos, BlockState state, @Nullable Player player, @Nullable Direction impactDir, KineticImpactType impactType, float impactDamage) {
        if (this.acceptsInput(level, pos, state, impactType, impactDir)) {
            int inboundCharge = (int) (impactDamage * getImpactConversionEfficiency(state, impactType));
            this.addCharge(level, pos, state, inboundCharge);
        }
    }

    default void handleOverload(Level level, BlockPos pos, BlockState state, int inboundCharge) {

    }
}
