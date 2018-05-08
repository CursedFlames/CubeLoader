package cursedflames.cubeloader.network;

import cursedflames.cubeloader.block.cubeloader.TESRCubeLoader;
import cursedflames.cubeloader.config.Config;
import cursedflames.lib.network.NBTPacket;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class HandlerSyncServerData {
	public static void handleMessage(NBTPacket message, MessageContext ctx) {
		NBTTagCompound tag = message.getTag();
		Config.SyncedConfig.loadSyncTag(tag);
		if (tag.hasKey("time")) {
			TESRCubeLoader.setTick(tag.getInteger("time"));
		}
	}
}