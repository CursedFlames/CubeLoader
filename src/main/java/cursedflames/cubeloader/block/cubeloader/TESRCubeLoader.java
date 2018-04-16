package cursedflames.cubeloader.block.cubeloader;

import cursedflames.lib.client.RenderUtil;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.model.animation.FastTESR;

public class TESRCubeLoader extends FastTESR<TileCubeLoader> {
	/*
	 * public static class TESRPolyhedronData { Light light = new Light(0, 0, 0,
	 * 1, 0, 0, 1, 64); }
	 */

	private static int tick = -1;
	public static TextureAtlasSprite texture;
	public static float u, v;

	public static void incrTick() {
		tick++;
	}

	public static void setTick(int t) {
		tick = t;
	}

	@Override
	public void renderTileEntityFast(TileCubeLoader te, double x, double y, double z,
			float partialTicks, int destroyStage, float partial, BufferBuilder buffer) {
		BlockPos pos = te.getPos();
		int mod = (((int) pos.getX()&0x4F)<<12)+(((int) pos.getY()&0x3F)<<6)
				+((int) pos.getZ()&0x4F);
		double sinMod = Math.sin(mod);
		int numCubes = te.getNumCubesInRange();
		if (numCubes<1) {
			return;
		}
		int hedron = numCubes==1 ? 1 : numCubes<28 ? 2 : numCubes<126 ? 3 : numCubes<344 ? 4 : 5;
		long time = tick+mod;
		// A bunch of things are mod Tau*division thing to prevent Math.sin
		// inaccuracies with large doubles
		double yaw = (time%(2*Math.PI*(71+sinMod*10))+partialTicks)/(71+sinMod*10);
		double yaw2 = (time%(2*Math.PI*(251+sinMod*10))+partialTicks)/(251+sinMod*10);
		double pitch = (time%(2*Math.PI*(97+sinMod*7))+partialTicks)/(97+sinMod*7);
		double scale = 0.18;
		double height = Math.sin((time%(2*Math.PI*(31+sinMod*2))+partialTicks)/(31+sinMod*2))
				*(Math.sin((time%(2*Math.PI*(600+sinMod*50))+partialTicks)/(600+sinMod*50)))*0.12;
//		Vec3d lightPos = new Vec3d(pos).addVector(0.5, height, 0.5);
//		te.poly.light.x = (float) lightPos.x;
//		te.poly.light.y = (float) lightPos.y;
//		te.poly.light.z = (float) lightPos.z;
		buffer.setTranslation(x+0.5, y+0.5, z+0.5);
		Vec3d[][] polys = hedron==1 ? RenderUtil.TetrahedronTriangles
				: hedron==2 ? RenderUtil.CubeQuads
						: hedron==3 ? RenderUtil.OctahedronTriangles
								: hedron==4 ? RenderUtil.DodecahedronPolys
										: RenderUtil.IcosahedronTriangles;
		int color = (int) Math.round(231+Math.sin(((time%(2*Math.PI*101))+partialTicks)/101)*9);
		for (int i = 0; i<polys.length; i++) {
			Vec3d[] poly = polys[i].clone();
			int l = poly.length;
			int polyColor = color
					+(int) (Math.sin(i*(i+1)+(time%(2*Math.PI*(12+((i*i)&7))))/(12+((i*i)&7)))*15);
			for (int j = 0; j<l; j++) {
				poly[j] = poly[j].scale(scale).rotateYaw((float) (yaw)).rotatePitch((float) pitch)
						.rotateYaw((float) (yaw2)).addVector(0, height, 0);
			}
			if (l==3) {
				RenderUtil.addTriToBuffer(buffer, poly[0], poly[1], poly[2], polyColor, 0, 0, 255);
			} else if (l==4) {
				RenderUtil.addQuadToBuffer(buffer, poly[0], poly[1], poly[2], poly[3], polyColor, 0,
						0, 255);
			} else if (l==5) {
				RenderUtil.addQuadToBuffer(buffer, poly[0], poly[1], poly[2], poly[3], polyColor, 0,
						0, 255);
				RenderUtil.addTriToBuffer(buffer, poly[0], poly[3], poly[4], polyColor, 0, 0, 255);
			}
		}
	}

	// what does this do?
	@Override
	public boolean isGlobalRenderer(TileCubeLoader te) {
		return true;
	}
}
