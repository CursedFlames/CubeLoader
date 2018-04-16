package cursedflames.cubeloader.chunkloading;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import cursedflames.cubeloader.block.cubeloader.TileCubeLoader;
import cursedflames.lib.Util;
import net.minecraft.nbt.NBTTagList;

//TODO fuels/power - also try allow for multiple items for a single fuel type, and multiple possible fuel types
public class PlayerChunkloaders {
	private Set<ChunkloaderPos> chunkloaders = new HashSet<>();
	// Map<Item, Long> fuelItems = new HashMap<>();
	// TODO allow liquid fuels
	// long power;
	boolean powered;

	public PlayerChunkloaders() {
		// TODO load item fuel types from config
		// TODO add check for whether loader is powered
		// power = 0;
		powered = true;
	}

	public Set<ChunkloaderPos> getChunkloaders() {
		return chunkloaders;
	}

	public void reloadChunkloaders() {
		chunkloaders.forEach(pos -> {
			TileCubeLoader te = pos.getCubeLoader(true, true);
			if (te!=null) {
				te.updateCubeLoading(true);
			}
		});
	}

	public NBTTagList getChunkloadersNBT() {
		return Util.listToNBTList(new ArrayList<ChunkloaderPos>(chunkloaders));
	}

	public PlayerChunkloaders loadChunkloadersNBT(NBTTagList nbtList) {
		List<Object> list = Util.listFromNBTList(nbtList, ChunkloaderPos.class);
		List<ChunkloaderPos> list2 = new ArrayList<>();
		list.forEach((Object obj) -> {
			if (obj instanceof ChunkloaderPos)
				list2.add((ChunkloaderPos) obj);
		});
		chunkloaders.addAll(list2);
		return this;
	}
}
