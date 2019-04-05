package cursedflames.cubeloader;

import org.apache.logging.log4j.Logger;

import cursedflames.cubeloader.block.ModBlocks;
import cursedflames.cubeloader.config.Config;
import cursedflames.cubeloader.event.CLEventHandler;
import cursedflames.cubeloader.network.PacketHandler;
import cursedflames.cubeloader.proxy.ISideProxy;
import cursedflames.lib.RegistryHelper;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Mod(modid = CubeLoader.MODID, useMetadata = true)
@Mod.EventBusSubscriber
public class CubeLoader {
	public static final String MODID = "cubeloader";

	public static final RegistryHelper registryHelper = new RegistryHelper(MODID);

	public static final CreativeTabs TAB_CUBELOADER = new CreativeTabs("cubeloader") {
		@SideOnly(Side.CLIENT)
		public ItemStack getTabIconItem() {
			return new ItemStack(Item.getItemFromBlock(ModBlocks.cubeLoader));
		}
	};

	public static Configuration config;
	public static Logger logger;

	@Mod.Instance
	public static CubeLoader instance;

	@SidedProxy(clientSide = "cursedflames.cubeloader.proxy.ClientProxy", serverSide = "cursedflames.cubeloader.proxy.ServerProxy")
	public static ISideProxy proxy;

	@EventHandler
	public void preInit(FMLPreInitializationEvent e) {
		logger = e.getModLog();
		PacketHandler.registerMessages();
		Config.preInit(e);
		MinecraftForge.EVENT_BUS.register(CLEventHandler.class);
	}

	@EventHandler
	public void init(FMLInitializationEvent e) {
		NetworkRegistry.INSTANCE.registerGuiHandler(CubeLoader.instance, new GuiProxy());
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent e) {
		Config.postInit(e);
	}

	@SubscribeEvent
	public static void registerBlocks(RegistryEvent.Register<Block> event) {
		ModBlocks.registerBlocksToRegistryHelperForRegistration();
		registryHelper.registerBlocks(event);
	}

	@SubscribeEvent
	public static void registerItems(RegistryEvent.Register<Item> event) {
		registryHelper.registerItems(event);
	}
}
