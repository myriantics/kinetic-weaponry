package net.myriantics.kinetic_weaponry.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import net.myriantics.kinetic_weaponry.KWCommon;
import net.myriantics.kinetic_weaponry.entity.misc.PrimedKineticDetonatorEntity;

public class PrimedKineticDetonatorRenderer extends EntityRenderer<PrimedKineticDetonatorEntity> {

    private static final ModelResourceLocation MODEL_LOCATION = new ModelResourceLocation(PrimedKineticDetonatorEntity.DEFAULT_MODEL_LOCATION, "tung");

    protected final BlockRenderDispatcher blockRenderer;

    public PrimedKineticDetonatorRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.blockRenderer = context.getBlockRenderDispatcher();
    }

    @Override
    public void render(PrimedKineticDetonatorEntity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
        poseStack.pushPose();
        Direction facing = entity.getDirection();
        Vec3 vec3 = this.getRenderOffset(entity, partialTick);
        poseStack.translate(vec3.x(), vec3.y(), vec3.z());

        poseStack.mulPose(Axis.XP.rotationDegrees(entity.getXRot()));
        poseStack.mulPose(Axis.YP.rotationDegrees(facing.toYRot()));

        if (!entity.isInvisible()) {
            ModelManager manager = this.blockRenderer.getBlockModelShaper().getModelManager();
            this.blockRenderer
                    .getModelRenderer()
                    .renderModel(
                            poseStack.last(),
                            bufferSource.getBuffer(Sheets.solidBlockSheet()),
                            null,
                            manager.getModel(MODEL_LOCATION),
                            1.0f,
                            1.0f,
                            1.0f,
                            packedLight,
                            OverlayTexture.NO_OVERLAY
                    );
        }

        poseStack.popPose();
    }

    @SuppressWarnings("deprecation")
    @Override
    public ResourceLocation getTextureLocation(PrimedKineticDetonatorEntity entity) {
        return TextureAtlas.LOCATION_BLOCKS;
    }
}
