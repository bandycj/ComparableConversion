/**
 * 
 */
package selurgniman.forge.comparableconversion.common.block;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.Material;
import net.minecraft.src.TileEntity;
import net.minecraft.src.World;
import selurgniman.forge.comparableconversion.common.ComparableConversion;
import selurgniman.forge.comparableconversion.common.tile.ReducerTile;
import cpw.mods.fml.common.Side;
import cpw.mods.fml.common.asm.SideOnly;

/**
 * @author <a href="mailto:e83800@wnco.com">Chris Bandy</a> Created on: Oct 3,
 *         2012
 */
public class ReducerBlock extends AbstractContainerBlock {
	private static final String NAME = "Reducer";
	public static final int GUID = 1;
	
	/**
	 * @param id
	 * @param material
	 */
	public ReducerBlock(int id, Material material) {
		super(id, material);
		blockIndexInTexture = 1;
		setHardness(2.0f);
		setResistance(5.0f);
		setStepSound(soundMetalFootstep);

		setBlockName(NAME);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getTextureFile() {
		return "/terrain.png";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getBlockTextureFromSide(int side) {
		switch (side) {
			case 0:
				return 21;
			case 1:
				return 21;
		}
		return blockIndexInTexture;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public TileEntity createNewTileEntity(World var1) {
		return new ReducerTile();
	}

	@SideOnly(Side.CLIENT)
	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int par6, float par7, float par8, float par9) {
		ReducerTile tileCalcinator = (ReducerTile) world.getBlockTileEntity(x, y, z);

		if (tileCalcinator != null) {
			player.openGui(ComparableConversion.instance, GUID, world, x, y, z);
		}

		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return NAME;
	}
}
