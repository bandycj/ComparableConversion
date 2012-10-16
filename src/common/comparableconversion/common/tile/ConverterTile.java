/**
 * 
 */
package comparableconversion.common.tile;

import net.minecraft.src.Chunk;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.EntityPlayerMP;
import net.minecraft.src.IInventory;
import net.minecraft.src.ItemStack;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.NBTTagList;
import net.minecraft.src.Packet;
import net.minecraft.src.TileEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.EventPriority;
import net.minecraftforge.event.ForgeSubscribe;

import comparableconversion.common.ComparableConversion;
import comparableconversion.common.block.ConverterBlock;
import comparableconversion.common.events.ConverterValueEvent;
import comparableconversion.common.utils.PacketHandler;
import comparableconversion.common.utils.ValueModel;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Side;
import cpw.mods.fml.common.asm.SideOnly;

/**
 * @author <a href="mailto:e83800@wnco.com">Chris Bandy</a> Created on: Oct 3,
 *         2012
 */
public class ConverterTile extends TileEntity implements IInventory {
	public static final int CONVERTER_SLOT = 0;
	public static final int FOCUS_SLOT = 1;

	private final ItemStack[] inv;
	private Integer storedValue;
	private Integer focusValue;
	private final ValueModel valueModel;

	private EntityPlayerMP thePlayer;
	private int timeSinceLastForceSave = 0;
	private boolean valueChanged = false;

	/**
	 * The number of ticks that a fresh copy of the currently-burning item would
	 * keep the take the converter to reduce
	 */
	public int currentItemBurnTime = 0;

	/** The number of ticks that the current item has been cooking for */
	public int converterCookTime = 0;

	public ConverterTile() {
		this.inv = new ItemStack[2];
		this.storedValue = 0;
		this.focusValue = 0;
		this.valueModel = ValueModel.getInstance(ComparableConversion.instance);
		MinecraftForge.EVENT_BUS.register(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void updateEntity() {
		super.updateEntity();
		this.timeSinceLastForceSave++;
		if ((this.valueChanged && this.timeSinceLastForceSave > 20) || this.timeSinceLastForceSave > 6000) {
			forceChunkSave();
		}
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
		return 1;
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer player) {
		return worldObj.getBlockTileEntity(xCoord, yCoord, zCoord) == this
				&& player.getDistanceSq(xCoord + 0.5, yCoord + 0.5, zCoord + 0.5) < 64
				&& (thePlayer != null && thePlayer == player);
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

		return stack;
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

		this.storedValue = tagCompound.getInteger("StoredValue");
		NBTTagList tagList = tagCompound.getTagList("Inventory");
		for (int i = 0; i < tagList.tagCount(); i++) {
			NBTTagCompound tag = (NBTTagCompound) tagList.tagAt(i);
			byte slot = tag.getByte("Slot");
			if (slot >= 0 && slot < inv.length) {
				inv[slot] = ItemStack.loadItemStackFromNBT(tag);
			}
		}
		ComparableConversion.instance.debug("Read NBT: " + tagCompound);
	}

	@Override
	public void writeToNBT(NBTTagCompound tagCompound) {
		super.writeToNBT(tagCompound);

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
		tagCompound.setInteger("StoredValue", getStoredValue());
		ComparableConversion.instance.debug("Write NBT: " + tagCompound);
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
			if (progress > max){
				progress = max;
			}
			return progress;
		} catch (ArithmeticException ex) {
			return 0;
		}
	}

	public void setPlayer(EntityPlayer player) {
		if (player instanceof EntityPlayerMP) {
			this.thePlayer = (EntityPlayerMP) player;
			sendPacketToPlayer();
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
		ComparableConversion.instance.debug("setFocusValue: " + this.focusValue + "->" + newValue);
		this.focusValue = newValue;
	}

	public void increaseStoredValue(ItemStack item) {
		if (item != null) {
			setStoredValue(getStoredValue() + valueModel.getValue(item));
		}
	}

	public void reduceStoredValue(ItemStack item) {
		reduceStoredValue(valueModel.getValue(item));
	}

	public void reduceStoredValue(int amount) {
		int newValue = getStoredValue() - amount;
		if (amount > 0 && newValue >= 0) {
			setStoredValue(newValue);
		}
	}

	private void setStoredValue(int newValue) {
		Side side = FMLCommonHandler.instance().getEffectiveSide();
		if (side == Side.SERVER) {
			ComparableConversion.instance.debug("setStoredValue: " + this.storedValue + "->" + newValue);
			this.storedValue = newValue;
			this.valueChanged = true;
			sendPacketToPlayer();
		}
	}

	private void forceChunkSave() {
		Chunk chunk = this.worldObj.getChunkFromBlockCoords(xCoord, yCoord);
		chunk.setChunkModified();
		chunk.needsSaving(true);

		this.worldObj.getChunkProvider().saveChunks(false, null);
		this.timeSinceLastForceSave = 0;
		this.valueChanged = false;
	}

	private void sendPacketToPlayer() {
		Side side = FMLCommonHandler.instance().getEffectiveSide();
		if (side == Side.SERVER && thePlayer != null) {
			Packet valuePacket = PacketHandler.assembleValuePacket(getStoredValue(), this.xCoord, this.yCoord,
					this.zCoord);
			PacketHandler.sendPacketToPlayer(thePlayer, valuePacket);
		}
	}

	@SideOnly(Side.CLIENT)
	@ForgeSubscribe(priority = EventPriority.NORMAL)
	public void valueChangeEvent(ConverterValueEvent event) {
		if (this.xCoord == event.getX() && this.yCoord == event.getY() && this.zCoord == event.getZ()) {
			ComparableConversion.instance.debug("valueChangeEvent: " + this.storedValue + "->" + event.getNewValue());
			this.storedValue = event.getNewValue();
		}
	}
}
