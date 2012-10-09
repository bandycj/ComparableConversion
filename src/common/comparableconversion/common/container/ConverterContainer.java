/**
 * 
 */
package comparableconversion.common.container;

import net.minecraft.src.Container;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.InventoryPlayer;
import net.minecraft.src.ItemStack;
import net.minecraft.src.Slot;

import comparableconversion.common.tile.ConverterTile;

/**
 * @author <a href="mailto:selurgniman@selurgniman.org">Selurgniman</a>
 * 
 */
public class ConverterContainer extends Container {

	protected ConverterTile reducer;

	/**
	 * @param inventoryPlayer
	 * @param reducer
	 */
	public ConverterContainer(InventoryPlayer inventoryPlayer, ConverterTile reducer) {
		this.reducer = reducer;

		addSlotToContainer(new Slot(reducer, ConverterTile.CONVERTER_SLOT, 56, 35));
		addSlotToContainer(new Slot(reducer, ConverterTile.RESULT_SLOT, 118, 35));
		addSlotToContainer(new Slot(reducer, ConverterTile.FOCUS_SLOT, 87, 62){
			@Override
			public void onSlotChanged(){
				
			}
		});

		bindPlayerInventory(inventoryPlayer);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean canInteractWith(EntityPlayer player) {
		return reducer.isUseableByPlayer(player);
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