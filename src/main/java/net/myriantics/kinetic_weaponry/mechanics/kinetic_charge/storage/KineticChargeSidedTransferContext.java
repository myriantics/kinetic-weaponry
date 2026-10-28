package net.myriantics.kinetic_weaponry.mechanics.kinetic_charge.storage;

import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;

public final class KineticChargeSidedTransferContext {

    public static final KineticChargeSidedTransferContext UP = new KineticChargeSidedTransferContext(Direction.UP);
    public static final KineticChargeSidedTransferContext DOWN = new KineticChargeSidedTransferContext(Direction.DOWN);
    public static final KineticChargeSidedTransferContext NORTH = new KineticChargeSidedTransferContext(Direction.NORTH);
    public static final KineticChargeSidedTransferContext SOUTH = new KineticChargeSidedTransferContext(Direction.SOUTH);
    public static final KineticChargeSidedTransferContext EAST = new KineticChargeSidedTransferContext(Direction.EAST);
    public static final KineticChargeSidedTransferContext WEST = new KineticChargeSidedTransferContext(Direction.WEST);
    public static final KineticChargeSidedTransferContext DIRECTIONLESS = new KineticChargeSidedTransferContext(null);

    private final @Nullable Direction direction;
    private final @Nullable Entity source;

    private KineticChargeSidedTransferContext(@Nullable Direction direction) {
        this(direction, null);
    }

    private KineticChargeSidedTransferContext(@Nullable Direction direction, @Nullable Entity source) {
        this.direction = direction;
        this.source = source;
    }

    public static KineticChargeSidedTransferContext of(@Nullable Direction direction) {
        return of(direction, null);
    }

    public static KineticChargeSidedTransferContext of(@Nullable Direction direction, @Nullable Entity entity) {
        if (entity == null) {
            return switch (direction) {
                case DOWN -> DOWN;
                case UP -> UP;
                case NORTH -> NORTH;
                case SOUTH -> SOUTH;
                case WEST -> WEST;
                case EAST -> EAST;
                case null -> DIRECTIONLESS;
            };
        }
        return new KineticChargeSidedTransferContext(direction, entity);
    }

    public KineticChargeSidedTransferContext invert() {
        return this.direction == null ? this : of(this.direction.getOpposite(), this.source);
    }

    public KineticChargeSidedTransferContext with(Direction newFacing) {
        return of(newFacing, this.source);
    }

    public KineticChargeSidedTransferContext with(@Nullable Entity source) {
        return of(this.direction, source);
    }

    public @Nullable Direction getImpactedFace() {
        return this.direction;
    }

    public @Nullable Entity getSource() {
        return this.source;
    }
}
