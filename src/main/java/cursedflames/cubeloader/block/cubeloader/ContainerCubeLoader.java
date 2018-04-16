package cursedflames.cubeloader.block.cubeloader;

import javax.annotation.Nullable;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

//TODO make a more generic container class
public class ContainerCubeLoader extends Container {
	TileCubeLoader te;
	public static final int NUM_SLOTS = 3;
	// duplicated here because Gui class is client only
	public static final int GUI_HEIGHT = 200;

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
