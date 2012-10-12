/**
 * 
 */
package comparableconversion.client.gui;

import net.minecraft.src.GuiContainer;
import net.minecraft.src.InventoryPlayer;
import net.minecraft.src.StatCollector;

import org.lwjgl.opengl.GL11;

import comparableconversion.common.CommonProxy;
import comparableconversion.common.container.ConverterContainer;
import comparableconversion.common.tile.ConverterTile;


/**
 * @author <a href="mailto:selurgniman@selurgniman.org">Selurgniman</a>
 *
 */
public class ConverterGui extends GuiContainer {

	private ConverterTile reducer;
	
    public ConverterGui(InventoryPlayer player, ConverterTile reducer) {
        super(new ConverterContainer(player, reducer));
        this.ySize = 176;
        this.reducer = reducer;
    }
    
    protected void drawGuiContainerForegroundLayer()
    {
        this.fontRenderer.drawString("Reducer", 60, 6, 4210752);
        this.fontRenderer.drawString(StatCollector.translateToLocal("container.inventory"), 8, this.ySize - 96 + 2, 4210752);
    }

    protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3)
    {
        int texture = this.mc.renderEngine.getTexture(CommonProxy.REDUCER_GUI_PNG);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.renderEngine.bindTexture(texture);
        int var5 = (this.width - this.xSize) / 2;
        int var6 = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(var5, var6, 0, 0, this.xSize, this.ySize);

        int progress = this.reducer.getCookProgressScaled(24);
        this.drawTexturedModalRect(var5 + 84, var6 + 41 -8, 176, 14, progress + 1, 16);
    }
    
}