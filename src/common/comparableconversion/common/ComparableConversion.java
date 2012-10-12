/**
 * 
 */
package comparableconversion.common;

import java.util.logging.Logger;

import net.minecraft.src.Block;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ICommandManager;
import net.minecraft.src.ItemStack;
import net.minecraft.src.ServerCommandManager;
import net.minecraft.src.ServerConfigurationManager;
import net.minecraftforge.common.Configuration;

import comparableconversion.common.block.ConverterBlock;
import comparableconversion.common.command.CCComand;
import comparableconversion.common.utils.Message;
import comparableconversion.common.utils.PacketHandler;
import comparableconversion.common.utils.ValueModel;

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
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.FMLRelauncher;

/**
 * @author <a href="mailto:e83800@wnco.com">Chris Bandy</a> Created on: Oct 2,
 *         2012
 */
@Mod(modid = "ComparableConversion", name = "ComparableConversion", version = "0.0.0")
@NetworkMod(clientSideRequired = true, serverSideRequired = true, channels = { PacketHandler.CHANNEL }, packetHandler = PacketHandler.class)
public class ComparableConversion {
	private static final Logger log = Logger.getLogger("Minecraft");

	private static final Block converterBlock = new ConverterBlock(4095);

	private boolean isDebug = false;

	// The instance of your mod that Forge uses.
	@Instance("ComparableConversion")
	public static ComparableConversion instance;

	// Says where the client and server 'proxy' code is loaded.
	@SidedProxy(clientSide = "comparableconversion.client.ClientProxy", serverSide = "comparableconversion.common.CommonProxy")
	public static CommonProxy proxy;

	private Configuration config = null;

	@PreInit
	public void preInit(FMLPreInitializationEvent event) {
		ComparableConversion.instance = this;
		this.config = new Configuration(event.getSuggestedConfigurationFile());
		this.config.load();
		this.isDebug = this.config.get("debug", "debug", false).getBoolean(false);

		this.config.save();
	}

	@Init
	public void load(FMLInitializationEvent event) {
		GameRegistry.registerBlock(converterBlock);

		ValueModel.getInstance(this);

		// Initialize mod tile entities
		proxy.initTileEntities();

		if (FMLRelauncher.side() == Side.CLIENT.toString()) {
			// Initialize custom rendering and pre-load textures (Client only)
			proxy.registerRenderers();
		}

		if (isDebug) {
			GameRegistry.addShapelessRecipe(new ItemStack(converterBlock), new ItemStack(Block.dirt));
			if (FMLRelauncher.side() == Side.SERVER.toString()) {
				initServerDebug();
			}
		}
		NetworkRegistry.instance().registerChannel(new PacketHandler(), PacketHandler.CHANNEL);
		NetworkRegistry.instance().registerGuiHandler(instance, proxy);
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
			ServerConfigurationManager configManager = FMLCommonHandler.instance().getMinecraftServerInstance()
					.getConfigurationManager();
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
