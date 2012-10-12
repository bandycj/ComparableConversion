/**
 * 
 */
package comparableconversion.common.tile;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.EntityPlayerMP;
import net.minecraft.src.IInventory;
import net.minecraft.src.ItemStack;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.NBTTagList;
import net.minecraft.src.Packet;
import net.minecraft.src.TileEntity;
import net.minecraftforge.event.EventPriority;
import net.minecraftforge.event.ForgeSubscribe;

import comparableconversion.common.ComparableConversion;
import comparableconversion.common.block.ConverterBlock;
import comparableconversion.common.events.ConverterValueEvent;
import comparableconversion.common.utils.PacketHandler;
import comparableconversion.common.utils.ValueModel;

import cpw.mods.fml.common.Side;
import cpw.mods.fml.common.asm.SideOnly;

/**
 * @author <a href="mailto:e83800@wnco.com">Chris Bandy</a> Created on: Oct 3,
 *         2012
 */
/**
 * @author <a href="mailto:e83800@wnco.com">Chris Bandy</a>
 * Created on: Oct 12, 2012
 */
public class ConverterTile extends TileEntity implements IInventory {
	public static final int CONVERTER_SLOT = 0;
	// public static final int RESULT_SLOT = 1;
	public static final int FOCUS_SLOT = 2;

	private final ItemStack[] inv;
	private Integer storedValue;
	private Integer focusValue;
	private final ValueModel valueModel;

	private EntityPlayerMP thePlayer;

	/**
	 * The number of ticks that a fresh copy of the currently-burning item would
	 * keep the take the converter to reduce
	 */
	public int currentItemBurnTime = 0;

	/** The number of ticks that the current item has been cooking for */
	public int converterCookTime = 0;

	public ConverterTile() {
		this.inv = new ItemStack[3];
		this.storedValue = 0;
		this.focusValue = 0;
		this.valueModel = ValueModel.getInstance(ComparableConversion.instance);
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
		//
		// switch (slot) {
		// case FOCUS_SLOT:
		// if (stack != null) {
		// setFocusValue(valueModel.getValue(stack.getItem()));
		// } else {
		// setFocusValue(-1);
		// }
		//
		// inv[FOCUS_SLOT] = stack;
		// break;
		// case CONVERTER_SLOT:
		// if (stack != null) {
		// setStoredValue(getStoredValue() + valueModel.getValue(stack));
		// }
		//
		// inv[CONVERTER_SLOT] = null;
		// break;
		// case RESULT_SLOT:
		// if (canConvert() && inv[FOCUS_SLOT] != null) {
		// inv[RESULT_SLOT] = inv[FOCUS_SLOT].copy();
		// } else {
		// inv[RESULT_SLOT] = null;
		// }
		//
		// break;
		// }

		this.onInventoryChanged();
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
	public ItemStack decrStackSize(int slot, int amt) {
		ItemStack stack = null;
		// if (slot != RESULT_SLOT) {
		stack = getStackInSlot(slot);
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
		// } else if (canConvert()) {
		// stack = getStackInSlot(FOCUS_SLOT).copy();
		// stack.stackSize = 1;
		// }
		return stack;
	}

	@Override
	public void openChest() {
		sendPacketToPlayer();
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

		setStoredValue(tagCompound.getInteger("StoredValue"));
		setFocusValue(tagCompound.getInteger("FocusValue"));
	}

	@Override
	public void writeToNBT(NBTTagCompound tagCompound) {
		super.writeToNBT(tagCompound);
		tagCompound.setInteger("StoredValue", getStoredValue());
		tagCompound.setInteger("FocusValue", getFocusValue());

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
	 * Returns an integer between 0 and the passed value representing how close
	 * the current item is to being completely cooked
	 */
	public int getCookProgressScaled(int max) {
		try {
			int progress = (getStoredValue() / getFocusValue()) * max;
			return progress;
		} catch (ArithmeticException ex) {
			return 0;
		}
	}

	public void setPlayer(EntityPlayer player) {
		if (player instanceof EntityPlayerMP) {
			this.thePlayer = (EntityPlayerMP) player;
			// Side side = FMLCommonHandler.instance().getEffectiveSide();
			// if (side == Side.SERVER) {
			// Packet valuePacket =
			// PacketHandler.assembleValuePacket(this.getStoredValue());
			// Packet focusPacket =
			// PacketHandler.assembleFocusPacket(this.getFocusValue());
			// sendPacketToPlayer(valuePacket);
			// sendPacketToPlayer(focusPacket);
			// }
		}
	}

	public EntityPlayer getPlayer() {
		return this.thePlayer;
	}

	public int getFocusValue() {
		return this.focusValue;
	}

	public int getStoredValue() {
		return this.storedValue;
	}

	public void setFocusValue(int newValue) {
		System.err.println("focus: " + newValue);
		this.focusValue = newValue;
	}

	public void storeValue(ItemStack item) {
		if (item != null) {
			setStoredValue(getStoredValue() + valueModel.getValue(item));
		}
	}

	public void reduceStoredValue(ItemStack item) {
		reduceStoredValue(valueModel.getValue(item));
	}

	public void reduceStoredValue(int amount) {
		if (amount > 0) {
			setStoredValue(getStoredValue() - amount);
		}
	}

	private void setStoredValue(int newValue) {
		System.err.println("stored: " + newValue);
		this.storedValue = newValue;
	}

	public void sendPacketToPlayer() {
		if (thePlayer != null) {
			Packet valuePacket = PacketHandler.assembleValuePacket(getStoredValue());
			thePlayer.playerNetServerHandler.sendPacketToPlayer(valuePacket);
		}
	}

	@SideOnly(Side.CLIENT)
	@ForgeSubscribe(priority = EventPriority.NORMAL)
	public void valueChangeEvent(ConverterValueEvent event) {
		this.storedValue = event.getNewValue();
	}
}
