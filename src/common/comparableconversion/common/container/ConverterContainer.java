/**
 * 
 */
package comparableconversion.common.container;

import net.minecraft.src.Container;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.InventoryPlayer;
import net.minecraft.src.ItemStack;
import net.minecraft.src.Slot;

import comparableconversion.common.ComparableConversion;
import comparableconversion.common.tile.ConverterTile;
import comparableconversion.common.utils.ValueModel;

/**
 * @author <a href="mailto:selurgniman@selurgniman.org">Selurgniman</a>
 * 
 */
public class ConverterContainer extends Container {
	private ValueModel valueModel = ValueModel.getInstance(ComparableConversion.instance);
	private ConverterTile converter;
	private Slot converterSlot;
	private Slot focusSlot;
	private Slot resultSlot;

	/**
	 * @param inventoryPlayer
	 * @param converter
	 */
	public ConverterContainer(InventoryPlayer inventoryPlayer, final ConverterTile converter) {
		this.converter = converter;
		converter.setPlayer(inventoryPlayer.player);
		converterSlot = new Slot(converter, ConverterTile.CONVERTER_SLOT, 56, 35) {
			@Override
			public void putStack(ItemStack itemStack) {
				if (itemStack != null) {
					converter.increaseStoredValue(itemStack);
				}
				super.putStack(null);
			}
		};
		focusSlot = new Slot(converter, ConverterTile.FOCUS_SLOT, 87, 62) {

			@Override
			public ItemStack decrStackSize(int par1) {
				return super.decrStackSize(par1);
			}

			@Override
			public void putStack(ItemStack itemStack) {
				if (itemStack != null) {
					converter.setFocusValue(valueModel.getValue(itemStack));
				}
				super.putStack(itemStack);
			}

			@Override
			public void onPickupFromSlot(ItemStack itemStack) {
				converter.setFocusValue(0);
			}
		};
		resultSlot = new Slot(converter, ConverterTile.FOCUS_SLOT, 118, 35) {
			@Override
			public boolean isItemValid(ItemStack par1ItemStack) {
				return false;
			}

			@Override
			public void putStack(ItemStack par1ItemStack) {
				return;
			}

			@Override
			public ItemStack decrStackSize(int par1) {
				return getStack();
			}

			@Override
			public void onPickupFromSlot(ItemStack itemStack) {
				if (converter.getStoredValue() >= converter.getFocusValue()) {
					converter.reduceStoredValue(converter.getFocusValue());
				}
			}

			@Override
			public ItemStack getStack() {
				ItemStack returnStack = focusSlot.getStack();
				if (returnStack != null) {
					returnStack = returnStack.copy();
					returnStack.stackSize = 1;
				}

				return returnStack;
			}
		};
		addSlotToContainer(converterSlot);
		addSlotToContainer(focusSlot);
		addSlotToContainer(resultSlot);

		bindPlayerInventory(inventoryPlayer);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ItemStack slotClick(int slotIndex, int inventoryType, boolean shiftClicked, EntityPlayer player) {
		if (slotIndex == resultSlot.slotNumber) {
			ComparableConversion.instance.debug("stored: " + converter.getStoredValue() + " focus: " + converter.getFocusValue());
			if (converter.getStoredValue() >= converter.getFocusValue() && converter.getFocusValue() > 0) {
				return resultSlotClick(slotIndex, inventoryType, shiftClicked, player);
			}

			return null;
		} else {
			return super.slotClick(slotIndex, inventoryType, shiftClicked, player);
		}
	}

	private ItemStack resultSlotClick(int slotIndex, int clickType, boolean shiftClicked, EntityPlayer player) {
		ItemStack returnStack = null;

		InventoryPlayer inventory = player.inventory;
		Slot resultSlot = (Slot) this.inventorySlots.get(slotIndex);

		if (shiftClicked) {
			ItemStack resultStack = resultSlot.getStack();
			if (resultStack != null && converter.getFocusValue() > 0) {
				int amount = converter.getStoredValue() / converter.getFocusValue();
				if (amount > resultStack.getMaxStackSize()) {
					amount = resultStack.getMaxStackSize();
				}
				resultStack.stackSize = amount;
				converter.reduceStoredValue(resultStack);
				mergeItemStack(resultStack, 3, inventorySlots.size(), false);
			}
		} else {
			if (slotIndex < 0) {
				return null;
			}

			if (resultSlot != null) {
				ItemStack resultStack = resultSlot.getStack();
				ItemStack inventoryStack = inventory.getItemStack();

				if (resultStack != null) {
					returnStack = resultStack.copy();
				}

				int stackSize;

				if (resultStack == null) {
					if (inventoryStack != null && resultSlot.isItemValid(inventoryStack)) {
						stackSize = clickType == 0 ? inventoryStack.stackSize : 1;

						if (stackSize > resultSlot.getSlotStackLimit()) {
							stackSize = resultSlot.getSlotStackLimit();
						}

						resultSlot.putStack(inventoryStack.splitStack(stackSize));

						if (inventoryStack.stackSize == 0) {
							inventory.setItemStack((ItemStack) null);
						}
					}
				} else if (inventoryStack == null) {
					stackSize = clickType == 0 ? resultStack.stackSize : (resultStack.stackSize + 1) / 2;
					ItemStack itemStack = resultSlot.decrStackSize(stackSize);
					inventory.setItemStack(itemStack);

					if (resultStack.stackSize == 0) {
						resultSlot.putStack((ItemStack) null);
					}

					resultSlot.onPickupFromSlot(inventory.getItemStack());
				} else if (resultSlot.isItemValid(inventoryStack)) {
					if (resultStack.itemID == inventoryStack.itemID
							&& (!resultStack.getHasSubtypes() || resultStack.getItemDamage() == inventoryStack
									.getItemDamage()) && ItemStack.func_77970_a(resultStack, inventoryStack)) {
						stackSize = clickType == 0 ? inventoryStack.stackSize : 1;

						if (stackSize > resultSlot.getSlotStackLimit() - resultStack.stackSize) {
							stackSize = resultSlot.getSlotStackLimit() - resultStack.stackSize;
						}

						if (stackSize > inventoryStack.getMaxStackSize() - resultStack.stackSize) {
							stackSize = inventoryStack.getMaxStackSize() - resultStack.stackSize;
						}

						inventoryStack.splitStack(stackSize);

						if (inventoryStack.stackSize == 0) {
							inventory.setItemStack((ItemStack) null);
						}

						resultStack.stackSize += stackSize;
					} else if (inventoryStack.stackSize <= resultSlot.getSlotStackLimit()) {
						resultSlot.putStack(inventoryStack);
						inventory.setItemStack(resultStack);
					}
				} else if (resultStack.itemID == inventoryStack.itemID && inventoryStack.getMaxStackSize() > 1
						&& (!resultStack.getHasSubtypes() || resultStack.getItemDamage() == inventoryStack.getItemDamage())
						&& ItemStack.func_77970_a(resultStack, inventoryStack)) {
					stackSize = resultStack.stackSize;

					if (stackSize > 0 && stackSize + inventoryStack.stackSize <= inventoryStack.getMaxStackSize()) {
						inventoryStack.stackSize += stackSize;

						if (resultStack.stackSize == 0) {
							resultSlot.putStack((ItemStack) null);
						}

						resultSlot.onPickupFromSlot(inventory.getItemStack());
					}
				}

				resultSlot.onSlotChanged();
			}
		}

		return returnStack;
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
	public boolean canInteractWith(EntityPlayer player) {
		return this.converter.getPlayer() == player;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ItemStack transferStackInSlot(int slotIndex) {
		ItemStack stack = null;
		Slot slotObject = (Slot) inventorySlots.get(slotIndex);

		// null checks and checks if the item can be stacked (maxStackSize > 1)
		if (slotObject != null && slotObject.getHasStack()) {
			ItemStack stackInSlot = slotObject.getStack();
			stack = stackInSlot.copy();

			// merges the item into player inventory since its in the tileEntity
			if (slotIndex == resultSlot.slotNumber || slotIndex == focusSlot.slotNumber) {
				if (!mergeItemStack(stackInSlot, 3, inventorySlots.size(), true)) {
					return null;
				}
			}
			// places it into the tileEntity if possible since its in the
			// player inventory
			else if (!mergeItemStack(stackInSlot, (focusSlot.getHasStack() ? 0 : 1), (focusSlot.getHasStack() ? 1 : 2),
					false)) {
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

	/**
	 * Callback for when the crafting gui is closed.
	 */
	@Override
	public void onCraftGuiClosed(EntityPlayer player) {
		this.converter.setPlayer((EntityPlayer) null);
	}
}