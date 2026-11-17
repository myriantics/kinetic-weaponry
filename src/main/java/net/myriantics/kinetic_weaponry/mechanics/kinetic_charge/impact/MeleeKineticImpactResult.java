package net.myriantics.kinetic_weaponry.mechanics.kinetic_charge.impact;

import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;

public final class MeleeKineticImpactResult {

    public static final MeleeKineticImpactResult SUCCESS_NO_ENTITY = new MeleeKineticImpactResult(true, null);
    public static final MeleeKineticImpactResult FAIL = new MeleeKineticImpactResult(false, null);

    private final boolean success;
    private final @Nullable Entity impactedEntity;

    private MeleeKineticImpactResult(boolean success, @Nullable Entity impactedEntity) {
        this.success = success;
        this.impactedEntity = impactedEntity;
    }

    public static MeleeKineticImpactResult of(boolean success) {
        return success ? SUCCESS_NO_ENTITY : FAIL;
    }

    public static MeleeKineticImpactResult success(@Nullable Entity impactedEntity) {
        return of(true, impactedEntity);
    }

    public static MeleeKineticImpactResult of(boolean success, @Nullable Entity impactedEntity) {
        if (!success) {
            return FAIL;
        }
        if (impactedEntity == null) {
            return SUCCESS_NO_ENTITY;
        }
        return new MeleeKineticImpactResult(true, impactedEntity);
    }

    public boolean isSuccess() {
        return this.success;
    }

    public @Nullable Entity getImpactedEntity() {
        return this.impactedEntity;
    }
}
