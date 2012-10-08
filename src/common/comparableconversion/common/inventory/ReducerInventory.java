/**
 * 
 */
package comparableconversion.common.inventory;

import net.minecraft.src.ItemStack;

/**
 * @author <a href="mailto:selurgniman@selurgniman.org">Selurgniman</a>
 *
 */
public class ReducerInventory {
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