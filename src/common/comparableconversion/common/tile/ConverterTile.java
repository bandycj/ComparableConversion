/**
 * 
 */
package comparableconversion.common.tile;

import net.minecraft.src.Container;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.EntityPlayerMP;
import net.minecraft.src.IInventory;
import net.minecraft.src.ItemStack;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.NBTTagList;
import net.minecraft.src.Packet;
import net.minecraft.src.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.common.ISidedInventory;
import net.minecraftforge.event.EventPriority;
import net.minecraftforge.event.ForgeSubscribe;

import comparableconversion.common.ComparableConversion;
import comparableconversion.common.ValueModel;
import comparableconversion.common.block.ConverterBlock;
import comparableconversion.common.events.ConverterFocusEvent;
import comparableconversion.common.events.ConverterValueEvent;
import comparableconversion.common.utils.PacketHandler;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Side;
import cpw.mods.fml.common.asm.SideOnly;

/**
 * @author <a href="mailto:e83800@wnco.com">Chris Bandy</a> Created on: Oct 3,
 *         2012
 */
public class ConverterTile extends TileEntity implements IInventory, ISidedInventory {
	public static final int CONVERTER_SLOT = 0;
	public static final int FOCUS_SLOT = 1;

	private ItemStack[] inv;
	private Integer storedValue = 0;
	private Integer focusValue = 0;

	private Container eventHandler;
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

		ValueModel valueModel = ValueModel.getInstance(ComparableConversion.instance);
		switch (slot) {
			case FOCUS_SLOT:
				if (stack != null) {
					setFocusValue(valueModel.getValue(stack.getItem()));
				} else {
					setFocusValue(0);
				}
				break;
			case CONVERTER_SLOT:
				if (stack != null) {
					setStoredValue(getStoredValue() + valueModel.getValue(stack));
					inv[CONVERTER_SLOT] = null;
				}
				break;
		}
		
		this.onInventoryChanged();
		this.eventHandler.onCraftMatrixChanged(this);
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

	/**
	 * Returns an integer between 0 and the passed value representing how close
	 * the current item is to being completely cooked
	 */
	public int getCookProgressScaled(int max) {
		try {
			System.err.println("progress..." + getStoredValue() + ":" + getFocusValue());
			int progress = (getStoredValue() / getFocusValue()) * max;
			System.err.println("progress: " + progress);
			return progress;
		} catch (ArithmeticException ex) {
			return 0;
		}
	}

	public int getFocusValue() {
		return this.focusValue;
	}

	public int getStoredValue() {
		return this.storedValue;
	}

	private void setFocusValue(int newValue) {
		this.focusValue = newValue;
//		updateResultSlot();

		Side side = FMLCommonHandler.instance().getEffectiveSide();
		if (side == Side.SERVER) {
			Packet packet = PacketHandler.assembleFocusPacket(newValue);
			sendPacketToPlayers(packet);
		}
	}

	public void reduceStoredValue(int amount) {
		if (amount > 0) {
			setStoredValue(getStoredValue() - amount);
		}
	}

	public void setStoredValue(int newValue) {
		this.storedValue = newValue;
//		updateResultSlot();

		Side side = FMLCommonHandler.instance().getEffectiveSide();
		if (side == Side.SERVER) {
			Packet packet = PacketHandler.assembleValuePacket(newValue);
			sendPacketToPlayers(packet);
		}
	}
//
//	public void updateResultSlot() {
//		ItemStack resultStack = null;
//		if (this.storedValue >= this.focusValue) {
//			resultStack = getStackInSlot(FOCUS_SLOT);
//			if (resultStack != null) {
//				resultStack = resultStack.copy();
//				resultStack.stackSize = 1;
//			}
//		}
//
//		setInventorySlotContents(RESULT_SLOT, resultStack);
//	}

	public void sendPacketToPlayers(Packet packet) {
		if (this != null && this.worldObj != null && this.worldObj.playerEntities != null) {
			for (Object obj : this.worldObj.playerEntities) {
				if (obj instanceof EntityPlayerMP) {
					EntityPlayerMP player = (EntityPlayerMP) obj;
					double x = this.xCoord - player.posX;
					double y = this.yCoord - player.posY;
					double z = this.zCoord - player.posZ;
					double maxDistance = Math.pow(20, 2);
					if (Math.pow(x, 2) + Math.pow(y, 2) + Math.pow(z, 2) < maxDistance) {
						player.playerNetServerHandler.sendPacketToPlayer(packet);
					}
				}
			}
		}
	}
	
	public void addEventHandler(Container eventHandler){
		this.eventHandler = eventHandler;
	}

	@SideOnly(Side.CLIENT)
	@ForgeSubscribe(priority = EventPriority.NORMAL)
	public void focusChangeEvent(ConverterFocusEvent event) {
		this.focusValue = event.getNewValue();
	}

	@SideOnly(Side.CLIENT)
	@ForgeSubscribe(priority = EventPriority.NORMAL)
	public void valueChangeEvent(ConverterValueEvent event) {
		this.storedValue = event.getNewValue();
	}
}
