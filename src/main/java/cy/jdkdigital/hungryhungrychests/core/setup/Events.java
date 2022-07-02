package cy.jdkdigital.hungryhungrychests.core.setup;

import cy.jdkdigital.hungryhungrychests.HungryHungryChests;
import cy.jdkdigital.hungryhungrychests.common.entity.HungryChest;
import cy.jdkdigital.hungryhungrychests.core.init.ModEntities;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = HungryHungryChests.MODID)
public class Events
{
    @SubscribeEvent
    public static void onEntityAttributeCreate(EntityAttributeCreationEvent event) {
        event.put(ModEntities.HUNGRY_CHEST.get(), HungryChest.createAttributes().build());
    }
}
