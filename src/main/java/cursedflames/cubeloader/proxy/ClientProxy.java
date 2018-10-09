package cursedflames.cubeloader.proxy;

import cursedflames.cubeloader.CubeLoader;
import cursedflames.cubeloader.block.cubeloader.TESRCubeLoader;
import cursedflames.cubeloader.block.cubeloader.TileCubeLoader;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

@Mod.EventBusSubscriber(Side.CLIENT)
public class ClientProxy implements ISideProxy {
	@SubscribeEvent
	public static void registerModels(ModelRegistryEvent event) {
		CubeLoader.registryHelper.registerModels();
		ClientRegistry.bindTileEntitySpecialRenderer(TileCubeLoader.class, new TESRCubeLoader());
	}

	@Override
	public String format(String translateKey, Object... parameters) {
		return I18n.format(translateKey, parameters);
	}
}