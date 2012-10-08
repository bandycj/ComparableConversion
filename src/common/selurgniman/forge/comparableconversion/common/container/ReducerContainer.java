/**
 * 
 */
package selurgniman.forge.comparableconversion.common.container;

import net.minecraft.src.Container;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.InventoryPlayer;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;
import net.minecraft.src.Slot;
import selurgniman.forge.comparableconversion.common.ComparableConversion;
import selurgniman.forge.comparableconversion.common.InfuseableGem;
import selurgniman.forge.comparableconversion.common.ValueModel;
import selurgniman.forge.comparableconversion.common.tile.ReducerTile;

/**
 * @author <a href="mailto:selurgniman@selurgniman.org">Selurgniman</a>
 * 
 */
public class ReducerContainer extends Container {
	private static final int REDUCER_SLOT = 0;
	private static final int GEM_SLOT = 1;
	
	private ReducerTile reducer;

	public ReducerContainer(InventoryPlayer inventoryPlayer, ReducerTile reducer) {
		// Set the instance of the Tilereducer for the container
		this.reducer = reducer;

		// The source to be reduced slot
		int reducerX = 56;
		int reducerY = 17;
		this.addSlotToContainer(new Slot(reducer, REDUCER_SLOT, reducerX, reducerY));

		// The slot for the gem to be infused
		int gemX = 56;
		int gemY = 62;
		this.addSlotToContainer(new Slot(reducer, GEM_SLOT, gemX, gemY));

		// Add the player's inventory slots to the container
		for (int inventoryRowIndex = 0; inventoryRowIndex < 3; ++inventoryRowIndex) {
			for (int inventoryColumnIndex = 0; inventoryColumnIndex < 9; ++inventoryColumnIndex) {
				this.addSlotToContainer(new Slot(
						inventoryPlayer,
						inventoryColumnIndex + inventoryRowIndex * 9 + 9,
						8 + inventoryColumnIndex * 18,
						94 + inventoryRowIndex * 18));
			}
		}

		// Add the player's action bar slots to the container
		for (int actionBarSlotIndex = 0; actionBarSlotIndex < 9; ++actionBarSlotIndex) {
			this.addSlotToContainer(new Slot(inventoryPlayer, actionBarSlotIndex, 8 + actionBarSlotIndex * 18, 152));
		}
	}

	@Override
	public boolean canInteractWith(EntityPlayer player) {
		return reducer.isUseableByPlayer(player);
	}

	@Override
	public ItemStack transferStackInSlot(int slotIndex) {
		ItemStack itemStack = null;
		Slot fromSlot = (Slot) this.inventorySlots.get(slotIndex);
		
		if (fromSlot != null && fromSlot.getHasStack()) {
			ItemStack fromItemStack = fromSlot.getStack();
			itemStack = fromItemStack.copy();

			if (slotIndex == GEM_SLOT) {
				if (!this.mergeItemStack(fromItemStack, GEM_SLOT, 39, true)) {
					return null;
				}

				fromSlot.onSlotChange(fromItemStack, itemStack);
			} else if (slotIndex != REDUCER_SLOT) {
				int value = ValueModel.getInstance(ComparableConversion.instance).getValue(itemStack.getItem());
				if (value > -1) {
					ItemStack gemSlotItemStack = ((Slot)this.inventorySlots.get(GEM_SLOT)).getStack();
					Item gemSlotItem = gemSlotItemStack.getItem();
					if (gemSlotItem instanceof InfuseableGem) {
						int valueAvailable = gemSlotItem.getMaxDamage()-gemSlotItemStack.getItemDamage();
						if (value < valueAvailable) {
							gemSlotItemStack.setItemDamage(gemSlotItemStack.getItemDamage()+value);
						}
					}
				} else if (slotIndex >= GEM_SLOT && slotIndex < 30) {
					if (!this.mergeItemStack(fromItemStack, 30, 39, false)) {
						return null;
					}
				} else if (slotIndex >= 30 && slotIndex < 39 && !this.mergeItemStack(fromItemStack, GEM_SLOT, 30, false)) {
					return null;
				}
			} else if (!this.mergeItemStack(fromItemStack, GEM_SLOT, 39, false)) {
				return null;
			}

			if (fromItemStack.stackSize == 0) {
				fromSlot.putStack((ItemStack) null);
			} else {
				fromSlot.onSlotChanged();
			}

			if (fromItemStack.stackSize == itemStack.stackSize) {
				return null;
			}

			fromSlot.onPickupFromSlot(fromItemStack);
		}

		return itemStack;
	}

}