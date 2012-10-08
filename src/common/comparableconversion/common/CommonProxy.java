/**
 * 
 */
package comparableconversion.common;

import comparableconversion.client.gui.ReducerGui;
import comparableconversion.common.block.ReducerBlock;
import comparableconversion.common.container.ReducerContainer;
import comparableconversion.common.tile.ReducerTile;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.World;
import cpw.mods.fml.common.network.IGuiHandler;
import cpw.mods.fml.common.registry.GameRegistry;

/**
 * @author <a href="mailto:e83800@wnco.com">Chris Bandy</a> Created on: Oct 2,
 *         2012
 */
public class CommonProxy implements IGuiHandler {
	public static final String GEMS_PNG = "/selurgniman/forge/comparableconversion/resources/gems.png";
	public static final String REDUCER_BLOCK_PNG = "/selurgniman/forge/comparableconversion/resources/reducerBlock.png";
	public static final String REDUCER_GUI_PNG = "/selurgniman/forge/comparableconversion/resources/reducerGui.png";

	public static int reducerRenderId = -1;

	public void registerRenderers() {
	}

	public void initTileEntities() {
		GameRegistry.registerTileEntity(ReducerTile.class, "reducerTile");
	}

	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		if (ID == ReducerBlock.GUID) {
			ReducerTile reducer = (ReducerTile) world.getBlockTileEntity(x, y, z);
			return new ReducerContainer(player.inventory, reducer);
		}

		return null;
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		if (ID == ReducerBlock.GUID) {
			ReducerTile reducer = (ReducerTile) world.getBlockTileEntity(x, y, z);
			return new ReducerGui(player.inventory, reducer);
		}

		return null;
	}
}
