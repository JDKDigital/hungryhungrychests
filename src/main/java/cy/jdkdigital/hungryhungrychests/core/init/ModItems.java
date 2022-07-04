package cy.jdkdigital.hungryhungrychests.core.init;

import cy.jdkdigital.hungryhungrychests.HungryHungryChests;
import cy.jdkdigital.hungryhungrychests.common.entity.HungryChest;
import cy.jdkdigital.hungryhungrychests.common.item.HungryChestItem;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems
{
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, HungryHungryChests.MODID);

    public static RegistryObject<Item> HUNGRY_CHEST = ITEMS.register("hungry_chest", () -> new ForgeSpawnEggItem(() -> ModEntities.HUNGRY_CHEST.get(), 8306542, 6238757, new Item.Properties().tab(CreativeModeTab.TAB_TRANSPORTATION)));
}
