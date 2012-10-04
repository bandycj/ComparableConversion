/**
 * 
 */
package org.selurgniman.forge.comparableconversion.common.block;

import net.minecraft.src.Material;
import net.minecraft.src.TileEntity;
import net.minecraft.src.World;

/**
 * @author <a href="mailto:e83800@wnco.com">Chris Bandy</a>
 * Created on: Oct 3, 2012
 */
public class ReducerBlock extends AbstractContainerBlock{
	private static final String NAME = "Reducer";
	
	/**
	 * @param id
	 * @param material
	 */
	public ReducerBlock(int id, Material material) {
		super(id, material);
		// TODO Auto-generated constructor stub
	}

	
	
	/* (non-Javadoc)
	 * @see net.minecraft.src.BlockContainer#createNewTileEntity(net.minecraft.src.World)
	 */
	@Override
	public TileEntity createNewTileEntity(World var1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String toString(){
		return NAME;
	}
}
