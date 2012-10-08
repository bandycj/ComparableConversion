/**
 * 
 */
package comparableconversion.client.render;

import comparableconversion.common.tile.ReducerTile;

import net.minecraft.src.TileEntity;
import net.minecraft.src.TileEntitySpecialRenderer;

/**
 * @author <a href="mailto:selurgniman@selurgniman.org">Selurgniman</a>
 *
 */
public class RenderReducer extends TileEntitySpecialRenderer {

    static final float scale = (float) (1.0 / 16.0);
    
	private ReducerModel reducerModel = new ReducerModel(scale);

	@Override
	public void renderTileEntityAt(TileEntity tileEntity, double x, double y, double z, float tick) {
		reducerModel.render((ReducerTile)tileEntity, x, y, z);
	}

}