/**
 * 
 */
package comparableconversion.common.command;

import comparableconversion.common.ComparableConversion;

import net.minecraft.src.CommandBase;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ICommandSender;


import cpw.mods.fml.common.FMLCommonHandler;

/**
 * @author <a href="mailto:e83800@wnco.com">Chris Bandy</a>
 * Created on: Oct 4, 2012
 */
public abstract class AbstractAdminCommand extends CommandBase {
	private final ComparableConversion mod;

	public AbstractAdminCommand(ComparableConversion mod) {
		this.mod = mod;
	}

	@Override
	public abstract String getCommandName();

	@Override
	public abstract void processCommand(ICommandSender commandSender, String[] commandOpts);

	@Override
	public boolean canCommandSenderUseCommand(ICommandSender commandSender) {
		if (commandSender instanceof EntityPlayer && !mod.isDebug()){
			EntityPlayer player = (EntityPlayer)commandSender;
			return FMLCommonHandler.instance().getMinecraftServerInstance().getConfigurationManager().getOps().contains(player.toString());
		}
		
		return true;
	}
	
	public ComparableConversion getMod(){
		return this.mod;
	}
}
