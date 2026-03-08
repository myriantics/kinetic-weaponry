package net.myriantics.kinetic_weaponry.block.retention_module;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.myriantics.kinetic_weaponry.mechanics.kinetic_charge.KineticBlock;
import net.myriantics.kinetic_weaponry.registry.block.KWBlockEntityTypes;
import net.myriantics.kinetic_weaponry.registry.item.KWDataComponents;
import net.myriantics.kinetic_weaponry.registry.misc.KWNbtIds;

public class KineticRetentionModuleBlockEntity extends BlockEntity {

    private int charge = 0;
    private int maxCharge = 0;

    public KineticRetentionModuleBlockEntity(BlockPos pos, BlockState blockState) {
        super(KWBlockEntityTypes.KINETIC_RETENTION_MODULE, pos, blockState);
    }

    public void updateState(Level level, BlockPos pos, BlockState original) {
        int charge = this.getCharge();
        int maxCharge = this.getMaxCharge();
        level.setBlockAndUpdate(pos, ((KineticBlock) original.getBlock()).withCharge(original, maxCharge == 0 ? 0 : (float) charge / maxCharge));
    }

    @Override
    protected void applyImplicitComponents(DataComponentInput componentInput) {
        super.applyImplicitComponents(componentInput);
        this.setMaxCharge(componentInput.getOrDefault(KWDataComponents.MAX_KINETIC_CHARGE, 0));
        this.setCharge(Math.min(componentInput.getOrDefault(KWDataComponents.KINETIC_CHARGE, 0), this.maxCharge));
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder components) {
        super.collectImplicitComponents(components);
        components.set(KWDataComponents.MAX_KINETIC_CHARGE, this.getMaxCharge());
        components.set(KWDataComponents.KINETIC_CHARGE, this.getCharge());
    }

    private void setCharge(int charge) {
        this.charge = Math.abs(charge);
        this.setChanged();
        if (this.level != null) {
            this.level.updateNeighbourForOutputSignal(this.getBlockPos(), this.level.getBlockState(this.getBlockPos()).getBlock());
        }
    }

    private void setMaxCharge(int maxCharge) {
        this.maxCharge = Math.abs(maxCharge);
    }

    public void setChargeWithSFX(int charge) {
        int initialCharge = this.getCharge();
        BlockPos pos = this.getBlockPos();
        this.setCharge(charge);
        if (this.level != null) {
            // play sound if necessary
            // ooo XOR moment
            if (initialCharge == 0 ^ charge == 0) {
                level.playSound(null, pos, initialCharge == 0 ? SoundEvents.COPPER_BULB_TURN_ON : SoundEvents.COPPER_BULB_TURN_OFF, SoundSource.BLOCKS);
            }
        }
    }

    public int getCharge() {
        return this.charge;
    }

    public int getMaxCharge() {
        return this.maxCharge;
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putInt(KWNbtIds.KINETIC_CHARGE, this.charge);
        tag.putInt(KWNbtIds.MAX_KINETIC_CHARGE, this.maxCharge);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains(KWNbtIds.MAX_KINETIC_CHARGE)) {
            this.setMaxCharge(Math.max(0, tag.getInt(KWNbtIds.MAX_KINETIC_CHARGE)));
        }
        if (tag.contains(KWNbtIds.KINETIC_CHARGE)) {
            this.setCharge(Math.clamp(tag.getInt(KWNbtIds.KINETIC_CHARGE), 0, this.maxCharge));
            if (this.level != null) {
                this.updateState(this.level, this.getBlockPos(), this.level.getBlockState(this.getBlockPos()));
            }
        }
    }
}
