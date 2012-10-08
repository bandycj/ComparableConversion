/**
 * 
 */
package comparableconversion.common;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.TileEntity;
import net.minecraft.src.World;

import comparableconversion.client.gui.ReducerGui;
import comparableconversion.common.container.ReducerContainer;
import comparableconversion.common.tile.ReducerTile;

import cpw.mods.fml.common.network.IGuiHandler;
import cpw.mods.fml.common.registry.GameRegistry;

/**
 * @author <a href="mailto:e83800@wnco.com">Chris Bandy</a> Created on: Oct 2,
 *         2012
 */
public class CommonProxy implements IGuiHandler {
	public static final String GEMS_PNG = "/comparableconversion/resources/gems.png";
	public static final String REDUCER_BLOCK_PNG = "/comparableconversion/resources/reducerBlock.png";
	public static final String REDUCER_GUI_PNG = "/comparableconversion/resources/reducerGui.png";

	public static int reducerRenderId = -1;

	public void registerRenderers() {
	}

	public void initTileEntities() {
		GameRegistry.registerTileEntity(ReducerTile.class, "reducerTile");
	}

	// returns an instance of the Container you made earlier
	@Override
	public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
		TileEntity tileEntity = world.getBlockTileEntity(x, y, z);
		if (tileEntity instanceof ReducerTile) {
			return new ReducerContainer(player.inventory, (ReducerTile) tileEntity);
		}
		return null;
	}

	// returns an instance of the Gui you made earlier
	@Override
	public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
		TileEntity tileEntity = world.getBlockTileEntity(x, y, z);
		if (tileEntity instanceof ReducerTile) {
			return new ReducerGui(player.inventory, (ReducerTile) tileEntity);
		}
		return null;
	}
}
