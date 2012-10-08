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
import comparableconversion.common.InfuseableGem;
import comparableconversion.common.ValueModel;
import comparableconversion.common.block.ReducerBlock;
import cpw.mods.fml.common.Side;
import cpw.mods.fml.common.asm.SideOnly;

/**
 * @author <a href="mailto:e83800@wnco.com">Chris Bandy</a> Created on: Oct 3,
 *         2012
 */
public class ReducerTile extends TileEntity implements IInventory, ISidedInventory {
	public static final int REDUCER_SLOT = 0;
	public static final int GEM_SLOT = 1;

	private ItemStack[] inv;

	/** The number of ticks that the reducer will keep burning */
	public int reducerBurnTime = 0;

	/**
	 * The number of ticks that a fresh copy of the currently-burning item would
	 * keep the take the reducer to reduce
	 */
	public int currentItemBurnTime = 0;

	/** The number of ticks that the current item has been cooking for */
	public int reducerCookTime = 0;

	public ReducerTile() {
		inv = new ItemStack[2];
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

		this.reducerBurnTime = tagCompound.getShort("BurnTime");
		this.reducerCookTime = tagCompound.getShort("CookTime");
		this.currentItemBurnTime = getItemBurnTime(this.inv[REDUCER_SLOT]);
	}

	@Override
	public void writeToNBT(NBTTagCompound tagCompound) {
		super.writeToNBT(tagCompound);
		tagCompound.setShort("BurnTime", (short) this.reducerBurnTime);
		tagCompound.setShort("CookTime", (short) this.reducerCookTime);

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
		return ReducerBlock.class.toString();
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
		boolean var2 = false;

		if (this.reducerBurnTime > 0) {
			--this.reducerBurnTime;
		}

		if (!this.worldObj.isRemote) {
			if (this.reducerBurnTime == 0 && this.canReduce()) {
				this.currentItemBurnTime = this.reducerBurnTime = getItemBurnTime(this.inv[REDUCER_SLOT]);

				if (this.reducerBurnTime > 0) {
					var2 = true;

					if (this.inv[REDUCER_SLOT] != null) {
						--this.inv[REDUCER_SLOT].stackSize;

						if (this.inv[REDUCER_SLOT].stackSize == 0) {
							this.inv[REDUCER_SLOT] = this.inv[REDUCER_SLOT].getItem().getContainerItemStack(inv[REDUCER_SLOT]);
						}
					}
				}
			}

			if (this.isBurning() && this.canReduce()) {
				++this.reducerCookTime;

				if (this.reducerCookTime == 200) {
					this.reducerCookTime = 0;
					this.reduceItem();
					var2 = true;
				}
			} else {
				this.reducerCookTime = 0;
			}
		}

		if (var2) {
			this.onInventoryChanged();
		}
	}

	/**
	 * Returns an integer between 0 and the passed value representing how close
	 * the current item is to being completely cooked
	 */
	@SideOnly(Side.CLIENT)
	public int getCookProgressScaled(int par1) {
		return this.reducerCookTime * par1 / 200;
	}

	/**
	 * Returns an integer between 0 and the passed value representing how much
	 * burn time is left on the current fuel item, where 0 means that the item
	 * is exhausted and the passed value means that the item is fresh
	 */
	@SideOnly(Side.CLIENT)
	public int getBurnTimeRemainingScaled(int par1) {
		if (this.currentItemBurnTime == 0) {
			this.currentItemBurnTime = 200;
		}

		return this.reducerBurnTime * par1 / this.currentItemBurnTime;
	}

	/**
	 * Returns true if the reducer can reduce an item, i.e. has a source item,
	 * destination stack isn't full, etc.
	 */
	private boolean canReduce() {
		if (inv[REDUCER_SLOT] != null) {
			int value = ValueModel.getInstance(ComparableConversion.instance).getValue(inv[REDUCER_SLOT].getItem());
			ItemStack gem = inv[GEM_SLOT];
			if (gem != null) {
				return (value > 0 && (gem.getMaxDamage() - gem.getItemDamage()) > value);
			}
		}
		return false;
	}

	/**
	 * Returns the number of ticks that the supplied fuel item will keep the
	 * reducer burning, or 0 if the item isn't reduceable.
	 */
	public static int getItemBurnTime(ItemStack itemStack) {
		if (itemStack != null) {
			int value = ValueModel.getInstance(ComparableConversion.instance).getValue(itemStack.getItem()) / 50;
			if (value > 0) {
				return value;
			}
		}

		return 1;
	}

	/**
	 * Returns true if the reducer is currently burning
	 */
	public boolean isBurning() {
		return this.reducerBurnTime > 0;
	}

	/**
	 * Turn one item from the reducer source stack into the appropriate value of
	 * the gem.
	 */
	public void reduceItem() {
//		if (this.canReduce()) {
//			int value = ValueModel.getInstance(ComparableConversion.instance)
//					.getValue(this.inv[REDUCER_SLOT].getItem());
//
//			if (this.inv[GEM_SLOT] == null) {
//				return;
//			} else if (this.inv[GEM_SLOT].getItem() instanceof InfuseableGem) {
//				System.out.println("old damage:" + this.inv[GEM_SLOT].getItemDamage() + " new damage: "
//						+ (this.inv[GEM_SLOT].getItemDamage() - value));
//				this.inv[GEM_SLOT].setItemDamage(this.inv[GEM_SLOT].getItemDamage() - value);
//			}
//
//			--this.inv[REDUCER_SLOT].stackSize;
//
//			if (this.inv[REDUCER_SLOT].stackSize <= 0) {
//				this.inv[REDUCER_SLOT] = null;
//			}
//		}
	}
}
