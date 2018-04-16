package cursedflames.cubeloader.network;

import cursedflames.cubeloader.CubeLoader;
import cursedflames.lib.network.NBTPacket;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

//TODO generic Handler class that calls a handler func based on packet type enum
public class PacketHandler {
	private static int id = 0;
	public static final SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE
			.newSimpleChannel(CubeLoader.MODID);

	public static void registerMessages() {
		INSTANCE.registerMessage(HandlerSyncData.class, NBTPacket.class, id++, Side.CLIENT);
		INSTANCE.registerMessage(HandlerUpdateCubeLoaderConfig.class, NBTPacket.class, id++,
				Side.SERVER);
	}
}
