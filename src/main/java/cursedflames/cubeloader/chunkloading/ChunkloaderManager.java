package cursedflames.cubeloader.chunkloading;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

import cursedflames.cubeloader.CubeLoader;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapStorage;
import net.minecraft.world.storage.WorldSavedData;

//TODO fix chunk unload on singleplayer dimension change
public class ChunkloaderManager extends WorldSavedData {
	public static final String DATA_NAME = CubeLoader.MODID+"_ChunkloaderManager";
	private static ChunkloaderManager INSTANCE;
	private Map<UUID, PlayerChunkloaders> loaders = new HashMap<>();
	private Map<UUID, String> playerNames = new HashMap<>();

	public ChunkloaderManager() {
		super(DATA_NAME);
	}

	// Apparently this constructor is required?
	public ChunkloaderManager(String s) {
		super(s);
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		if (!tag.hasKey("chunkloaders"))
			return;
		NBTTagCompound tag1 = tag.getCompoundTag("chunkloaders");
		for (int i = 0; tag1.hasKey(String.valueOf(i)); i++) {
			NBTTagCompound tag2 = tag1.getCompoundTag(String.valueOf(i));
			if (!tag2.hasUniqueId("id")||!tag2.hasKey("loaders", 9))
				continue;
			loaders.put(tag2.getUniqueId("id"), new PlayerChunkloaders()
					.loadChunkloadersNBT((NBTTagList) tag2.getTag("loaders")));
		}
		NBTTagCompound tag3 = tag.getCompoundTag("playerNames");
		for (int i = 0; tag3.hasKey(String.valueOf(i)); i++) {
			NBTTagCompound tag4 = tag3.getCompoundTag(String.valueOf(i));
			if (!tag4.hasUniqueId("id")||!tag4.hasKey("name"))
				continue;
			playerNames.put(tag4.getUniqueId("id"), tag4.getString("name"));
		}
	}

	// TODO this will probably break if loaders is modified concurrently
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		NBTTagCompound tag1 = new NBTTagCompound();
		Iterator<UUID> iter = loaders.keySet().iterator();
		for (int i = 0; iter.hasNext(); i++) {
			NBTTagCompound tag2 = new NBTTagCompound();
			UUID id = iter.next();
			tag2.setUniqueId("id", id);
			tag2.setTag("loaders", (loaders.get(id)).getChunkloadersNBT());
			tag1.setTag(String.valueOf(i), tag2);
		}
		tag.setTag("chunkloaders", tag1);
		iter = playerNames.keySet().iterator();
		NBTTagCompound tag3 = new NBTTagCompound();
		for (int i = 0; iter.hasNext(); i++) {
			NBTTagCompound tag4 = new NBTTagCompound();
			UUID id = iter.next();
			tag4.setUniqueId("id", id);
			tag4.setString("name", playerNames.get(id));
			tag3.setTag(String.valueOf(i), tag4);
		}
		tag.setTag("playerNames", tag1);
		return tag;
	}

	public static ChunkloaderManager getInstance(World world) {
		if (INSTANCE!=null)
			return INSTANCE;
		if (world==null)
			return null;
		MapStorage storage = world.getMapStorage();
		ChunkloaderManager instance = (ChunkloaderManager) (storage
				.getOrLoadData(ChunkloaderManager.class, DATA_NAME));
		if (instance==null) {
			instance = new ChunkloaderManager();
			storage.setData(DATA_NAME, instance);
		}
		INSTANCE = instance;
		return instance;
	}

	public void tick() {
	}

	public void reloadChunkloaders() {
		CubeLoader.logger.info("Reloading chunkloaders...");
		loaders.values().forEach(playerChunkloaders -> playerChunkloaders.reloadChunkloaders());
	}

	public PlayerChunkloaders getPlayerChunkloaders(UUID id) {
		PlayerChunkloaders playerLoaders = loaders.get(id);
		if (playerLoaders==null) {
			playerLoaders = new PlayerChunkloaders();
			loaders.put(id, playerLoaders);
		}
		return playerLoaders;
	}

	public String getPlayerName(UUID id) {
		return playerNames.get(id);
	}

	public void setPlayerName(UUID id, String name) {
		playerNames.put(id, name);
		markDirty();
	}
}
