package cursedflames.cubeloader;

import cursedflames.cubeloader.block.cubeloader.ContainerCubeLoader;
import cursedflames.cubeloader.block.cubeloader.GuiCubeLoader;
import cursedflames.cubeloader.block.cubeloader.TileCubeLoader;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

public class GuiProxy implements IGuiHandler {
	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y,
			int z) {
		BlockPos pos = new BlockPos(x, y, z);
		TileEntity te = world.getTileEntity(pos);
		if (te instanceof TileCubeLoader) {
			return new ContainerCubeLoader(player.inventory, (TileCubeLoader) te);
		}
		return null;
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y,
			int z) {
		BlockPos pos = new BlockPos(x, y, z);
		TileEntity te = world.getTileEntity(pos);
		if (te instanceof TileCubeLoader) {
			TileCubeLoader te2 = (TileCubeLoader) te;
			return new GuiCubeLoader(te2, new ContainerCubeLoader(player.inventory, te2));
		}
		return null;
	}
}
