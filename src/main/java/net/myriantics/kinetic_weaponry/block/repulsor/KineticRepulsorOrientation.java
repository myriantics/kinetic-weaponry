package net.myriantics.kinetic_weaponry.block.repulsor;

import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;

public enum KineticRepulsorOrientation implements StringRepresentable {
    NORTH_ALONG_X("north_along_x", Direction.NORTH, Direction.Axis.X),
    SOUTH_ALONG_X("south_along_x", Direction.SOUTH, Direction.Axis.X),
    NORTH_ALONG_Y("north_along_y", Direction.NORTH, Direction.Axis.Y),
    SOUTH_ALONG_Y("south_along_y", Direction.SOUTH, Direction.Axis.Y),
    EAST_ALONG_Z("east_along_z", Direction.EAST, Direction.Axis.Z),
    WEST_ALONG_Z("west_along_z", Direction.WEST, Direction.Axis.Z),
    EAST_ALONG_Y("east_along_y", Direction.EAST, Direction.Axis.Y),
    WEST_ALONG_Y("west_along_y", Direction.WEST, Direction.Axis.Y),
    UP_ALONG_X("up_along_x", Direction.UP, Direction.Axis.X),
    DOWN_ALONG_X("down_along_x", Direction.DOWN, Direction.Axis.X),
    UP_ALONG_Z("up_along_z", Direction.UP, Direction.Axis.Z),
    DOWN_ALONG_Z("down_along_z", Direction.DOWN, Direction.Axis.Z);

    private final String stringRepresentation;
    private final Direction facing;
    private final Direction.Axis alignmentAxis;

    KineticRepulsorOrientation(String stringRepresentation, Direction facing, Direction.Axis alignmentAxis) {
        this.stringRepresentation = stringRepresentation;
        this.facing = facing;
        this.alignmentAxis = alignmentAxis;
    }

    public static KineticRepulsorOrientation of(Direction direction, Direction.Axis alignmentAxis) {
        if (direction.getAxis() == alignmentAxis) {
            throw new IllegalArgumentException();
        }

        return switch (direction) {
            case DOWN -> alignmentAxis == Direction.Axis.X ? DOWN_ALONG_X : DOWN_ALONG_Z;
            case UP -> alignmentAxis == Direction.Axis.X ? UP_ALONG_X : UP_ALONG_Z;
            case NORTH -> alignmentAxis == Direction.Axis.X ? NORTH_ALONG_X : NORTH_ALONG_Y;
            case SOUTH -> alignmentAxis == Direction.Axis.X ? SOUTH_ALONG_X : SOUTH_ALONG_Y;
            case WEST -> alignmentAxis == Direction.Axis.Z ? WEST_ALONG_Z : WEST_ALONG_Y;
            case EAST -> alignmentAxis == Direction.Axis.Z ? EAST_ALONG_Z : EAST_ALONG_Y;
        };
    }

    @Override
    public String getSerializedName() {
        return this.stringRepresentation;
    }

    public Direction getFacing() {
        return this.facing;
    }

    public Direction.Axis getAlignmentAxis() {
        return this.alignmentAxis;
    }
}
