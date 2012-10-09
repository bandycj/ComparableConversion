/**
 * 
 */
package comparableconversion.common.tile;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.IInventory;
import net.minecraft.src.ItemStack;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.NBTTagList;
import net.minecraft.src.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.common.ISidedInventory;

import comparableconversion.common.ComparableConversion;
import comparableconversion.common.ValueModel;
import comparableconversion.common.block.ConverterBlock;

/**
 * @author <a href="mailto:e83800@wnco.com">Chris Bandy</a> Created on: Oct 3,
 *         2012
 */
public class ConverterTile extends TileEntity implements IInventory, ISidedInventory {
	public static final int CONVERTER_SLOT = 0;
	public static final int RESULT_SLOT = 1;
	public static final int FOCUS_SLOT = 2;

	private ItemStack[] inv;
	private Integer storedValue = 0;
	private Integer focusValue = 0;

	/**
	 * The number of ticks that a fresh copy of the currently-burning item would
	 * keep the take the converter to reduce
	 */
	public int currentItemBurnTime = 0;

	/** The number of ticks that the current item has been cooking for */
	public int converterCookTime = 0;

	public ConverterTile() {
		inv = new ItemStack[3];
	}

	@Override
	public int getSizeInventory() {
		return inv.length;
	}

	@Override
	public ItemStack getStackInSlot(int slot) {
		return inv[slot];
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack stack) {
		inv[slot] = stack;
		if (stack != null && stack.stackSize > getInventoryStackLimit()) {
			stack.stackSize = getInventoryStackLimit();
		}
	}

	@Override
	public ItemStack decrStackSize(int slot, int amt) {
		ItemStack stack = getStackInSlot(slot);
		if (stack != null) {
			if (stack.stackSize <= amt) {
				setInventorySlotContents(slot, null);
			} else {
				stack = stack.splitStack(amt);
				if (stack.stackSize == 0) {
					setInventorySlotContents(slot, null);
				}
			}
		}
		return stack;
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int slot) {
		ItemStack stack = getStackInSlot(slot);
		if (stack != null) {
			setInventorySlotContents(slot, null);
		}
		return stack;
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer player) {
		return worldObj.getBlockTileEntity(xCoord, yCoord, zCoord) == this
				&& player.getDistanceSq(xCoord + 0.5, yCoord + 0.5, zCoord + 0.5) < 64;
	}

	@Override
	public void openChest() {
	}

	@Override
	public void closeChest() {
	}

	@Override
	public void readFromNBT(NBTTagCompound tagCompound) {
		super.readFromNBT(tagCompound);

		NBTTagList tagList = tagCompound.getTagList("Inventory");
		for (int i = 0; i < tagList.tagCount(); i++) {
			NBTTagCompound tag = (NBTTagCompound) tagList.tagAt(i);
			byte slot = tag.getByte("Slot");
			if (slot >= 0 && slot < inv.length) {
				inv[slot] = ItemStack.loadItemStackFromNBT(tag);
			}
		}

		this.storedValue = tagCompound.getInteger("StoredValue");
		this.focusValue = tagCompound.getInteger("FocusValue");
	}

	@Override
	public void writeToNBT(NBTTagCompound tagCompound) {
		super.writeToNBT(tagCompound);
		tagCompound.setInteger("StoredValue", this.storedValue);
		tagCompound.setInteger("FocusValue", this.focusValue);

		NBTTagList itemList = new NBTTagList();
		for (int i = 0; i < inv.length; i++) {
			ItemStack stack = inv[i];
			if (stack != null) {
				NBTTagCompound tag = new NBTTagCompound();
				tag.setByte("Slot", (byte) i);
				stack.writeToNBT(tag);
				itemList.appendTag(tag);
			}
		}
		tagCompound.setTag("Inventory", itemList);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getInvName() {
		return ConverterBlock.class.toString();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getStartInventorySide(ForgeDirection side) {
		if (side == ForgeDirection.DOWN)
			return 1;
		if (side == ForgeDirection.UP)
			return 0;
		return 2;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getSizeInventorySide(ForgeDirection side) {
		return 1;
	}

	@Override
	public void updateEntity() {
		if (!this.worldObj.isRemote && canConvert()) {
			ValueModel valueModel = ValueModel.getInstance(ComparableConversion.instance);
			ItemStack focusItem = this.inv[FOCUS_SLOT];

			if (this.focusValue < 1 && focusItem != null) {
				this.focusValue = valueModel.getValue(focusItem.getItem());
				System.err.println("updated focus: " + this.focusValue);
			}

			convertItem();
		}
	}

	/**
	 * Returns an integer between 0 and the passed value representing how close
	 * the current item is to being completely cooked
	 */
	public int getCookProgressScaled(int max) {
		try {
			System.err.println("progress..." + this.storedValue + ":" + this.focusValue);
			int progress = (this.storedValue / this.focusValue) * max;
			System.err.println("progress: " + progress);
			return progress;
		} catch (ArithmeticException ex) {
			return 0;
		}
	}

	/**
	 * Returns true if the converter can reduce an item, i.e. has a source item,
	 * destination stack isn't full, etc.
	 */
	private boolean canConvert() {
		if (inv[CONVERTER_SLOT] != null) {
			ItemStack focus = inv[FOCUS_SLOT];
			ItemStack result = inv[RESULT_SLOT];
			if (focus != null) {
				if ((result != null && focus.getItem() == result.getItem() && result.stackSize <= result
						.getMaxStackSize()) || result == null) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Turn one item from the converter source stack into the focus item.
	 */
	public void convertItem() {
		int value = ValueModel.getInstance(ComparableConversion.instance).getValue(this.inv[CONVERTER_SLOT].getItem());

		if (value > 0) {
			this.storedValue += value;
		}

		if (this.inv[CONVERTER_SLOT].stackSize > 1) {
			--this.inv[CONVERTER_SLOT].stackSize;
		} else {
			this.inv[CONVERTER_SLOT] = null;
		}

		if (this.storedValue >= this.focusValue) {
			this.storedValue -= this.focusValue;
			if (this.inv[RESULT_SLOT] == null) {
				this.inv[RESULT_SLOT] = new ItemStack(this.inv[FOCUS_SLOT].getItem(), 1);
			} else {
				this.inv[RESULT_SLOT].stackSize += 1;
			}
		}

		this.onInventoryChanged();
	}
}
