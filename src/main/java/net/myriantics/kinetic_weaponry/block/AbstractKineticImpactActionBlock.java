package net.myriantics.kinetic_weaponry.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.MaceItem;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractKineticImpactActionBlock extends Block {

    public AbstractKineticImpactActionBlock(Properties properties) {
        super(properties);
    }

    public void onImpact(ServerLevel serverLevel, BlockPos pos, @Nullable ServerPlayer player, float impactDamage) {
        if (player != null && player.getMainHandItem().getItem() instanceof MaceItem && impactDamage > 0) {
            ItemStack maceStack = player.getMainHandItem();

            player.setSpawnExtraParticlesOnFall(true);
            SoundEvent soundevent = impactDamage > 10 ? SoundEvents.MACE_SMASH_GROUND_HEAVY : SoundEvents.MACE_SMASH_GROUND;
            serverLevel.playSound(null, player.getX(), player.getY(), player.getZ(), soundevent, player.getSoundSource(), 1.0F, 1.0F);

            player.resetFallDistance();
            maceStack.hurtAndBreak(1, player, EquipmentSlot.MAINHAND);
        }
    }

    // should be checked before calling onImpact
    public boolean isImpactValid(ServerLevel serverLevel, BlockPos pos) {
        return true;
    };

}
