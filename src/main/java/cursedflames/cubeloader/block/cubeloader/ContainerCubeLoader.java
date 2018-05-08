package cursedflames.cubeloader.block.cubeloader;

import java.util.UUID;

import javax.annotation.Nullable;

import cursedflames.cubeloader.chunkloading.ChunkloaderManager;
import cursedflames.cubeloader.chunkloading.PlayerChunkloaders;
import cursedflames.cubeloader.network.PacketHandler;
import cursedflames.cubeloader.network.PacketHandler.HandlerIds;
import cursedflames.lib.network.NBTPacket;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

//TODO make a more generic container class
public class ContainerCubeLoader extends Container {
	TileCubeLoader te;
	public static final int NUM_SLOTS = 3;
	// duplicated here because Gui class is client only
	public static final int GUI_HEIGHT = 256;
	boolean disabled, fueled, paused;
	int cubesLoaded, globalLoaded;
	UUID owner;
	String ownerName;

	public ContainerCubeLoader(IInventory playerInventory, TileCubeLoader te) {
		this.te = te;
		addInputSlots();
		addPlayerSlots(playerInventory);
	}

	private void addPlayerSlots(IInventory playerInventory) {
		// Slots for the main inventory
		for (int row = 0; row<3; ++row) {
			for (int col = 0; col<9; ++col) {
				int x = 8+col*18;
				int y = GUI_HEIGHT-82+row*18;
				addSlotToContainer(new Slot(playerInventory, col+row*9+9, x, y));
			}
		}

		// Slots for the hotbar
		for (int row = 0; row<9; ++row) {
			int x = 8+row*18;
			int y = GUI_HEIGHT-82+58;
			addSlotToContainer(new Slot(playerInventory, row, x, y));
		}
	}

	private void addInputSlots() {
		IItemHandler handler = te.itemHandler;
		// TODO fix slot positions
		for (int i = 0; i<3; i++) {
			int x = 62+i*18;
			int y = GUI_HEIGHT-113;
			addSlotToContainer(new SlotItemHandler(handler, i, x, y));
		}
	}

	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();
		// I don't think this is ever true, but just in case
		if (te.getWorld().isRemote)
			return;
		PlayerChunkloaders playerLoaders = te.getPlayerChunkloaders();
		if (playerLoaders==null)
			return;
		NBTTagCompound tag = new NBTTagCompound();
		boolean disabled = te.isDisabled();
		if (disabled!=this.disabled) {
			this.disabled = disabled;
			tag.setBoolean("disabled", disabled);
		}
		boolean fueled = playerLoaders.isFueled();
		if (fueled!=this.fueled) {
			this.fueled = fueled;
			tag.setBoolean("fueled", fueled);
		}
		boolean paused = playerLoaders.isPaused();
		if (paused!=this.paused) {
			this.paused = paused;
			tag.setBoolean("paused", paused);
		}
		int teLoaded = te.getNumCubesInRange();
		if (teLoaded!=cubesLoaded) {
			cubesLoaded = teLoaded;
			tag.setInteger("cubesLoaded", teLoaded);
		}
		int allLoaded = playerLoaders.getTotalLoaded();
		if (allLoaded!=globalLoaded) {
			globalLoaded = allLoaded;
			tag.setInteger("globalLoaded", allLoaded);
		}
		UUID id = te.owner;
		if (id!=owner) {
			owner = id;
			tag.setUniqueId("owner", id);
			ownerName = ChunkloaderManager.getInstance(te.getWorld()).getPlayerName(owner);
			tag.setString("ownerName", ownerName);
		}

		if (tag.getSize()==0) {
			return;
		}
		for (int j = 0; j<this.listeners.size(); ++j) {
			PacketHandler.INSTANCE.sendTo(new NBTPacket(tag, HandlerIds.UPDATE_GUI_DATA.id),
					((EntityPlayerMP) this.listeners.get(j)));
		}
	}

	public void readChanges(NBTTagCompound tag) {
		if (tag.hasKey("disabled"))
			disabled = tag.getBoolean("disabled");
		if (tag.hasKey("fueled"))
			fueled = tag.getBoolean("fueled");
		if (tag.hasKey("paused"))
			paused = tag.getBoolean("paused");
		if (tag.hasKey("cubesLoaded"))
			cubesLoaded = tag.getInteger("cubesLoaded");
		if (tag.hasKey("globalLoaded"))
			globalLoaded = tag.getInteger("globalLoaded");
		if (tag.hasUniqueId("owner"))
			owner = tag.getUniqueId("owner");
		if (tag.hasKey("ownerName"))
			ownerName = tag.getString("ownerName");
	}

	@Nullable
	@Override
	public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
		ItemStack itemstack = null;
		Slot slot = this.inventorySlots.get(index);

		if (slot!=null&&slot.getHasStack()) {
			ItemStack itemstack1 = slot.getStack();
			itemstack = itemstack1.copy();

			if (index<NUM_SLOTS) {
				if (!this.mergeItemStack(itemstack1, NUM_SLOTS, this.inventorySlots.size(), true)) {
					return ItemStack.EMPTY;
				}
			} else if (!this.mergeItemStack(itemstack1, 0, NUM_SLOTS, false)) {
				return ItemStack.EMPTY;
			}

			if (itemstack1.isEmpty()) {
				slot.putStack(ItemStack.EMPTY);
			} else {
				slot.onSlotChanged();
			}
		}

		return itemstack==null ? ItemStack.EMPTY : itemstack;
	}

	// TODO make this not use the TileEntity pos - generic blockpos instead?
	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		return te.canInteractWith(playerIn);
	}
}
