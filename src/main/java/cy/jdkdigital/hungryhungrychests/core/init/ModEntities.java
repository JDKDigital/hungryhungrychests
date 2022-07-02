package cy.jdkdigital.hungryhungrychests.core.init;

import cy.jdkdigital.hungryhungrychests.HungryHungryChests;
import cy.jdkdigital.hungryhungrychests.common.entity.HungryChest;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEntities
{
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITIES, HungryHungryChests.MODID);

    public static RegistryObject<EntityType<HungryChest>> HUNGRY_CHEST = ENTITIES.register("hungry_chest", () -> EntityType.Builder.of(HungryChest::new, MobCategory.CREATURE).clientTrackingRange(8).build(HungryHungryChests.MODID + ":hungry_chest"));
}
