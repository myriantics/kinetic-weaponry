package net.myriantics.kinetic_weaponry.block.connecting_bar;

import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

public enum ConnectingBarOrientation implements StringRepresentable {
    NORTH_UNALIGNED(Direction.NORTH),
    NORTH_ALONG_X(Direction.NORTH, Direction.Axis.X),
    NORTH_ALONG_X_OPEN_EAST(Direction.NORTH, Direction.EAST),
    NORTH_ALONG_X_OPEN_WEST(Direction.NORTH, Direction.WEST),
    NORTH_ALONG_Y(Direction.NORTH, Direction.Axis.Y),
    NORTH_ALONG_Y_OPEN_UP(Direction.NORTH, Direction.UP),
    NORTH_ALONG_Y_OPEN_DOWN(Direction.NORTH, Direction.DOWN),
    SOUTH_UNALIGNED(Direction.SOUTH),
    SOUTH_ALONG_X(Direction.SOUTH, Direction.Axis.X),
    SOUTH_ALONG_X_OPEN_EAST(Direction.SOUTH, Direction.EAST),
    SOUTH_ALONG_X_OPEN_WEST(Direction.SOUTH, Direction.WEST),
    SOUTH_ALONG_Y(Direction.SOUTH, Direction.Axis.Y),
    SOUTH_ALONG_Y_OPEN_UP(Direction.SOUTH, Direction.UP),
    SOUTH_ALONG_Y_OPEN_DOWN(Direction.SOUTH, Direction.DOWN),
    EAST_UNALIGNED(Direction.EAST),
    EAST_ALONG_Z(Direction.EAST, Direction.Axis.Z),
    EAST_ALONG_Z_OPEN_NORTH(Direction.EAST, Direction.NORTH),
    EAST_ALONG_Z_OPEN_SOUTH(Direction.EAST, Direction.SOUTH),
    EAST_ALONG_Y(Direction.EAST, Direction.Axis.Y),
    EAST_ALONG_Y_OPEN_UP(Direction.EAST, Direction.UP),
    EAST_ALONG_Y_OPEN_DOWN(Direction.EAST, Direction.DOWN),
    WEST_UNALIGNED(Direction.WEST),
    WEST_ALONG_Z(Direction.WEST, Direction.Axis.Z),
    WEST_ALONG_Z_OPEN_NORTH(Direction.WEST, Direction.NORTH),
    WEST_ALONG_Z_OPEN_SOUTH(Direction.WEST, Direction.SOUTH),
    WEST_ALONG_Y(Direction.WEST, Direction.Axis.Y),
    WEST_ALONG_Y_OPEN_UP(Direction.WEST, Direction.UP),
    WEST_ALONG_Y_OPEN_DOWN(Direction.WEST, Direction.DOWN),
    UP_UNALIGNED(Direction.UP),
    UP_ALONG_X(Direction.UP, Direction.Axis.X),
    UP_ALONG_X_OPEN_EAST(Direction.UP, Direction.EAST),
    UP_ALONG_X_OPEN_WEST(Direction.UP, Direction.WEST),
    UP_ALONG_Z(Direction.UP, Direction.Axis.Z),
    UP_ALONG_Z_OPEN_NORTH(Direction.UP, Direction.NORTH),
    UP_ALONG_Z_OPEN_SOUTH(Direction.UP, Direction.SOUTH),
    DOWN_UNALIGNED(Direction.DOWN),
    DOWN_ALONG_X(Direction.DOWN, Direction.Axis.X),
    DOWN_ALONG_X_OPEN_EAST(Direction.DOWN, Direction.EAST),
    DOWN_ALONG_X_OPEN_WEST(Direction.DOWN, Direction.WEST),
    DOWN_ALONG_Z(Direction.DOWN, Direction.Axis.Z),
    DOWN_ALONG_Z_OPEN_NORTH(Direction.DOWN, Direction.NORTH),
    DOWN_ALONG_Z_OPEN_SOUTH(Direction.DOWN, Direction.SOUTH);

    private static final String USER_DID_A_BAD = "Kinetic Repulsor Orientations may not be aligned along their facing axis!";

    private final String stringRepresentation;
    private final Direction facing;
    private final @Nullable Direction.Axis alignmentAxis;
    private final @Nullable Direction openFace;
    private final Archetype archetype;

    ConnectingBarOrientation(Direction facing) {
        this(facing, null, null);
    }

    ConnectingBarOrientation(Direction facing, Direction.Axis alignmentAxis) {
        this(facing, alignmentAxis, null);
    }

    ConnectingBarOrientation(Direction facing, Direction openFace) {
        this(facing, openFace.getAxis(), openFace);
    }

    ConnectingBarOrientation(Direction facing, @Nullable Direction.Axis alignmentAxis, @Nullable Direction openFace) {
        this.facing = facing;
        if (this.facing.getAxis() == alignmentAxis) {
            throw new IllegalArgumentException(USER_DID_A_BAD);
        }

        this.alignmentAxis = alignmentAxis;
        if (openFace != null && openFace.getAxis() != alignmentAxis) {
            throw new IllegalArgumentException("Open face " + openFace + " provided does not lie on axis " + alignmentAxis);
        }
        this.openFace = openFace;
        this.archetype = selectArchetype(facing, alignmentAxis, openFace);
        this.stringRepresentation = this.archetype.createStringRepresentation(this);
        if (!this.name().toLowerCase(Locale.getDefault()).equals(this.stringRepresentation)) {
            throw new ExceptionInInitializerError("Kinetic Repulsor Orientation initialized with improper enum constant name; expected " + this.stringRepresentation.toUpperCase() + ", got " + this.name());
        }
    }

    public Archetype getArchetype() {
        return this.archetype;
    }

    public Direction getOpenFace() {
        return this.openFace;
    }

    public boolean mayConnectTo(ConnectingBarOrientation other, Direction directionToOther) {
        if (this.facing != other.facing) {
            return false;
        }

        if (other.getFacing().getAxis() == directionToOther.getAxis()) {
            return false;
        }

        if (other.getArchetype() != Archetype.UNALIGNED && other.getAlignmentAxis() != directionToOther.getAxis()) {
            return false;
        }

        return true;
    }

    public static ConnectingBarOrientation findUnaligned(Direction facing) {
        return switch (facing) {
            case DOWN -> DOWN_UNALIGNED;
            case UP -> UP_UNALIGNED;
            case NORTH -> NORTH_UNALIGNED;
            case SOUTH -> SOUTH_UNALIGNED;
            case WEST -> WEST_UNALIGNED;
            case EAST -> EAST_UNALIGNED;
        };
    }

    public static ConnectingBarOrientation findConnector(Direction facing, Direction.Axis alignmentAxis) {
        if (facing.getAxis() == alignmentAxis) {
            throw new IllegalArgumentException(USER_DID_A_BAD);
        }

        return switch (facing) {
            case DOWN -> alignmentAxis == Direction.Axis.X ? DOWN_ALONG_X : DOWN_ALONG_Z;
            case UP -> alignmentAxis == Direction.Axis.X ? UP_ALONG_X : UP_ALONG_Z;
            case NORTH -> alignmentAxis == Direction.Axis.X ? NORTH_ALONG_X : NORTH_ALONG_Y;
            case SOUTH -> alignmentAxis == Direction.Axis.X ? SOUTH_ALONG_X : SOUTH_ALONG_Y;
            case WEST -> alignmentAxis == Direction.Axis.Z ? WEST_ALONG_Z : WEST_ALONG_Y;
            case EAST -> alignmentAxis == Direction.Axis.Z ? EAST_ALONG_Z : EAST_ALONG_Y;
        };
    }

    public static ConnectingBarOrientation findEndpoint(Direction facing, Direction openDirection) {
        return switch (facing) {
            case DOWN -> switch (openDirection) {
                case NORTH -> DOWN_ALONG_Z_OPEN_NORTH;
                case SOUTH -> DOWN_ALONG_Z_OPEN_SOUTH;
                case WEST -> DOWN_ALONG_X_OPEN_WEST;
                case EAST -> DOWN_ALONG_X_OPEN_EAST;
                default -> throw new IllegalArgumentException(USER_DID_A_BAD);
            };
            case UP -> switch (openDirection) {
                case NORTH -> UP_ALONG_Z_OPEN_NORTH;
                case SOUTH -> UP_ALONG_Z_OPEN_SOUTH;
                case WEST -> UP_ALONG_X_OPEN_WEST;
                case EAST -> UP_ALONG_X_OPEN_EAST;
                default -> throw new IllegalArgumentException(USER_DID_A_BAD);
            };
            case NORTH -> switch (openDirection) {
                case DOWN -> NORTH_ALONG_Y_OPEN_DOWN;
                case UP -> NORTH_ALONG_Y_OPEN_UP;
                case WEST -> NORTH_ALONG_X_OPEN_WEST;
                case EAST -> NORTH_ALONG_X_OPEN_EAST;
                default -> throw new IllegalArgumentException(USER_DID_A_BAD);
            };
            case SOUTH -> switch (openDirection) {
                case DOWN -> SOUTH_ALONG_Y_OPEN_DOWN;
                case UP -> SOUTH_ALONG_Y_OPEN_UP;
                case WEST -> SOUTH_ALONG_X_OPEN_WEST;
                case EAST -> SOUTH_ALONG_X_OPEN_EAST;
                default -> throw new IllegalArgumentException(USER_DID_A_BAD);
            };
            case WEST -> switch (openDirection) {
                case DOWN -> WEST_ALONG_Y_OPEN_DOWN;
                case UP -> WEST_ALONG_Y_OPEN_UP;
                case NORTH -> WEST_ALONG_Z_OPEN_NORTH;
                case SOUTH -> WEST_ALONG_Z_OPEN_SOUTH;
                default -> throw new IllegalArgumentException(USER_DID_A_BAD);
            };
            case EAST -> switch (openDirection) {
                case DOWN -> EAST_ALONG_Y_OPEN_DOWN;
                case UP -> EAST_ALONG_Y_OPEN_UP;
                case NORTH -> EAST_ALONG_Z_OPEN_NORTH;
                case SOUTH -> EAST_ALONG_Z_OPEN_SOUTH;
                default -> throw new IllegalArgumentException(USER_DID_A_BAD);
            };
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

    private static Archetype selectArchetype(Direction facing, @Nullable Direction.Axis alignmentAxis, @Nullable Direction openFace) {
        if (alignmentAxis == null) {
            if (openFace != null) {
                throw new IllegalArgumentException("Cannot assign an open face to an unaligned repulsor orientation!");
            }
        } else {
            if (openFace == null) {
                return Archetype.CONNECTOR;
            } else {
                return Archetype.ENDPOINT;
            }
        }
        return Archetype.UNALIGNED;
    }

    public enum Archetype {
        UNALIGNED,
        ENDPOINT,
        CONNECTOR;

        public boolean isConnected() {
            return this != UNALIGNED;
        }

        private String createStringRepresentation(ConnectingBarOrientation orientation) {
            return switch (this) {
                case UNALIGNED -> orientation.getFacing().getName() + "_unaligned";
                case ENDPOINT -> orientation.getFacing().getName() + "_along_" + orientation.getAlignmentAxis().getName() + "_open_" + orientation.openFace.getName();
                case CONNECTOR -> orientation.getFacing().getName() + "_along_" + orientation.getAlignmentAxis().getName();
            };
        }
    }
}
