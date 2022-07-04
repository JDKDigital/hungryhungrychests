package cy.jdkdigital.hungryhungrychests.client.render.entity;

import com.google.common.collect.ImmutableList;
import cy.jdkdigital.hungryhungrychests.common.entity.HungryChest;
import net.minecraft.client.model.AgeableListModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

public class HungryChestModel<E extends HungryChest> extends AgeableListModel<E>
{
    public final ModelPart base;
    public final ModelPart rightLeg;
    public final ModelPart leftLeg;
    public final ModelPart bottom;
    public final ModelPart lid;
    public final ModelPart lock;

    private final float CLOSED = (float)Math.PI;
    private final float SLIGHT_OPEN = -0.5F * (float)Math.PI * 0.2F + (float)Math.PI;
    private final float VERY_OPEN = -0.5F * (float)Math.PI * 1.0F + (float)Math.PI;

    public HungryChestModel(ModelPart model) {
        super(true, 24.0F, 0.0F, 3.0F, 2.0F, 24.0F);
        this.base = model;
        this.rightLeg = model.getChild("right_leg");
        this.leftLeg = model.getChild("left_leg");
        this.bottom = model.getChild("bottom");
        this.lid = model.getChild("lid");
        this.lock = this.lid.getChild("lock");
    }

    public static LayerDefinition createBodyLayer() {
        return createSingleBodyLayer();
    }

    @Override
    public void setupAnim(E entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.leftLeg.xRot = Mth.sin(limbSwing * 1.5F * 0.5F) * 2.0F * limbSwingAmount;
        this.rightLeg.xRot = Mth.sin(limbSwing * 1.5F * 0.5F + (float)Math.PI) * 2.0F * limbSwingAmount;
        this.leftLeg.zRot = 0.17453292F * Mth.cos(limbSwing * 1.5F * 0.5F) * limbSwingAmount;
        this.rightLeg.zRot = 0.17453292F * Mth.cos(limbSwing * 1.5F * 0.5F + (float)Math.PI) * limbSwingAmount;
    }

    @Override
    public void prepareMobModel(E entity, float limbSwing, float limbSwingAmount, float partialTick) {
        super.prepareMobModel(entity, limbSwing, limbSwingAmount, partialTick);

        this.lid.xRot = Mth.lerp(entity.getLidAmount(partialTick), CLOSED, entity.isOpen() ? VERY_OPEN : SLIGHT_OPEN);
    }

    protected @NotNull Iterable<ModelPart> headParts() {
        return ImmutableList.of(this.lid);
    }

    protected @NotNull Iterable<ModelPart> bodyParts() {
        return ImmutableList.of(this.bottom, this.rightLeg, this.leftLeg);
    }

    public static LayerDefinition createSingleBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        partdefinition.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(15, 24).addBox(1.0F, 0.0F, -1.0F, 3.0F, 7.0F, 3.0F), PartPose.offset(0.0F, 18.0F, 0.0F));
        partdefinition.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(15, 24).addBox(-4.0F, 0.0F, -1.0F, 3.0F, 7.0F, 3.0F), PartPose.offset(0.0F, 18.0F, 0.0F));
        partdefinition.addOrReplaceChild("bottom", CubeListBuilder.create().texOffs(0, 19).addBox(-15.0F, 0.0F, 1.0F, 14.0F, 10.0F, 14.0F), PartPose.offsetAndRotation(8.0F, 18.0F, 8.0F, 3.1416F, 0.0F, 0.0F));
        PartDefinition lid = partdefinition.addOrReplaceChild("lid", CubeListBuilder.create().texOffs(0, 0).addBox(-15.0F, 0.0F, 0.0F, 14.0F, 5.0F, 14.0F), PartPose.offsetAndRotation(8.0F, 9.0F, 7.0F, 3.1416F, 0.0F, 0.0F));
        lid.addOrReplaceChild("lock", CubeListBuilder.create().texOffs(0, 0).addBox(-9.0F, -1.0F, 15.0F, 2.0F, 4.0F, 1.0F), PartPose.offset(0.0F, 0.0F, -1.0F));

        return LayerDefinition.create(meshdefinition, 64, 64);
    }
}
