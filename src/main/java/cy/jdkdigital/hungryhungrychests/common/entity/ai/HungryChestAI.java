package cy.jdkdigital.hungryhungrychests.common.entity.ai;

import cy.jdkdigital.hungryhungrychests.HungryHungryChests;
import cy.jdkdigital.hungryhungrychests.common.entity.HungryChest;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.w3c.dom.Entity;

import java.util.List;

public class HungryChestAI
{
    private static abstract class HungryChestGoal extends Goal {
        protected HungryChest chestEntity;

        public HungryChestGoal(HungryChest chestEntity) {
            this.chestEntity = chestEntity;
        }
    }
    
    // Find shit on the ground goal
    public static class FindStuffOnGroundGoal extends HungryChestGoal
    {
        private int ticks = 0;

        public FindStuffOnGroundGoal(HungryChest chestEntity) {
            super(chestEntity);
        }

        @Override
        public boolean canUse() {
            List<ItemEntity> items = this.chestEntity.getItemsNearby(this.chestEntity.blockPosition(), 15);

            if (!items.isEmpty()) {
                ItemEntity nearestItem = null;
                double nearestItemDistance = -1;
                int i = 0;
                for (ItemEntity item : items) {
                    // Check if inventory has space for item
                    if (item != null && this.chestEntity.canFit(item.getItem())) {
                        BlockPos itemLocation = new BlockPos(item.getX(), item.getY(), item.getZ());
                        double distance = itemLocation.distSqr(this.chestEntity.blockPosition());
                        if (nearestItemDistance == -1 || distance < nearestItemDistance) {
                            nearestItemDistance = distance;
                            nearestItem = item;
                        }
                    }

                    // Don't look at more than 20 items
                    if (++i > 20) {
                        break;
                    }
                }

                if (nearestItem != null) {
                    this.chestEntity.targetItem = nearestItem;
                    HungryHungryChests.LOGGER.info("found new item " + this.chestEntity.targetItem);

                    return true;
                }
            }
            return false;
        }

        @Override
        public boolean canContinueToUse() {
            return (this.chestEntity.targetItem != null && this.chestEntity.canFit(this.chestEntity.targetItem.getItem())) || canUse();
        }

        @Override
        public void start() {
            ticks = 0;
        }

        @Override
        public void stop() {
            ticks = 0;
        }

        @Override
        public void tick() {
            ++ticks;
            if (this.chestEntity.targetItem != null) {
                if (ticks > 600) {
                    this.chestEntity.targetItem = null;
                } else {
                    Vec3 vec3d = Vec3.atCenterOf(this.chestEntity.targetItem.blockPosition()).add(0.0D, 0.6F, 0.0D);
                    double distanceToTarget = vec3d.distanceTo(this.chestEntity.position());

                    if (distanceToTarget > 1.2D) {
                        this.moveToNextTarget(vec3d);
                    } else {
                        if (distanceToTarget > 0.5D && ticks > 600) {
                            this.chestEntity.targetItem = null;
                        } else {
                            // Pick up item
                            if (this.chestEntity.targetItem != null) {
                                ItemStack itemstack = this.chestEntity.targetItem.getItem().copy();

                                ItemStack remaining = this.chestEntity.addItem(itemstack);
                                if (remaining.isEmpty()) {
                                    HungryHungryChests.LOGGER.info("remaining is empty " + remaining);
                                    this.chestEntity.targetItem.discard();
                                } else {
                                    HungryHungryChests.LOGGER.info("remaining is not empty " + remaining);
                                    this.chestEntity.targetItem.setItem(remaining);
                                }

                                this.chestEntity.targetItem = null;

                                this.chestEntity.playSound(SoundEvents.BEEHIVE_ENTER, 0.2F, 1.0F);
                            }
                        }
                    }
                }
            }
        }

        private void moveToNextTarget(Vec3 nextTarget) {
            this.chestEntity.getMoveControl().setWantedPosition(nextTarget.x, nextTarget.y, nextTarget.z, 1.0F);
        }
    }

    // Get in owners boat goal
    public static class GetIntoOwnersBoatGoal extends HungryChestGoal
    {
        private Player owner;
        private int timeToRecalcPath;

        public GetIntoOwnersBoatGoal(HungryChest chestEntity) {
            super(chestEntity);
        }

        @Override
        public boolean canUse() {
            if (!this.chestEntity.isPassenger() && this.chestEntity.isTame() && this.chestEntity.getOwner() instanceof Player owner && owner.getVehicle() instanceof Boat) {
                this.owner = owner;
                return true;
            }
            if (this.owner != null && !this.owner.isPassenger() && this.chestEntity.isPassenger()) {
                this.chestEntity.stopRiding();
            }
            return false;
        }

        @Override
        public void tick() {
            this.chestEntity.getLookControl().setLookAt(this.owner, 10.0F, (float)this.chestEntity.getMaxHeadXRot());
            if (--this.timeToRecalcPath <= 0) {
                this.timeToRecalcPath = this.adjustedTickDelay(10);
                if (!this.chestEntity.isLeashed() && !this.chestEntity.isPassenger()) {
                    if (this.chestEntity.position().distanceTo(this.owner.position()) < 2D && this.owner.getVehicle() instanceof Boat) {
                        this.chestEntity.startRiding(this.owner.getVehicle());
                    } else {
                        this.chestEntity.getNavigation().moveTo(this.owner, 1.0F);
                    }
                }
            }
        }
    }

    // Pick up from designated source inventory
    public static class CollectFromInventoryGoal extends HungryChestGoal
    {
        public CollectFromInventoryGoal(HungryChest chestEntity) {
            super(chestEntity);
        }

        @Override
        public boolean canUse() {
            return false;
        }
    }

    // Drop off items in designated offload inventory
    public static class OffloadToInventoryGoal extends HungryChestGoal
    {
        public OffloadToInventoryGoal(HungryChest chestEntity) {
            super(chestEntity);
        }

        @Override
        public boolean canUse() {
            return false;
        }
    }
}
