package net.myriantics.kinetic_weaponry.block.trial_weave;

import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;

public enum TrialWeaveState implements StringRepresentable {
    INERT("inert"),
    EMITTING_XY_NORTH("emitting_xy_north"),
    EMITTING_XY_SOUTH("emitting_xy_south"),
    EMITTING_YZ_EAST("emitting_yz_east"),
    EMITTING_YZ_WEST("emitting_yz_west"),
    EMITTING_XZ_UP("emitting_xz_up"),
    EMITTING_XZ_DOWN("emitting_xz_down"),
    CONDUCTING_NORTH("conducting_north"),
    CONDUCTING_SOUTH("conducting"),
    CONDUCTING_EAST("conducting_east"),
    CONDUCTING_WEST("conducting_west"),
    CONDUCTING_UP("conducting_up"),
    CONDUCTING_DOWN("conducting_down");

    private final String name;

    TrialWeaveState(String name) {
        this.name = name;
    }

    public static TrialWeaveState getEmitting(Direction impactedFace) {
        return switch (impactedFace) {
            case DOWN -> EMITTING_XZ_UP;
            case UP -> EMITTING_XZ_DOWN;
            case NORTH -> EMITTING_XY_SOUTH;
            case SOUTH -> EMITTING_XY_NORTH;
            case WEST -> EMITTING_YZ_EAST;
            case EAST -> EMITTING_YZ_WEST;
        };
    }

    public static TrialWeaveState getConducting(Direction conductionDirection) {
        return switch (conductionDirection) {
            case DOWN -> CONDUCTING_DOWN;
            case UP -> CONDUCTING_UP;
            case NORTH -> CONDUCTING_NORTH;
            case SOUTH -> CONDUCTING_SOUTH;
            case WEST -> CONDUCTING_WEST;
            case EAST -> CONDUCTING_EAST;
        };
    }

    @Override
    public String getSerializedName() {
        return this.name;
    }

    public boolean isActive() {
        return this != INERT;
    }
}
