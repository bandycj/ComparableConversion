/**
 * 
 */
package comparableconversion.client.render;

import comparableconversion.common.tile.ConverterTile;

import net.minecraft.src.TileEntity;
import net.minecraft.src.TileEntitySpecialRenderer;

/**
 * @author <a href="mailto:selurgniman@selurgniman.org">Selurgniman</a>
 *
 */
public class ConverterRender extends TileEntitySpecialRenderer {

    static final float scale = (float) (1.0 / 16.0);
    
	private ConverterModel reducerModel = new ConverterModel(scale);

	@Override
	public void renderTileEntityAt(TileEntity tileEntity, double x, double y, double z, float tick) {
		reducerModel.render((ConverterTile)tileEntity, x, y, z);
	}

}