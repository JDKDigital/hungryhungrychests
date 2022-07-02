package cy.jdkdigital.hungryhungrychests.common.entity;

import net.minecraft.world.Container;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandlerModifiable;

public abstract class ContainerAnimal extends Animal implements Container
{
    protected ContainerAnimal(EntityType<? extends Animal> type, Level level) {
        super(type, level);
    }

    abstract LazyOptional<IItemHandlerModifiable> getContainer();

    @Override
    public int getContainerSize() {
        return getContainer().map(IItemHandlerModifiable::getSlots).orElse(0);
    }

    @Override
    public boolean isEmpty() {
        return getContainer().map(h -> {
            for (int i = 0; i < h.getSlots(); i++) {
                if (!h.getStackInSlot(i).isEmpty()) {
                    return false;
                }
            }
            return true;
        }).orElse(true);
    }

    @Override
    public ItemStack getItem(int slot) {
        return getContainer().map((h) -> h.getStackInSlot(slot)).orElse(ItemStack.EMPTY);
    }

    @Override
    public ItemStack removeItem(int slot, int count) {
        if (slot >= 0 && slot < getContainerSize()) {

            ItemStack currentItem = getContainer().map((h) -> h.getStackInSlot(slot)).orElse(ItemStack.EMPTY);

            if (!currentItem.isEmpty()) {
                return currentItem.split(count);
            }
        }
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        ItemStack currentItem = getContainer().map((h) -> h.getStackInSlot(slot)).orElse(ItemStack.EMPTY);
        setItem(slot, ItemStack.EMPTY);;
        return currentItem;
    }

    @Override
    public void setItem(int slot, ItemStack itemStack) {
        getContainer().ifPresent((h) -> h.setStackInSlot(slot, itemStack));
    }

    @Override
    public void setChanged() {
    }

    @Override
    public boolean stillValid(Player player) {
        return player.position().distanceTo(this.position()) < 10D;
    }

    @Override
    public void clearContent() {
        getContainer().ifPresent(h -> {
            for (int i = 0; i < h.getSlots(); i++) {
                h.setStackInSlot(i, ItemStack.EMPTY);
            }
        });
    }
}
