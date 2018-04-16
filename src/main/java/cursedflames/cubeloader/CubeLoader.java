package cursedflames.cubeloader;

import cursedflames.cubeloader.block.ModBlocks;
import cursedflames.cubeloader.proxy.CommonProxy;
import cursedflames.lib.RegistryHelper;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Mod(modid = CubeLoader.MODID, name = CubeLoader.MODNAME, version = CubeLoader.VERSION, useMetadata = true)
@Mod.EventBusSubscriber
public class CubeLoader {
	public static final String MODNAME = "Cube Loader";
	public static final String MODID = "cubeloader";
	public static final String VERSION = "0.0.0";

	public static final RegistryHelper registryHelper = new RegistryHelper(MODID);

	public static final CreativeTabs TAB_CUBELOADER = new CreativeTabs("cubeloader") {
		@SideOnly(Side.CLIENT)
		public ItemStack getTabIconItem() {
			return new ItemStack(Item.getItemFromBlock(ModBlocks.cubeLoader));
		}
	};

	@Mod.Instance
	public static CubeLoader instance;

	@SidedProxy(clientSide = "cursedflames.cubeloader.proxy.ClientProxy", serverSide = "cursedflames.cubeloader.proxy.CommonProxy")
	public static CommonProxy proxy;

	@EventHandler
	public void preInit(FMLPreInitializationEvent e) {
		proxy.preInit(e);
	}

	@EventHandler
	public void init(FMLInitializationEvent e) {
		proxy.init(e);
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent e) {
		proxy.postInit(e);
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
