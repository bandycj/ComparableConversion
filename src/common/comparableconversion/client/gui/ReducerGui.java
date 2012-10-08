/**
 * 
 */
package comparableconversion.client.gui;

import net.minecraft.src.GuiContainer;
import net.minecraft.src.InventoryPlayer;
import net.minecraft.src.StatCollector;

import org.lwjgl.opengl.GL11;

import comparableconversion.common.CommonProxy;
import comparableconversion.common.container.ReducerContainer;
import comparableconversion.common.tile.ReducerTile;


/**
 * @author <a href="mailto:selurgniman@selurgniman.org">Selurgniman</a>
 *
 */
public class ReducerGui extends GuiContainer {

	private ReducerTile reducer;
	
    public ReducerGui(InventoryPlayer player, ReducerTile reducer) {
        super(new ReducerContainer(player, reducer));
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
        
        int burnTimeRemaining;
        if (this.reducer.isBurning())
        {
            burnTimeRemaining = this.reducer.getBurnTimeRemainingScaled(12);
            this.drawTexturedModalRect(var5 + 56, var6 + 36 + 12 - burnTimeRemaining, 176, 12 - burnTimeRemaining, 14, burnTimeRemaining + 2);
        }

        burnTimeRemaining = this.reducer.getCookProgressScaled(24);
        this.drawTexturedModalRect(var5 + 79, var6 + 34, 176, 14, burnTimeRemaining + 1, 16);
    }
    
}