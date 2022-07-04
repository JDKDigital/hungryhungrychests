package cy.jdkdigital.hungryhungrychests.common.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Container;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandlerModifiable;

public abstract class ContainerAnimal extends TamableAnimal implements Container
{
    protected ContainerAnimal(EntityType<? extends TamableAnimal> type, Level level) {
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

    public boolean canFit(ItemStack targetItem) {
        return getContainer().map(h -> {
            for (int i = 0; i < h.getSlots(); i++) {
                ItemStack existingStack = h.getStackInSlot(i);
                if (existingStack.isEmpty() || (existingStack.getCount() < existingStack.getMaxStackSize() && existingStack.getItem().equals(targetItem.getItem()))) {
                    return true;
                }
            }
            return false;
        }).orElse(false);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        getContainer().ifPresent(inv -> {
            CompoundTag compound = ((INBTSerializable<CompoundTag>) inv).serializeNBT();
            tag.put("inv", compound);
        });
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("inv")) {
            getContainer().ifPresent(inv -> ((INBTSerializable<CompoundTag>) inv).deserializeNBT(tag.getCompound("inv")));
        }
    }

    // Copied from simple container
    public ItemStack addItem(ItemStack pStack) {
        ItemStack itemstack = pStack.copy();
        this.moveItemToOccupiedSlotsWithSameType(itemstack);
        if (itemstack.isEmpty()) {
            return ItemStack.EMPTY;
        } else {
            this.moveItemToEmptySlots(itemstack);
            return itemstack.isEmpty() ? ItemStack.EMPTY : itemstack;
        }
    }

    private void moveItemToEmptySlots(ItemStack stack) {
        for(int i = 0; i < this.getContainerSize(); ++i) {
            ItemStack itemstack = this.getItem(i);
            if (itemstack.isEmpty()) {
                this.setItem(i, stack.copy());
                stack.setCount(0);
                return;
            }
        }
    }

    private void moveItemToOccupiedSlotsWithSameType(ItemStack stack) {
        for(int i = 0; i < this.getContainerSize(); ++i) {
            ItemStack itemstack = this.getItem(i);
            if (ItemStack.isSameItemSameTags(itemstack, stack)) {
                this.moveItemsBetweenStacks(stack, itemstack);
                if (stack.isEmpty()) {
                    return;
                }
            }
        }
    }

    private void moveItemsBetweenStacks(ItemStack stack1, ItemStack stack2) {
        int i = Math.min(this.getMaxStackSize(), stack2.getMaxStackSize());
        int j = Math.min(stack1.getCount(), i - stack2.getCount());
        if (j > 0) {
            stack2.grow(j);
            stack1.shrink(j);
            this.setChanged();
        }
    }
}
