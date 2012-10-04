/**
 * 
 */
package org.selurgniman.forge.comparableconversion.common.tile;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.FurnaceRecipes;
import net.minecraft.src.IInventory;
import net.minecraft.src.ItemRedstone;
import net.minecraft.src.ItemStack;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.common.ISidedInventory;

import org.selurgniman.forge.comparableconversion.common.block.ReducerBlock;

/**
 * @author <a href="mailto:e83800@wnco.com">Chris Bandy</a> Created on: Oct 3,
 *         2012
 */
public class ReducerTile extends AbstractTileEntity implements IInventory, ISidedInventory {

	/**
	 * The ItemStacks that hold the items currently being used in the Reducer
	 */
	private ReducerInventory reducerInventory = new ReducerInventory();

	/**
	 * Do not make give this method the name canInteractWith because it clashes
	 * with Container
	 */
	public boolean isUseableByPlayer(EntityPlayer par1EntityPlayer) {
		return this.worldObj.getBlockTileEntity(this.xCoord, this.yCoord, this.zCoord) != this ? false
				: par1EntityPlayer.getDistanceSq((double) this.xCoord + 0.5D, (double) this.yCoord + 0.5D,
						(double) this.zCoord + 0.5D) <= 64.0D;
	}

	/**
	 * Returns the number of ticks that the supplied fuel item will keep the
	 * furnace burning, or 0 if the item isn't fuel
	 */
	public static int getItemBurnTime(ItemStack fuelItemStack) {
		if (fuelItemStack != null && fuelItemStack.getItem() instanceof ItemRedstone) {
			return 2400;
		}
		
		return 0;
	}
	
    /**
     * Returns true if the furnace can smelt an item, i.e. has a source item, destination stack isn't full, etc.
     */
    private boolean canSmelt()
    {
        if (this.reducerInventory.getItem() == null)
        {
            return false;
        }
        else
        {
//            ItemStack var1 = FurnaceRecipes.smelting().getSmeltingResult(this.reducerInventory.getItem());
//            if (var1 == null) return false;
//            if (this.furnaceItemStacks[2] == null) return true;
//            if (!this.furnaceItemStacks[2].isItemEqual(var1)) return false;
//            int result = furnaceItemStacks[2].stackSize + var1.stackSize;
//            return (result <= getInventoryStackLimit() && result <= var1.getMaxStackSize());
        	return true;
        }
    }
    
	/**
     * Turn one item from the furnace source stack into the appropriate smelted item in the furnace result stack
     */
    public void smeltItem()
    {
        if (this.canSmelt())
        {
//            ItemStack var1 = FurnaceRecipes.smelting().getSmeltingResult(this.furnaceItemStacks[0]);
//
//            if (this.furnaceItemStacks[2] == null)
//            {
//                this.furnaceItemStacks[2] = var1.copy();
//            }
//            else if (this.furnaceItemStacks[2].isItemEqual(var1))
//            {
//                furnaceItemStacks[2].stackSize += var1.stackSize;
//            }
//
//            --this.furnaceItemStacks[0].stackSize;
//
//            if (this.furnaceItemStacks[0].stackSize <= 0)
//            {
//                this.furnaceItemStacks[0] = null;
//            }
        }
    }
    
    
	
    /**
     * Return true if item is a fuel source (getItemBurnTime() > 0).
     */
    public static boolean isItemFuel(ItemStack par0ItemStack)
    {
        return getItemBurnTime(par0ItemStack) > 0;
    }

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getSizeInventory() {
		return this.reducerInventory.getSize();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ItemStack getStackInSlot(int slot) {
		return this.reducerInventory.getSlot(slot);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ItemStack decrStackSize(int slot, int amount) {
		ItemStack item = getStackInSlot(slot);
		item.stackSize = item.stackSize - amount;
		if (item.stackSize < 0) {
			item.stackSize = 0;
		}
		return item;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ItemStack getStackInSlotOnClosing(int slot) {
		return getStackInSlot(slot);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setInventorySlotContents(int slot, ItemStack item) {
		this.reducerInventory.setSlotContents(slot, item);
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
	public int getInventoryStackLimit() {
		return 64;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void openChest() {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void closeChest() {
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

	private class ReducerInventory {
		private ItemStack item = null;
		private ItemStack fuel = null;
		private ItemStack result = null;
		private final int size = 3;
		
		public ItemStack getSlot(int slot){
			switch(slot){
				case 0: return item;
				case 1: return fuel;
				case 2: return result;
				default: return null;
			}
		}
		
		public void setSlotContents(int slot, ItemStack item){
			switch(slot){
				case 0: setItem(item);
				case 1: setFuel(item);
				case 2: setResult(item);
			}
		}
		
		/**
		 * @return the item
		 */
		public ItemStack getItem() {
			return item;
		}
		/**
		 * @param item the item to set
		 */
		public void setItem(ItemStack item) {
			this.item = item;
		}
		/**
		 * @return the fuel
		 */
		public ItemStack getFuel() {
			return fuel;
		}
		/**
		 * @param fuel the fuel to set
		 */
		public void setFuel(ItemStack fuel) {
			this.fuel = fuel;
		}
		/**
		 * @return the result
		 */
		public ItemStack getResult() {
			return result;
		}
		/**
		 * @param result the result to set
		 */
		public void setResult(ItemStack result) {
			this.result = result;
		}
		/**
		 * @return the size
		 */
		public int getSize() {
			return size;
		}
	}
}
