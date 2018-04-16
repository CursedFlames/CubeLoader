package cursedflames.cubeloader.block;

import cursedflames.cubeloader.block.cubeloader.BlockCubeLoader;
import cursedflames.cubeloader.block.unloaddetector.BlockUnloadDetector;
import net.minecraft.block.Block;

public class ModBlocks {
	public static Block cubeLoader;

	public static void registerBlocksToRegistryHelperForRegistration() {
		cubeLoader = new BlockCubeLoader();
		new BlockUnloadDetector();
	}
}
