/**
 * 
 */
package comparableconversion.common.command;

import comparableconversion.common.ComparableConversion;

import net.minecraft.src.Block;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ICommandSender;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;

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
		if (canCommandSenderUseCommand(commandSender) && commandOpts.length > 0) {
			switch (commandOpts[0]) {
				case "debug":
					processDebugCommand(commandSender, commandOpts);
					break;
				case "god":
					processGodCommand(commandSender, commandOpts);
					break;
				case "fly":
					processFlyCommand(commandSender, commandOpts);
					break;
				case "give":
					processGiveCommand(commandSender, commandOpts);
					break;
			}

		}
	}

	private void processDebugCommand(ICommandSender commandSender, String[] commandOpts) {
		if (commandOpts.length > 1) {

		} else {
			if (commandSender instanceof EntityPlayer) {
				ComparableConversion.messagePlayer(getCommandSenderAsPlayer(commandSender), "debug is: "
						+ getMod().isDebug());
			} else {
				ComparableConversion.log("debug is: " + getMod().isDebug());
			}
		}
	}

	private void processGodCommand(ICommandSender commandSender, String[] commandOpts) {
		if (commandSender instanceof EntityPlayer) {
			EntityPlayer player = getCommandSenderAsPlayer(commandSender);
			if (player.capabilities.disableDamage) {
				player.capabilities.disableDamage = false;
			} else {
				player.capabilities.disableDamage = true;
			}

			ComparableConversion.messagePlayer(player, "god is: " + player.capabilities.disableDamage);
		}
	}

	private void processFlyCommand(ICommandSender commandSender, String[] commandOpts) {
		if (commandSender instanceof EntityPlayer) {
			EntityPlayer player = getCommandSenderAsPlayer(commandSender);
			if (player.capabilities.allowFlying) {
				player.capabilities.allowFlying = false;
			} else {
				player.capabilities.allowFlying = true;
			}

			ComparableConversion.messagePlayer(player, "flight is: " + player.capabilities.allowFlying);
		}
	}

	private void processGiveCommand(ICommandSender commandSender, String[] commandOpts) {
		if (commandSender instanceof EntityPlayer && commandOpts.length > 1) {
			EntityPlayer player = getCommandSenderAsPlayer(commandSender);
			ItemStack giveItem = null;
			String giveItemName = commandOpts[1];
			int amount = 1;
			if (commandOpts.length > 2) {
				try {
					amount = Integer.parseInt(commandOpts[2]);
				} catch (NumberFormatException ex) {
					giveItemName = giveItemName+commandOpts[2];
				}
			}
			for (Item item : Item.itemsList) {
				if (item != null && item.getItemName() != null
						&& giveItemName.equalsIgnoreCase(item.getItemName().replace("item.", ""))) {
					giveItem = new ItemStack(item, amount);
					break;
				}
			}
			if (giveItem == null) {
				for (Block block : Block.blocksList) {
					if (block != null && block.getBlockName() != null
							&& giveItemName.equalsIgnoreCase(block.getBlockName().replace("tile.", ""))) {
						giveItem = new ItemStack(block, amount);
						break;
					}
				}
			}
			if (giveItem != null) {
				player.inventory.addItemStackToInventory(giveItem);
				ComparableConversion.messagePlayer(player, "giving you " + amount + " of " + giveItemName);
			} else {
				ComparableConversion.messagePlayer(player, "coulnd't find " + giveItemName);
			}
		}
	}
}
