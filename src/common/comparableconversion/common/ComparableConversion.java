/**
 * 
 */
package comparableconversion.common;

import java.util.logging.Logger;

import comparableconversion.common.block.ReducerBlock;
import comparableconversion.common.command.CCComand;

import net.minecraft.src.Block;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ICommandManager;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;
import net.minecraft.src.Material;
import net.minecraft.src.ServerCommandManager;
import net.minecraft.src.ServerConfigurationManager;
import net.minecraftforge.common.Configuration;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.Init;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.Mod.PostInit;
import cpw.mods.fml.common.Mod.PreInit;
import cpw.mods.fml.common.Side;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.asm.SideOnly;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import cpw.mods.fml.relauncher.FMLRelauncher;

/**
 * @author <a href="mailto:e83800@wnco.com">Chris Bandy</a> Created on: Oct 2,
 *         2012
 */
@Mod(modid = "ComparableConversion", name = "ComparableConversion", version = "0.0.0")
@NetworkMod(clientSideRequired = true, serverSideRequired = true)
public class ComparableConversion {
	private static final Logger log = Logger.getLogger("Minecraft");
	private static final Item rubyGem = new InfuseableGem(5001).setMaxStackSize(64).setIconIndex(0).setItemName("rubyGem").setMaxDamage(200000);
	private static final Item emeraldGem = new InfuseableGem(5002).setMaxStackSize(64).setIconIndex(1).setItemName("emeraldGem").setMaxDamage(3200000);
	private static final Item diamondGem = new InfuseableGem(5003).setMaxStackSize(64).setIconIndex(2).setItemName("diamondGem").setMaxDamage(51200000);
	private static final Block reducerBlock = new ReducerBlock(4095, Material.rock);

	private boolean isDebug = false;

	// The instance of your mod that Forge uses.
	@Instance("Generic")
	public static ComparableConversion instance;

	// Says where the client and server 'proxy' code is loaded.
	@SidedProxy(clientSide = "selurgniman.forge.comparableconversion.client.ClientProxy", serverSide = "selurgniman.forge.comparableconversion.common.CommonProxy")
	public static CommonProxy proxy;

	private Configuration config = null;

	@PreInit
	public void preInit(FMLPreInitializationEvent event) {
		ComparableConversion.instance = this;
		this.config = new Configuration(event.getSuggestedConfigurationFile());
		this.config.load();
		this.isDebug = this.config.get("debug", "debug", false).getBoolean(false);
		ValueModel.getInstance(this);
		this.config.save();
	}

	@Init
	public void load(FMLInitializationEvent event) {
		LanguageRegistry.addName(rubyGem, "Infuseable Ruby");
		LanguageRegistry.addName(emeraldGem, "Infusable Emerald");
		LanguageRegistry.addName(diamondGem, "Infusable Diamond");

		ItemStack ruby = new ItemStack(rubyGem);
		ItemStack rubyStack = new ItemStack(rubyGem, 9);
		ItemStack emerald = new ItemStack(emeraldGem);
		ItemStack emeraldStack = new ItemStack(emeraldGem, 9);
		ItemStack diamond = new ItemStack(diamondGem);

		// Crafting Recipes
		GameRegistry.addRecipe(emerald, "xxx", "xxx", "xxx", 'x', ruby);
		GameRegistry.addShapelessRecipe(rubyStack, emerald);
		GameRegistry.addRecipe(diamond, "xxx", "xxx", "xxx", 'x', emerald);
		GameRegistry.addShapelessRecipe(emeraldStack, diamond);

		GameRegistry.addSmelting(Block.stone.blockID, new ItemStack(Block.stoneBrick), 0.1f);

		GameRegistry.registerBlock(reducerBlock);

		// Initialize mod tile entities
        proxy.initTileEntities();
        
        if (FMLRelauncher.side() == Side.CLIENT.toString()) {
	        // Initialize custom rendering and pre-load textures (Client only)
	        proxy.registerRenderers();
        }
        
		if (isDebug) {
			GameRegistry.addShapelessRecipe(new ItemStack(reducerBlock), new ItemStack(Block.dirt));
			if (FMLRelauncher.side() == Side.SERVER.toString()) {
				initServerDebug();
			}
		}
	}

	@PostInit
	public void postInit(FMLPostInitializationEvent event) {
		// Stub Method
	}

	@SideOnly(Side.SERVER)
	private void initServerDebug() {
		ICommandManager commandManager = FMLCommonHandler.instance().getMinecraftServerInstance().getCommandManager();
		if (commandManager instanceof ServerCommandManager) {
			((ServerCommandManager) commandManager).registerCommand(new CCComand(this));
		}
	}

	public Configuration getConfig() {
		return this.config;
	}

	public static void messagePlayer(EntityPlayer player, String message) {
		player.sendChatToPlayer(Message.PREFIX + " " + message);
	}

	public static void log(String message) {
		log.info(Message.PREFIX + " " + message);
	}

	public void debug(String message) {
		if (isDebug) {
			ServerConfigurationManager configManager = FMLCommonHandler.instance().getMinecraftServerInstance().getConfigurationManager();
			for (Object player : configManager.playerEntityList) {
				messagePlayer((EntityPlayer) player, Message.DEBUG_MESSAGE + message);
			}
			log(Message.DEBUG_MESSAGE + message);
		}
	}

	public boolean isDebug() {
		return this.isDebug;
	}
}
