package net.myriantics.kinetic_weaponry.block.charging_bus;

import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.Nullable;

public enum KineticChargingBusShape implements StringRepresentable {
    SINGLE("single"),
    BOTTOM("bottom"),
    TOP("top");

    private final String name;

    KineticChargingBusShape(String name) {
        this.name = name;
    }

    @Override
    public String getSerializedName() {
        return this.name;
    }

    public @Nullable Direction getDirectionToCounterpart(Direction facing) {
        return switch (this) {
            case SINGLE -> null;
            case BOTTOM -> facing;
            case TOP -> facing.getOpposite();
        };
    }

    public KineticChargingBusShape getOpposite() {
        return switch (this) {
            case SINGLE -> null;
            case BOTTOM -> TOP;
            case TOP -> BOTTOM;
        };
    }

    public boolean isDominant() {
        return this != TOP;
    }

    public boolean isSingle() {
        return this == SINGLE;
    }

    public boolean isCompatibleWith(KineticChargingBusShape otherShape) {
        return !this.isSingle() && this.getOpposite() == otherShape;
    }
}
