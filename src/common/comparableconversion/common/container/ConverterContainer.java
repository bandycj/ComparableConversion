/**
 * 
 */
package comparableconversion.common.container;

import net.minecraft.src.Container;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.IInventory;
import net.minecraft.src.InventoryCraftResult;
import net.minecraft.src.InventoryCrafting;
import net.minecraft.src.InventoryPlayer;
import net.minecraft.src.ItemStack;
import net.minecraft.src.Slot;
import net.minecraft.src.SlotCrafting;

import comparableconversion.common.tile.ConverterTile;

/**
 * @author <a href="mailto:selurgniman@selurgniman.org">Selurgniman</a>
 * 
 */
public class ConverterContainer extends Container {

	protected ConverterTile converter;
	public IInventory craftResult = new InventoryCraftResult();

	/**
	 * @param inventoryPlayer
	 * @param converter
	 */
	public ConverterContainer(InventoryPlayer inventoryPlayer, ConverterTile converter) {
		this.converter = converter;
		this.converter.addEventHandler(this);
		addSlotToContainer(new Slot(converter, ConverterTile.CONVERTER_SLOT, 56, 35));
		addSlotToContainer(new Slot(converter, ConverterTile.FOCUS_SLOT, 87, 62));
		addSlotToContainer(new SlotCrafting(inventoryPlayer.player, this.converter, this.craftResult, 0, 87, 62));
		// addSlotToContainer(new Slot(converter, ConverterTile.RESULT_SLOT,
		// 118, 35) {
		// @Override
		// public boolean isItemValid(ItemStack itemStack) {
		// return false;
		// }
		//
		// @Override
		// public void onPickupFromSlot(ItemStack itemStack) {
		// super.onPickupFromSlot(itemStack);
		// int value =
		// ValueModel.getInstance(ComparableConversion.instance).getValue(itemStack);
		// if (value > 0) {
		// converter.reduceStoredValue(value);
		// }
		// }
		// });

		bindPlayerInventory(inventoryPlayer);
		this.onCraftMatrixChanged(this.converter);
	}

	/**
	 * Callback for when the crafting matrix is changed.
	 */
	@Override
	public void onCraftMatrixChanged(IInventory par1IInventory) {
		// if (converter.getStoredValue() >= converter.getFocusValue()){
		ItemStack focusItem = converter.getStackInSlot(ConverterTile.FOCUS_SLOT);
		if (focusItem != null) {
			focusItem = focusItem.copy();
			focusItem.stackSize = 1;
		}
		this.craftResult.setInventorySlotContents(0, focusItem);
		// }
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean canInteractWith(EntityPlayer player) {
		return converter.isUseableByPlayer(player);
	}

	/**
	 * @param inventoryPlayer
	 */
	protected void bindPlayerInventory(InventoryPlayer inventoryPlayer) {
		for (int row = 0; row < 3; row++) {
			for (int col = 0; col < 9; col++) {
				addSlotToContainer(new Slot(inventoryPlayer, col + row * 9 + 9, 8 + col * 18, 94 + row * 18));
			}
		}

		for (int i = 0; i < 9; i++) {
			addSlotToContainer(new Slot(inventoryPlayer, i, 8 + i * 18, 152));
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ItemStack transferStackInSlot(int slot) {
		ItemStack stack = null;
		Slot slotObject = (Slot) inventorySlots.get(slot);

		// null checks and checks if the item can be stacked (maxStackSize > 1)
		if (slotObject != null && slotObject.getHasStack()) {
			ItemStack stackInSlot = slotObject.getStack();
			stack = stackInSlot.copy();

			// merges the item into player inventory since its in the tileEntity
			if (slot == 0) {
				if (!mergeItemStack(stackInSlot, 1, inventorySlots.size(), true)) {
					return null;
				}
				// places it into the tileEntity is possible since its in the
				// player inventory
			} else if (!mergeItemStack(stackInSlot, 0, 1, false)) {
				return null;
			}

			if (stackInSlot.stackSize == 0) {
				slotObject.putStack(null);
			} else {
				slotObject.onSlotChanged();
			}
		}

		return stack;
	}
}