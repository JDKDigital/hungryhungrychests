package cy.jdkdigital.hungryhungrychests.client.setup;

import cy.jdkdigital.hungryhungrychests.HungryHungryChests;
import cy.jdkdigital.hungryhungrychests.client.render.entity.HungryChestModel;
import cy.jdkdigital.hungryhungrychests.client.render.entity.HungryChestRenderer;
import cy.jdkdigital.hungryhungrychests.core.init.ModEntities;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = HungryHungryChests.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Events
{
    @SubscribeEvent
    public static void layerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(HungryChestRenderer.MAIN_LAYER, HungryChestModel::createBodyLayer);
    }

    @SubscribeEvent
    public static void registerEntityRendering(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.HUNGRY_CHEST.get(), HungryChestRenderer::new);
    }
}
