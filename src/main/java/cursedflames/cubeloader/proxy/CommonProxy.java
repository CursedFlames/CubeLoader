package cursedflames.cubeloader.proxy;

import org.apache.logging.log4j.Logger;

import cursedflames.cubeloader.CubeLoader;
import cursedflames.cubeloader.GuiProxy;
import cursedflames.cubeloader.config.Config;
import cursedflames.cubeloader.event.CLEventHandler;
import cursedflames.cubeloader.network.PacketHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;

public class CommonProxy {
	public static Configuration config;
	public static Logger logger;

	public void preInit(FMLPreInitializationEvent e) {
		logger = e.getModLog();
		PacketHandler.registerMessages();
		Config.preInit(e);
		MinecraftForge.EVENT_BUS.register(CLEventHandler.class);
	}

	public void init(FMLInitializationEvent e) {
		NetworkRegistry.INSTANCE.registerGuiHandler(CubeLoader.instance, new GuiProxy());
	}

	public void postInit(FMLPostInitializationEvent e) {
		Config.postInit(e);
	}

	public String format(String translateKey, Object... parameters) {
		// Why does Minecraft localize item names, etc. serverside anyway
		// That doesn't make any sense
		return translateKey;
	}
}
