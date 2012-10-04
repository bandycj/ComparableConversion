/**
 * 
 */
package org.selurgniman.forge.comparableconversion.common.command;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ICommandSender;

import org.selurgniman.forge.comparableconversion.common.ComparableConversion;

/**
 * @author <a href="mailto:e83800@wnco.com">Chris Bandy</a> Created on: Oct 4,
 *         2012
 */
public class CCComand extends AbstractAdminCommand {
	public CCComand(ComparableConversion mod) {
		super(mod);
	}

	@Override
	public String getCommandName() {
		return "cc";
	}

	@Override
	public void processCommand(ICommandSender commandSender, String[] commandOpts) {
		if (commandOpts.length > 0) {
			switch (commandOpts[0]) {
				case "debug":
					processDebugCommand(commandSender, commandOpts);
					break;
			}

		}
	}

	private void processDebugCommand(ICommandSender commandSender, String[] commandOpts) {
		if (commandOpts.length > 1){
			
		} else {
			if (commandSender instanceof EntityPlayer) {
				ComparableConversion.messagePlayer(getCommandSenderAsPlayer(commandSender), "debug is: " + getMod().isDebug());
			} else {
				ComparableConversion.log("debug is: " + getMod().isDebug());
			}
		}
	}
}
