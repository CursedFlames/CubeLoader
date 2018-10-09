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
	public String translateWithArgs(String string, Object... args) {
		return I18n.format(string, args);
	}

	// TODO escape formatting properly instead of doing this
	@SuppressWarnings("deprecation")
	@Override
	public String translate(String string) {
		return net.minecraft.util.text.translation.I18n.translateToLocal(string);
	}

	@Override
	public boolean hasTranslationKey(String string) {
		return I18n.hasKey(string);
	}
}