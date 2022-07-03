package cy.jdkdigital.hungryhungrychests.client.render.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import cy.jdkdigital.hungryhungrychests.HungryHungryChests;
import cy.jdkdigital.hungryhungrychests.common.entity.HungryChest;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class HungryChestRenderer extends MobRenderer<HungryChest, HungryChestModel<HungryChest>>
{
    private static final ResourceLocation NORMAL = new ResourceLocation("textures/entity/chest/normal.png");

    public static final ModelLayerLocation MAIN_LAYER = new ModelLayerLocation(new ResourceLocation(HungryHungryChests.MODID, "main"), "main");

    public HungryChestRenderer(EntityRendererProvider.Context context) {
        super(context, new HungryChestModel<>(context.bakeLayer(MAIN_LAYER)), 1.0F);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(HungryChest entity) {
        return NORMAL;
    }
}
