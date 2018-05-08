package cursedflames.cubeloader.event;

import java.util.UUID;

import cursedflames.cubeloader.block.cubeloader.TESRCubeLoader;
import cursedflames.cubeloader.chunkloading.ChunkloaderManager;
import cursedflames.cubeloader.config.Config;
import cursedflames.cubeloader.network.PacketHandler;
import cursedflames.cubeloader.network.PacketHandler.HandlerIds;
import cursedflames.cubeloader.proxy.CommonProxy;
import cursedflames.lib.network.NBTPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class CLEventHandler {
	@SubscribeEvent(priority = EventPriority.LOWEST)
	public static void worldLoaded(WorldEvent.Load event) {
		// CommonProxy.logger.info("worldLoaded");
		World world = event.getWorld();
		if (world==null||world.isRemote)
			return;
		ChunkloaderManager manager = ChunkloaderManager.getInstance(world);
		if (manager!=null)
			manager.reloadChunkloaders();
	}

	@SubscribeEvent
	public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
		if (!event.player.world.isRemote) {
			CommonProxy.logger.info("Attempting to sync config");
			NBTTagCompound tag = Config.getSyncTag();
			tag.setInteger("time", event.player.world.getMinecraftServer().getTickCounter());
			PacketHandler.INSTANCE.sendTo(new NBTPacket(tag, HandlerIds.SYNC_SERVER_DATA.id),
					(EntityPlayerMP) event.player);
			ChunkloaderManager manager = ChunkloaderManager.getInstance(event.player.world);
			String name = event.player.getName();
			UUID id = event.player.getUniqueID();
			if (name!=manager.getPlayerName(id)) {
				manager.setPlayerName(id, name);
			}
		}
	}

	@SubscribeEvent
	public static void clientTick(TickEvent.ClientTickEvent event) {
		if (event.phase==TickEvent.Phase.END&&!Minecraft.getMinecraft().isGamePaused()) {
			TESRCubeLoader.incrTick();
		}
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public static void textureRegister(TextureStitchEvent.Pre event) {
//		TESRCubeLoader.texture = event.getMap()
//				.registerSprite(new ResourceLocation(CubeLoader.MODID, "blocks/polyhedron"));
//		TESRCubeLoader.u = TESRCubeLoader.texture.getMinU();
//		TESRCubeLoader.v = TESRCubeLoader.texture.getMinV();
	}
}
