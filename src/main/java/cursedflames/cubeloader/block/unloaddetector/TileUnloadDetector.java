package cursedflames.cubeloader.block.unloaddetector;

import cursedflames.lib.block.GenericTileEntity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ITickable;

public class TileUnloadDetector extends GenericTileEntity implements ITickable {
	int counter = 0;

	@Override
	public NBTTagCompound writeDataToNBT(NBTTagCompound tag) {
		tag.setInteger("counter", counter+1);
		return tag;
	}

	// Undo the increment done by writeDataToNBT, since this method is for
	// client-server syncing
	@Override
	public NBTTagCompound getUpdateTag() {
		NBTTagCompound tag = super.getUpdateTag();
		tag.setInteger("counter", tag.getInteger("counter")-1);
		return tag;
	}

	@Override
	public void readDataFromNBT(NBTTagCompound tag) {
		counter = tag.getInteger("counter")&15;
	}

	@Override
	public void update() {
		if (!world.isRemote&&(world.getWorldTime()&63)==37) {
			markDirty();
		}
	}

	@Override
	public NBTTagCompound getBlockBreakNBT() {
		return null;
	}

	@Override
	public void loadBlockPlaceNBT(NBTTagCompound tag) {
	}
}
