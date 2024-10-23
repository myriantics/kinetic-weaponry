package com.myriantics.kinetic_weaponry.client.render;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.ModelUtils;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.blockentity.SkullBlockRenderer;
import net.minecraft.data.DataProvider;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.client.RenderTypeHelper;
import net.neoforged.neoforge.client.model.generators.ModelBuilder;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.client.model.generators.ModelProvider;

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
