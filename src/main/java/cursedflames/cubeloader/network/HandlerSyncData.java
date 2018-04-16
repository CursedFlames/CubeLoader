package cursedflames.cubeloader.network;

import cursedflames.cubeloader.block.cubeloader.TESRCubeLoader;
import cursedflames.cubeloader.config.Config;
import cursedflames.lib.network.NBTPacket;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

//TODO some sort of generic message class
public class HandlerSyncData implements IMessageHandler<NBTPacket, IMessage> {
	@Override
	public IMessage onMessage(NBTPacket message, MessageContext ctx) {
		FMLCommonHandler.instance().getWorldThread(ctx.netHandler)
				.addScheduledTask(() -> handleMessage(message, ctx));
		return null;
	}

	private void handleMessage(NBTPacket message, MessageContext ctx) {
		NBTTagCompound tag = message.getTag();
		Config.SyncedConfig.loadSyncTag(tag);
		if (tag.hasKey("time")) {
			TESRCubeLoader.setTick(tag.getInteger("time"));
		}
	}
}
