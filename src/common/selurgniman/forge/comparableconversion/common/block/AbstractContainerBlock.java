/**
 * 
 */
package selurgniman.forge.comparableconversion.common.block;

/**
 * @author <a href="mailto:e83800@wnco.com">Chris Bandy</a>
 * Created on: Oct 3, 2012
 */

import net.minecraft.src.BlockContainer;
import net.minecraft.src.Material;

public abstract class AbstractContainerBlock extends BlockContainer {

	public AbstractContainerBlock(int id, Material material) {
		super(id, material);
	}

}