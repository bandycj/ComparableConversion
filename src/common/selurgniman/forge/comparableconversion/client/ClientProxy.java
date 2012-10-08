/**
 * 
 */
package selurgniman.forge.comparableconversion.client;

import net.minecraftforge.client.MinecraftForgeClient;
import selurgniman.forge.comparableconversion.client.render.RenderReducer;
import selurgniman.forge.comparableconversion.common.CommonProxy;
import selurgniman.forge.comparableconversion.common.tile.ReducerTile;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;

/**
 * @author <a href="mailto:e83800@wnco.com">Chris Bandy</a>
 * Created on: Oct 2, 2012
 */
public class ClientProxy extends CommonProxy {
	
	@Override
	public void registerRenderers() {
		MinecraftForgeClient.preloadTexture(GEMS_PNG);
		MinecraftForgeClient.preloadTexture(REDUCER_BLOCK_PNG);
		MinecraftForgeClient.preloadTexture(REDUCER_GUI_PNG);
		
		reducerRenderId = RenderingRegistry.getNextAvailableRenderId();
	}
	    
    @Override
    public void initTileEntities() {
    	super.initTileEntities();
    	
    	ClientRegistry.bindTileEntitySpecialRenderer(ReducerTile.class, new RenderReducer());
    	
    }
}
