package cy.jdkdigital.hungryhungrychests.common.entity;

import com.google.common.collect.Sets;
import cy.jdkdigital.hungryhungrychests.HungryHungryChests;
import cy.jdkdigital.hungryhungrychests.common.container.HungryChestContainer;
import cy.jdkdigital.hungryhungrychests.common.entity.ai.HungryChestAI;
import cy.jdkdigital.hungryhungrychests.core.init.ModEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;

public class HungryChest extends ContainerAnimal
{
    private static final Set<Item> TAME_FOOD = Sets.newHashSet(Items.TRAPPED_CHEST);
    private LazyOptional<IItemHandlerModifiable> inventoryHandler;
    public ItemEntity targetItem;
    private float openAmount0;
    private float openAmount;

    public HungryChest(EntityType<? extends TamableAnimal> type, Level level) {
        super(type, level);
        inventoryHandler = LazyOptional.of(() -> new ItemStackHandler(isBaby() ? 9 : 27));
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();

        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new PanicGoal(this, 2.0D));
        this.goalSelector.addGoal(2, new BreedGoal(this, 2.0D));
        this.goalSelector.addGoal(3, new TemptGoal(this, 1.25D, Ingredient.of(Items.CHEST), false));
        this.goalSelector.addGoal(4, new FollowParentGoal(this, 1.25D));

        this.goalSelector.addGoal(5, new HungryChestAI.GetIntoOwnersBoatGoal(this));
        this.goalSelector.addGoal(6, new HungryChestAI.FindStuffOnGroundGoal(this));
        this.goalSelector.addGoal(7, new FollowOwnerGoal(this, 1.0D, 12.0F, 6.0F, false));
        this.goalSelector.addGoal(8, new HungryChestAI.CollectFromInventoryGoal(this));
        this.goalSelector.addGoal(9, new HungryChestAI.OffloadToInventoryGoal(this));


        this.goalSelector.addGoal(10, new WaterAvoidingRandomStrollGoal(this, 1.0D));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 40.0D).add(Attributes.MOVEMENT_SPEED, 0.4F).add(Attributes.FOLLOW_RANGE, 48.0D);
    }

    @Override
    public void tick() {
        super.tick();

        updateLid();
    }

    private void updateLid() {
        this.openAmount0 = this.openAmount;
        if (this.getDeltaMovement().horizontalDistance() > 0.01F || isOpen()) {
            this.openAmount = Math.min(1.0F, this.openAmount + 0.15F);
        } else {
            this.openAmount = Math.max(0.0F, this.openAmount - 0.19F);
        }
    }

    public float getLidAmount(float partialTicks) {
        return Mth.lerp(partialTicks, this.openAmount0, this.openAmount);
    }

    @Override
    LazyOptional<IItemHandlerModifiable> getContainer() {
        return inventoryHandler;
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob parent) {
        var offspring = ModEntities.HUNGRY_CHEST.get().create(level);

        if (offspring != null && this.isTame()) {
            offspring.setOwnerUUID(this.getOwnerUUID());
            offspring.setTame(true);
        }

        return offspring;
    }

    @Override
    public boolean isFood(ItemStack pStack) {
        return pStack.getItem().equals(Items.CHEST);
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        // Tame
        ItemStack itemstack = player.getItemInHand(hand);
        if (!this.isTame() && TAME_FOOD.contains(itemstack.getItem())) {
            if (!player.getAbilities().instabuild) {
                itemstack.shrink(1);
            }

            if (!this.level.isClientSide) {
                if (this.random.nextInt(5) == 0 && !net.minecraftforge.event.ForgeEventFactory.onAnimalTame(this, player)) {
                    this.tame(player);
                    this.level.broadcastEntityEvent(this, (byte)7);
                } else {
                    this.level.broadcastEntityEvent(this, (byte)6);
                }
            }
        } else {
            this.openInventory(player);
        }
        return InteractionResult.sidedSuccess(this.level.isClientSide);
    }

    public void openInventory(Player player) {
        if (!this.level.isClientSide) {
            var containerProvider = new MenuProvider() {
                @Override
                public @NotNull Component getDisplayName() {
                    return new TranslatableComponent(HungryHungryChests.MODID + ".hungrychest.container.title");
                }

                @Override
                public AbstractContainerMenu createMenu(int windowId, Inventory playerInventory, Player playerEntity) {
                    if (isBaby()) {
                        return HungryChestContainer.singleBaby(windowId, playerInventory, HungryChest.this);
                    }
                    return HungryChestContainer.single(windowId, playerInventory, HungryChest.this);
                }
            };
            NetworkHooks.openGui((ServerPlayer) player, containerProvider);
        }
    }

    public List<ItemEntity> getItemsNearby(BlockPos pos, double distance) {
        return level.getEntitiesOfClass(ItemEntity.class, (new AABB(pos).inflate(distance, 3, distance)));
    }

    public boolean isOpen() {
        return false; // TODO track if any number of players has the chest open
    }

    public void destroy(DamageSource pSource) {
        if (this.level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
            Containers.dropContents(this.level, this, this);
            if (!this.level.isClientSide) {
                Entity entity = pSource.getDirectEntity();
                if (entity != null && entity.getType() == EntityType.PLAYER) {
                    PiglinAi.angerNearbyPiglins((Player)entity, true);
                }
            }
        }
    }
}
