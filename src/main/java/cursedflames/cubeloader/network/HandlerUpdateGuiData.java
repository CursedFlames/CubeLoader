package cursedflames.cubeloader.network;

import cursedflames.cubeloader.block.cubeloader.ContainerCubeLoader;
import cursedflames.lib.network.NBTPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.inventory.Container;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class HandlerUpdateGuiData {
	public static void handleMessage(NBTPacket message, MessageContext ctx) {
		NBTTagCompound tag = message.getTag();
		Container container = Minecraft.getMinecraft().player.openContainer;
		if (container instanceof ContainerCubeLoader) {
			((ContainerCubeLoader) container).readChanges(tag);
		}
	}
}