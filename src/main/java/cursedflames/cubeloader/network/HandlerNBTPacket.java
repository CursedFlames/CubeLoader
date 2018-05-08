package cursedflames.cubeloader.network;

import cursedflames.cubeloader.network.PacketHandler.HandlerIds;
import cursedflames.lib.network.NBTPacket;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

//TODO move this to CursedLib
public class HandlerNBTPacket implements IMessageHandler<NBTPacket, IMessage> {
	@Override
	public IMessage onMessage(NBTPacket message, MessageContext ctx) {
		FMLCommonHandler.instance().getWorldThread(ctx.netHandler)
				.addScheduledTask(() -> handleMessage(message, ctx));
		return null;
	}

	private void handleMessage(NBTPacket message, MessageContext ctx) {
		NBTTagCompound tag = message.getTag();
		int id = tag.getByte("id");
		if (id==HandlerIds.SYNC_SERVER_DATA.id) {
			HandlerSyncServerData.handleMessage(message, ctx);
		} else if (id==HandlerIds.UPDATE_CUBELOADER_CONFIG.id) {
			HandlerUpdateCubeLoaderConfig.handleMessage(message, ctx);
		} else if (id==HandlerIds.UPDATE_GUI_DATA.id) {
			HandlerUpdateGuiData.handleMessage(message, ctx);
		}
	}
}
