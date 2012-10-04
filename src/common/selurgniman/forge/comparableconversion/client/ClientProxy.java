/**
 * 
 */
package selurgniman.forge.comparableconversion.client;

import selurgniman.forge.comparableconversion.common.CommonProxy;

import net.minecraftforge.client.MinecraftForgeClient;

/**
 * @author <a href="mailto:e83800@wnco.com">Chris Bandy</a>
 * Created on: Oct 2, 2012
 */
public class ClientProxy extends CommonProxy {
	@Override
	public void registerRenderers() {
		MinecraftForgeClient.preloadTexture(GEMS_PNG);
	}
}
