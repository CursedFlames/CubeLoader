package cursedflames.cubeloader.config;

import java.io.File;

import org.apache.logging.log4j.Level;

import cursedflames.cubeloader.proxy.CommonProxy;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

//TODO find a better way to store synced config data
//TODO make data sync when playing LAN multiplayer
//TODO put most of this into an abstract Config class in CursedLib?
//TODO maybe try using @Config annotations instead?
public class Config {
	public static Configuration configuration;
	private static boolean configReset = false;

	public static void preInit(FMLPreInitializationEvent e) {
		File directory = e.getModConfigurationDirectory();
		File configFile = new File(directory.getPath(), "cubeloader.cfg");
		configuration = new Configuration(configFile, "1");
		if (configuration.hasKey("general", "version")) {
			String version = configuration.get("general", "version", "").getString();
			if (version.equals("0")) {
				try {
					CommonProxy.logger.info("Deleting old config file, properties will be saved.");
					if (configuration.hasKey("general", "maxCubesLoaded")) {
						maxCubesLoaded = configuration.get("general", "maxCubesLoaded", -2)
								.getInt();
					}
					configFile.delete();
					configReset = true;
				} catch (Error error) {
					CommonProxy.logger.warn("Failed to delete old config file");
					CommonProxy.logger.catching(Level.WARN, error);
				}
			}
		}
		Config.readConfig();
	}

	public static void postInit(FMLPostInitializationEvent e) {
		if (configuration.hasChanged()) {
			configuration.save();
		}
	}

	public static void readConfig() {
		try {
			configuration.load();
			initConfig();
		} catch (Exception e) {
			CommonProxy.logger.error("Failed to load config: ", e);
		} finally {
			if (configuration.hasChanged()||configReset) {
				configuration.save();
			}
		}
	}

	// TODO dimension whitelist/blacklist
	// TODO figure out whether adminLoaderIn... will work in multiplayer
//	public static boolean adminLoaderInCreativeMenu;
	public static int maxCubesLoaded = -2;
	public static boolean fueled = false;

	public static void initConfig() {
//		adminLoaderInCreativeMenu = configuration.get("general", "adminLoaderInCreativeMenu", false,
//				"Whether or not admin loaders are listed in the creative menu. As there is no "
//						+"limit to the number of cubes loaded by admin loaders, it is "
//						+"reccomended to disable this on servers where non-ops can have "
//						+"creative mode.")
//				.getBoolean();
		// maxCubesLoaded defaults to -2, if it's something else, an old config
		// must have been loaded and deleted
		maxCubesLoaded = configuration.get("general", "maxCubesLoaded",
				maxCubesLoaded==-2 ? 1600 : maxCubesLoaded,
				"The maximum number of cubes that a player can load. -1 for infinite.", -1, 1000000)
				.getInt();
		fueled = configuration
				.get("general", "fueled", false,
						"Whether or not cubeloaders require fuel to run. Fuel items are specified in the fuel json file in the config directory.")
				.getBoolean();
	}

	public static NBTTagCompound getSyncTag() {
		NBTTagCompound tag = new NBTTagCompound();
//		tag.setBoolean("adminLoaderInCreativeMenu", adminLoaderInCreativeMenu);
		tag.setInteger("maxCubesLoaded", maxCubesLoaded);
		tag.setBoolean("fueled", fueled);
		return tag;
	}

	public static class SyncedConfig {
		public static SyncedConfig INSTANCE;

//		public boolean adminLoaderInCreativeMenu;
		public int maxCubesLoaded;
		public boolean fueled;

		public static SyncedConfig loadSyncTag(NBTTagCompound tag) {
			try {
				SyncedConfig newInstance = new SyncedConfig();
//				newInstance.adminLoaderInCreativeMenu = tag.getBoolean("adminLoaderInCreativeMenu");
				newInstance.maxCubesLoaded = tag.getInteger("maxCubesLoaded");
				newInstance.fueled = tag.getBoolean("fueled");
				INSTANCE = newInstance;
				// CommonProxy.logger.info("Synced"+newInstance.maxCubesLoaded);
				return newInstance;
			} catch (Error e) {
				CommonProxy.logger.error("Failed to load sync tag, keeping old SyncedConfig", e);
				return INSTANCE;
			}
		}
	}
}
