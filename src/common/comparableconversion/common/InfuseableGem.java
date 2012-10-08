/**
 * 
 */
package comparableconversion.common;

import net.minecraft.src.CreativeTabs;
import net.minecraft.src.Item;

/**
 * @author <a href="mailto:e83800@wnco.com">Chris Bandy</a>
 * Created on: Oct 2, 2012
 */
public class InfuseableGem extends Item {

	/**
	 * @param par1
	 */
	protected InfuseableGem(int par1) {
		super(par1);
		// // Constructor Configuration
		setMaxStackSize(64);
		setCreativeTab(CreativeTabs.tabMisc);
		setIconIndex(0);
		setItemName("genericInfusableGem");	
	}

	public String getTextureFile() {
		return CommonProxy.GEMS_PNG;
	}
}
