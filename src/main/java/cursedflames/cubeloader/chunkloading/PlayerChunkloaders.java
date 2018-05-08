package cursedflames.cubeloader.chunkloading;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import cursedflames.cubeloader.block.cubeloader.TileCubeLoader;
import cursedflames.cubeloader.proxy.CommonProxy;
import cursedflames.lib.Util;
import net.minecraft.nbt.NBTTagList;

//TODO fuels/power - also try allow for multiple items for a single fuel type, and multiple possible fuel types
public class PlayerChunkloaders {
	private Set<ChunkloaderPos> chunkloaders = new HashSet<>();

	private boolean fueled = true;
	private boolean paused = false;
	private int totalLoaded = 0;
	private boolean dirty = true;

	public PlayerChunkloaders() {
		// TODO load item fuel types from config
		// TODO add check for whether loader is powered
		// power = 0;
	}

	public Set<ChunkloaderPos> getChunkloaders() {
		return chunkloaders;
	}

	public boolean isFueled() {
		return fueled;
	}

	public boolean isPaused() {
		return paused;
	}

	public void setPaused(boolean paused) {
		this.paused = paused;
	}

	public void reloadChunkloaders() {
		chunkloaders.forEach(pos -> {
			TileCubeLoader te = pos.getCubeLoader(true, true);
			CommonProxy.logger.info(te);
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
		recalculateTotalLoaded();
		return this;
	}

	public int recalculateTotalLoaded() {
		int newTotal = 0;
		for (Iterator<ChunkloaderPos> iter = chunkloaders.iterator(); iter.hasNext();) {
			ChunkloaderPos p = iter.next();
			TileCubeLoader te = p.getCubeLoader(false, false);
			if (te!=null)
				newTotal += te.getNumCubesLoaded();
		}
		totalLoaded = newTotal;
		return totalLoaded;
	}

	public int getTotalLoaded() {
		if (dirty) {
			dirty = false;
			return recalculateTotalLoaded();
		} else {
			return totalLoaded;
		}
	}

	public void markDirty() {
		ChunkloaderManager inst = ChunkloaderManager.getInstance(null);
		if (inst!=null) {
			inst.markDirty();
		}
		dirty = true;
	}
}
