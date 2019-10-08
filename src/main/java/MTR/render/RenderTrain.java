package mtr.render;

import mtr.MathTools;
import mtr.entity.EntityTrain;
import mtr.entity.EntityTrain.EnumTrainType;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelMinecart;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

// https://wiki.mcjty.eu/modding/index.php/Render_Block_TESR_/_OBJ-1.9
@SideOnly(Side.CLIENT)
public abstract class RenderTrain<T extends EntityTrain> extends Render<T> {

	protected ModelBase modelBogie = new ModelMinecart();

	public RenderTrain(RenderManager renderManager) {
		super(renderManager);
	}

	@Override
	public void doRender(T entity, double x, double y, double z, float entityYaw, float partialTicks) {
		GlStateManager.pushMatrix();
		GlStateManager.translate(x, y + 0.375, z);
		GlStateManager.rotate(-entity.rotationYaw, 0, 1, 0);
		GlStateManager.rotate(180 + entity.rotationPitch, 0, 0, 1);
		bindTexture(new ResourceLocation("textures/entity/minecart.png"));
		modelBogie.render(entity, 0, 0, -0.1F, 0, 0, 0.0625F);
		GlStateManager.popMatrix();

		super.doRender(entity, x, y, z, entityYaw, partialTicks);

		final T entitySibling = (T) entity.world.getEntityByID(entity.getSiblingIDClient());
		if (entitySibling == null)
			return;
		final double x1 = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * partialTicks;
		final double y1 = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * partialTicks;
		final double z1 = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * partialTicks;
		final double x2 = entitySibling.lastTickPosX + (entitySibling.posX - entitySibling.lastTickPosX) * partialTicks;
		final double y2 = entitySibling.lastTickPosY + (entitySibling.posY - entitySibling.lastTickPosY) * partialTicks;
		final double z2 = entitySibling.lastTickPosZ + (entitySibling.posZ - entitySibling.lastTickPosZ) * partialTicks;
		final double midX = (x2 - x1) / 2D;
		final double midY = (y2 - y1) / 2D;
		final double midZ = (z2 - z1) / 2D;
		final double angleYaw = MathTools.angleBetweenPoints(x2, z2, x1, z1);
		final double anglePitch = Math.asin((y1 - y2) / MathTools.distanceBetweenPoints(x1, z1, x2, z2));
		final int trainType = entity.getTrainTypeClient();

		GlStateManager.pushMatrix();
		GlStateManager.translate(x, y + 1.5, z);
		GlStateManager.translate(midX, midY, midZ);
		GlStateManager.rotate((float) Math.toDegrees(angleYaw) + (trainType < 0 ? 180 : 0), 0, 1, 0);
		GlStateManager.rotate((float) Math.toDegrees(anglePitch) * Math.signum(trainType), 1, 0, 0);
		bindEntityTexture(entity);
		GlStateManager.scale(-1, -1, 1);
		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240, 240);
		final float leftDoor = entity.getLeftDoor() / 60F;
		final float rightDoor = entity.getRightDoor() / 60F;
		render(entity, EnumTrainType.getByDirection(trainType), leftDoor, rightDoor);
		GlStateManager.popMatrix();
	}

	protected abstract void render(T entity, EnumTrainType trainType, float leftDoor, float rightDoor);
}
