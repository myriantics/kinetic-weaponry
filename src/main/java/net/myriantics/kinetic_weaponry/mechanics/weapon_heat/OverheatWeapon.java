package net.myriantics.kinetic_weaponry.mechanics.weapon_heat;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.myriantics.kinetic_weaponry.registry.item.KWDataComponents;
import net.myriantics.kinetic_weaponry.registry.misc.KWSounds;

import java.util.List;
import java.util.function.Predicate;

public interface OverheatWeapon {

   int DEFAULT_HEAT_UNIT_DISSIPATION_RATE = 4;
   float IGNITION_THRESHOLD = 0.75f;

    default int getHeatUnits(ItemStack stack) {
        return stack.getOrDefault(KWDataComponents.HEAT_UNITS, 0);
    }

    default int getMaxHeatUnits(ItemStack stack) {
        return stack.getOrDefault(KWDataComponents.MAX_HEAT_UNITS, 0);
    }

    default void setHeatUnits(ItemStack stack, int heatUnits) {
        stack.set(KWDataComponents.HEAT_UNITS, heatUnits);
    }

    default int getHeatDissipationRate(ItemStack stack) {
        return stack.getOrDefault(KWDataComponents.HEAT_UNIT_DISSIPATION_RATE, DEFAULT_HEAT_UNIT_DISSIPATION_RATE);
    }

    default boolean addHeatUnits(ItemStack stack, int heatUnits) {
        int maxHeat = this.getMaxHeatUnits(stack);
        int initialHeat = this.getHeatUnits(stack);

        int newHeat = Math.clamp((long) heatUnits + (long) initialHeat, 0, maxHeat);

        this.setHeatUnits(stack, newHeat);
        return newHeat != initialHeat;
    }

    default List<Integer> getHeatSoundThresholds(ItemStack stack) {
        return stack.getOrDefault(KWDataComponents.HEAT_SOUND_THRESHOLDS, List.of());
    }

    default void tickHeat(Entity entity, ItemStack stack) {
        Level level = entity.level();

        int maxHeatUnits = this.getMaxHeatUnits(stack);
        int oldHeatUnits = this.getHeatUnits(stack);
        int dissipationRate = this.getHeatDissipationRate(stack);

        if (maxHeatUnits == 0) {
            return;
        }

        float heatRatio = (float) oldHeatUnits / maxHeatUnits;

        if (entity.isUnderWater()) {
            dissipationRate *= 8;
        } else if (entity.isInWaterOrRain()) {
            dissipationRate *= 4;
        }

        if (entity.tickCount % 20 == 0 && oldHeatUnits > 0) {
            if (!level.isClientSide()) {
                // don't decrement heat while using
                if (!this.allowHeatDissipation(entity, stack)) {
                    if (entity instanceof LivingEntity livingEntity && heatRatio > IGNITION_THRESHOLD) {
                        livingEntity.igniteForTicks((int) (40f * heatRatio));
                    }
                } else {
                    this.addHeatUnits(stack, -dissipationRate);
                    int newHeatUnits = this.getHeatUnits(stack);

                    for (int threshold : this.getHeatSoundThresholds(stack)) {
                        if (oldHeatUnits > threshold && newHeatUnits <= threshold) {
                            level.playSound(
                                    null,
                                    entity.getX(),
                                    entity.getY(),
                                    entity.getZ(),
                                    KWSounds.KINETIC_SHORTBOW_COOL_DOWN,
                                    SoundSource.PLAYERS,
                                    1.0F,
                                    1.0F / (level.getRandom().nextFloat() * 0.4F + 2.4F) * 0.5F + (float) 0.05 * this.getHeatUnits(stack)
                            );
                        }
                    }
                }
            }
        }
    }

    boolean allowHeatDissipation(Entity entity, ItemStack stack);
}
