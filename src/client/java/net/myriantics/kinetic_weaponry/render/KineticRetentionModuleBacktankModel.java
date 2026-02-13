package net.myriantics.kinetic_weaponry.render;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.LivingEntity;

public class KineticRetentionModuleBacktankModel<T extends LivingEntity, M extends EntityModel<T>> {
    public final ModelPart main;

    public KineticRetentionModuleBacktankModel(ModelPart main) {
        this.main = main;
    }

    /*public static LayerDefinition getTexturedModelData() {
        MeshDefinition meshDefinition = new MeshDefinition();
        //PartDefinition partDefinition
    }*/
}
