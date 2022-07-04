package cy.jdkdigital.hungryhungrychests.common.container;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.MenuType;

public class HungryChestContainer extends ChestMenu
{
    private HungryChestContainer(MenuType<?> menu, int id, Inventory inventory, Container container, int rows) {
        super(menu, id, inventory, container, rows);
    }

    public static HungryChestContainer single(int id, Inventory inventory, Container container) {
        return new HungryChestContainer(MenuType.GENERIC_9x3, id, inventory, container, 3);
    }

    public static HungryChestContainer singleBaby(int id, Inventory inventory, Container container) {
        return new HungryChestContainer(MenuType.GENERIC_9x1, id, inventory, container, 1);
    }
}
