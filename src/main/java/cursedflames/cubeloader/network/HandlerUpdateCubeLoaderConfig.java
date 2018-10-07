package cursedflames.cubeloader.network;

import cursedflames.cubeloader.block.cubeloader.TileCubeLoader;
import cursedflames.lib.Util;
import cursedflames.lib.network.NBTPacket;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class HandlerUpdateCubeLoaderConfig {
	public static void handleMessage(NBTPacket message, MessageContext ctx) {
		NBTTagCompound tag = message.getTag();
		if (!tag.hasKey("pos"))
			return;
		EntityPlayer player = ctx.getServerHandler().player;
		World world = player.getEntityWorld();
		BlockPos pos = Util.blockPosFromNBT(tag.getCompoundTag("pos"));
		if (world.isBlockLoaded(pos)) {
			TileEntity te = world.getTileEntity(pos);
			if (te instanceof TileCubeLoader) {
				TileCubeLoader loader = (TileCubeLoader) te;
				if (tag.hasKey("xRange")&&tag.hasKey("yRange")&&tag.hasKey("zRange"))
					loader.setRange(tag.getInteger("xRange"), tag.getInteger("yRange"),
							tag.getInteger("zRange"));
				if (tag.hasKey("disabled"))
					loader.setDisabled(tag.getBoolean("disabled"));
			}
		}
	}
}