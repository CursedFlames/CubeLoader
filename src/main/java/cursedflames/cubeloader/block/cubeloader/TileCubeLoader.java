package cursedflames.cubeloader.block.cubeloader;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;

import cubicchunks.util.ticket.ITicket;
import cubicchunks.util.ticket.TicketList;
import cubicchunks.world.ICubicWorld;
import cursedflames.cubeloader.chunkloading.ChunkloaderManager;
import cursedflames.cubeloader.chunkloading.ChunkloaderPos;
import cursedflames.cubeloader.chunkloading.PlayerChunkloaders;
import cursedflames.lib.Util;
import cursedflames.lib.block.GenericTileEntity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;

public class TileCubeLoader extends GenericTileEntity
		implements ITicket/* , ILightProvider */ {
	UUID owner;
	private static final int DEFAULT_HORIZONTAL_RADIUS = 1;
	private static final int DEFAULT_VERTICAL_RADIUS = 1;
	private int xRange = DEFAULT_HORIZONTAL_RADIUS;
	private int yRange = DEFAULT_VERTICAL_RADIUS;
	private int zRange = DEFAULT_HORIZONTAL_RADIUS;
	private Set<TicketList> ticketLists = new HashSet<>();
//	TESRCubeLoader.TESRPolyhedronData poly = new TESRCubeLoader.TESRPolyhedronData();
	ItemStackHandler itemHandler = new ItemStackHandler(3) {
		@Override
		protected void onContentsChanged(int slot) {
			TileCubeLoader.this.markDirty();
		}
	};

	@Override
	public NBTTagCompound writeDataToNBT(NBTTagCompound tag) {
		if (owner!=null)
			tag.setUniqueId("owner", owner);
		tag.setInteger("xRange", xRange);
		tag.setInteger("yRange", yRange);
		tag.setInteger("zRange", zRange);
		tag.setTag("items", itemHandler.serializeNBT());
		return tag;
	}

	@Override
	public void readDataFromNBT(NBTTagCompound tag) {
		if (tag.hasKey("owner"))
			owner = tag.getUniqueId("owner");
		xRange = tag.hasKey("xRange") ? tag.getInteger("xRange") : DEFAULT_HORIZONTAL_RADIUS;
		yRange = tag.hasKey("yRange") ? tag.getInteger("yRange") : DEFAULT_VERTICAL_RADIUS;
		zRange = tag.hasKey("zRange") ? tag.getInteger("zRange") : DEFAULT_HORIZONTAL_RADIUS;
		if (tag.hasKey("items"))
			itemHandler.deserializeNBT(tag.getCompoundTag("items"));
	}

	@Override
	public NBTTagCompound getBlockBreakNBT() {
		return null;
	}

	@Override
	public void loadBlockPlaceNBT(NBTTagCompound tag) {
	}

	/** ITicket method to determine whether a chunk should tick */
	@Override
	public boolean shouldTick() {
		return true;
	}

//	public void onNeighborChange(IBlockAccess world, BlockPos pos, BlockPos neighbor) {
//		updateCubeLoading(false);
//	}

	/**
	 * 
	 * @return whether the cubeloader is enabled - should be removed from any
	 *         CubeLoader lists if false
	 */
	public boolean updateCubeLoading(boolean worldLoad) {
		if (!world.isRemote) {
			// TODO add non-cubic world compatibility
			if (!(this.world instanceof ICubicWorld))
				return false;
			for (Iterator<TicketList> iter = ticketLists.iterator(); iter.hasNext();) {
				TicketList tickets = iter.next();
				if (tickets.contains(this)) {
					tickets.remove(this);
				}
			}
			ticketLists = new HashSet<>();
			ICubicWorld world = ((ICubicWorld) this.world);
			int chunkX = pos.getX()>>4;
			int chunkY = pos.getY()>>4;
			int chunkZ = pos.getZ()>>4;
//			CommonProxy.logger.info("ranges "+xRange+" "+yRange+" "+zRange);
			for (int x = chunkX-xRange; x<chunkX+xRange+1; x++) {
				for (int y = chunkY-yRange; y<chunkY+yRange+1; y++) {
					for (int z = chunkZ-zRange; z<chunkZ+zRange+1; z++) {
//						CommonProxy.logger.info("loading "+x+" "+y+" "+z);
						TicketList tickets = world.getCubeFromCubeCoords(x, y, z).getTickets();
						ticketLists.add(tickets);
						if (!tickets.contains(this)) {
							tickets.add(this);
						}
					}
				}
			}
			// attempting to modify player chunk loaders when loading the world
			// will cause a ConcurrentModificationException
			if (worldLoad)
				return true;
			PlayerChunkloaders loaders = ChunkloaderManager.getInstance((World) world)
					.getPlayerChunkloaders(owner);
			if (loaders!=null) {
				// TODO check that player is not above chunkloader limit
				loaders.getChunkloaders()
						.add(new ChunkloaderPos(pos, world.getProvider().getDimension()));
			}
		}
		return true;
	}

	// TODO add support for disabled loaders
	public int getNumCubesLoaded() {
		return (xRange*2+1)*(yRange*2+1)*(zRange*2+1);
	}

	public int getNumCubesInRange() {
		return getNumCubesLoaded();
	}

	public UUID getOwner() {
		return owner;
	}

	public int getXRange() {
		return xRange;
	}

	public void updateClients() {
		Util.updateBlock(world, pos, null, false, true, false, false, false);
	}

	public void setXRange(int xRange) {
		this.xRange = xRange;
		updateClients();
		markDirty();
		updateCubeLoading(false);
		// CommonProxy.logger.info("setting xRange to "+xRange);
	}

	public int getYRange() {
		return yRange;
	}

	public void setYRange(int yRange) {
		this.yRange = yRange;
		updateClients();
		markDirty();
		updateCubeLoading(false);
		// CommonProxy.logger.info("setting yRange to "+yRange);
	}

	public int getZRange() {
		return zRange;
	}

	public void setZRange(int zRange) {
		this.zRange = zRange;
		updateClients();
		markDirty();
		updateCubeLoading(false);
		// CommonProxy.logger.info("setting zRange to "+zRange);
	}

	public void setRange(int xRange, int yRange, int zRange) {
		this.xRange = xRange;
		this.yRange = yRange;
		this.zRange = zRange;
		updateClients();
		markDirty();
		updateCubeLoading(false);
	}

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		if (capability==CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
			return true;
		}
		return super.hasCapability(capability, facing);
	}

	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		if (capability==CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
			return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(itemHandler);
		}
		return super.getCapability(capability, facing);
	}

	@Override
	public boolean hasFastRenderer() {
		return true;
	}

	// TODO might not need this
	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		return INFINITE_EXTENT_AABB;
	}

//	@Override
//	public Light provideLight() {
//		CommonProxy.logger.info("providing light");
//		return new Light(pos.getX(), pos.getY(), pos.getZ(), 1, 0, 0, 1, 15);
//	}
}
