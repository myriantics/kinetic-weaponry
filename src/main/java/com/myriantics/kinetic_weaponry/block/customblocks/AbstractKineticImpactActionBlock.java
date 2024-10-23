package com.myriantics.kinetic_weaponry.block.customblocks;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.MaceItem;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.block.Block;

import javax.annotation.Nullable;

public abstract class AbstractKineticImpactActionBlock extends Block {

    public AbstractKineticImpactActionBlock(Properties properties) {
        super(properties);
    }

    public void onImpact(ServerLevel serverLevel, BlockPos pos, @Nullable ServerPlayer player, float impactDamage) {
        if (player != null && player.getMainHandItem().getItem() instanceof MaceItem) {
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
