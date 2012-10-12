/**
 * 
 */
package comparableconversion.common.utils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import comparableconversion.common.ComparableConversion;

import net.minecraft.src.Block;
import net.minecraft.src.CraftingManager;
import net.minecraft.src.IRecipe;
import net.minecraft.src.Item;
import net.minecraft.src.ItemBlock;
import net.minecraft.src.ItemStack;
import net.minecraft.src.ShapedRecipes;
import net.minecraft.src.ShapelessRecipes;
import net.minecraftforge.common.Configuration;

/**
 * @author <a href="mailto:e83800@wnco.com">Chris Bandy</a> Created on: Oct 3,
 *         2012
 */
public class ValueModel {
	private static final String CONFIG_CATEGORY = "values";
	private final ComparableConversion mod;
	private final ArrayList<ItemValue> itemValues;
	private final ArrayList<BlockValue> blockValues;

	private static ValueModel instance = null;

	public static ValueModel getInstance(ComparableConversion mod) {
		if (instance == null) {
			instance = new ValueModel(mod);
		}
		return instance;
	}

	private ValueModel(ComparableConversion mod) {
		this.mod = mod;
		this.itemValues = new ArrayList<ItemValue>();
		this.blockValues = new ArrayList<BlockValue>();

		loadMap();
	}

	private void loadMap() {
		Configuration config = mod.getConfig();
		this.blockValues.add(new BlockValue(Block.stone, config.get(CONFIG_CATEGORY, Block.stone.getBlockName(), 1)
				.getInt()));
		this.blockValues.add(new BlockValue(Block.grass, config.get(CONFIG_CATEGORY, Block.grass.getBlockName(), 1)
				.getInt()));
		this.blockValues.add(new BlockValue(Block.dirt, config.get(CONFIG_CATEGORY, Block.dirt.getBlockName(), 1)
				.getInt()));
		this.blockValues.add(new BlockValue(Block.cobblestone, config.get(CONFIG_CATEGORY,
				Block.cobblestone.getBlockName(), 1).getInt()));
		this.blockValues.add(new BlockValue(Block.wood, config.get(CONFIG_CATEGORY, Block.wood.getBlockName(), 32)
				.getInt()));
		this.blockValues.add(new BlockValue(Block.sapling, config
				.get(CONFIG_CATEGORY, Block.sapling.getBlockName(), 32).getInt()));
		this.blockValues.add(new BlockValue(Block.sand, config.get(CONFIG_CATEGORY, Block.sand.getBlockName(), 1)
				.getInt()));
		this.blockValues.add(new BlockValue(Block.gravel, config.get(CONFIG_CATEGORY, Block.gravel.getBlockName(), 4)
				.getInt()));
		this.blockValues.add(new BlockValue(Block.leaves, config.get(CONFIG_CATEGORY, Block.leaves.getBlockName(), 1)
				.getInt()));
		this.blockValues.add(new BlockValue(Block.web, config.get(CONFIG_CATEGORY, Block.web.getBlockName(), 12)
				.getInt()));
		this.blockValues.add(new BlockValue(Block.obsidian, config.get(CONFIG_CATEGORY, Block.obsidian.getBlockName(),
				64).getInt()));
		this.blockValues.add(new BlockValue(Block.snow, config.get(CONFIG_CATEGORY, Block.snow.getBlockName(), 1)
				.getInt()));
		this.blockValues.add(new BlockValue(Block.ice, config.get(CONFIG_CATEGORY, Block.ice.getBlockName(), 1)
				.getInt()));
		this.blockValues.add(new BlockValue(Block.cactus, config.get(CONFIG_CATEGORY, Block.cactus.getBlockName(), 8)
				.getInt()));
		this.blockValues.add(new BlockValue(Block.pumpkin, config.get(CONFIG_CATEGORY, Block.pumpkin.getBlockName(),
				144).getInt()));
		this.blockValues.add(new BlockValue(Block.netherrack, config.get(CONFIG_CATEGORY,
				Block.netherrack.getBlockName(), 1).getInt()));
		this.blockValues.add(new BlockValue(Block.melon, config.get(CONFIG_CATEGORY, Block.melon.getBlockName(), 144)
				.getInt()));
		this.blockValues.add(new BlockValue(Block.oreLapis, config.get(CONFIG_CATEGORY, Block.oreLapis.getBlockName(),
				8).getInt()));
		this.blockValues.add(new BlockValue(Block.deadBush, config.get(CONFIG_CATEGORY, Block.deadBush.getBlockName(),
				1).getInt()));
		this.blockValues.add(new BlockValue(Block.plantYellow, config.get(CONFIG_CATEGORY,
				Block.plantYellow.getBlockName(), 16).getInt()));
		this.blockValues.add(new BlockValue(Block.plantRed, config.get(CONFIG_CATEGORY, Block.plantRed.getBlockName(),
				16).getInt()));
		this.blockValues.add(new BlockValue(Block.cobblestoneMossy, config.get(CONFIG_CATEGORY,
				Block.cobblestoneMossy.getBlockName(), 145).getInt()));
		this.blockValues.add(new BlockValue(Block.blockSnow, config.get(CONFIG_CATEGORY,
				Block.blockSnow.getBlockName(), 1).getInt()));
		this.blockValues.add(new BlockValue(Block.slowSand, config.get(CONFIG_CATEGORY, Block.slowSand.getBlockName(),
				49).getInt()));
		this.blockValues.add(new BlockValue(Block.netherBrick, config.get(CONFIG_CATEGORY,
				Block.netherBrick.getBlockName(), 4).getInt()));
		this.blockValues.add(new BlockValue(Block.mushroomBrown, config.get(CONFIG_CATEGORY,
				Block.mushroomBrown.getBlockName(), 32).getInt()));
		this.blockValues.add(new BlockValue(Block.mushroomRed, config.get(CONFIG_CATEGORY,
				Block.mushroomRed.getBlockName(), 32).getInt()));
		this.blockValues.add(new BlockValue(Block.brick, config.get(CONFIG_CATEGORY, Block.brick.getBlockName(), 64)
				.getInt()));

		this.itemValues.add(new ItemValue(Item.dyePowder, config
				.get(CONFIG_CATEGORY, Item.dyePowder.getItemName(), 144).getInt()));
		this.itemValues.add(new ItemValue(Item.redstone, config.get(CONFIG_CATEGORY, Item.redstone.getItemName(), 64)
				.getInt()));
		this.itemValues.add(new ItemValue(Item.ingotIron, config
				.get(CONFIG_CATEGORY, Item.ingotIron.getItemName(), 256).getInt()));
		this.itemValues.add(new ItemValue(Item.ingotGold, config.get(CONFIG_CATEGORY, Item.ingotGold.getItemName(),
				2048).getInt()));
		this.itemValues
				.add(new ItemValue(Item.stick, config.get(CONFIG_CATEGORY, Item.stick.getItemName(), 4).getInt()));
		this.itemValues
				.add(new ItemValue(Item.silk, config.get(CONFIG_CATEGORY, Item.silk.getItemName(), 12).getInt()));
		this.itemValues.add(new ItemValue(Item.gunpowder, config
				.get(CONFIG_CATEGORY, Item.gunpowder.getItemName(), 192).getInt()));
		this.itemValues.add(new ItemValue(Item.diamond, config.get(CONFIG_CATEGORY, Item.diamond.getItemName(), 8192)
				.getInt()));
		this.itemValues.add(new ItemValue(Item.emerald, config.get(CONFIG_CATEGORY, Item.emerald.getItemName(), 512)
				.getInt()));
		this.itemValues
				.add(new ItemValue(Item.coal, config.get(CONFIG_CATEGORY, Item.coal.getItemName(), 128).getInt()));
		this.itemValues
				.add(new ItemValue(Item.clay, config.get(CONFIG_CATEGORY, Item.clay.getItemName(), 16).getInt()));
		this.itemValues.add(new ItemValue(Item.lightStoneDust, config.get(CONFIG_CATEGORY,
				Item.lightStoneDust.getItemName(), 384).getInt()));
		this.itemValues
				.add(new ItemValue(Item.flint, config.get(CONFIG_CATEGORY, Item.flint.getItemName(), 4).getInt()));
		this.itemValues.add(new ItemValue(Item.feather, config.get(CONFIG_CATEGORY, Item.feather.getItemName(), 48)
				.getInt()));
		this.itemValues.add(new ItemValue(Item.leather, config.get(CONFIG_CATEGORY, Item.leather.getItemName(), 64)
				.getInt()));
		this.itemValues.add(new ItemValue(Item.wheat, config.get(CONFIG_CATEGORY, Item.wheat.getItemName(), 24)
				.getInt()));
		this.itemValues.add(new ItemValue(Item.appleRed, config.get(CONFIG_CATEGORY, Item.appleRed.getItemName(), 128)
				.getInt()));
		this.itemValues.add(new ItemValue(Item.appleGold, config.get(CONFIG_CATEGORY, Item.appleGold.getItemName(),
				1948).getInt()));
		this.itemValues.add(new ItemValue(Item.sugar, config.get(CONFIG_CATEGORY, Item.sugar.getItemName(), 32)
				.getInt()));
		this.itemValues.add(new ItemValue(Item.egg, config.get(CONFIG_CATEGORY, Item.egg.getItemName(), 32).getInt()));
		this.itemValues.add(new ItemValue(Item.blazeRod, config.get(CONFIG_CATEGORY, Item.blazeRod.getItemName(), 1536)
				.getInt()));
		this.itemValues.add(new ItemValue(Item.slimeBall, config.get(CONFIG_CATEGORY, Item.slimeBall.getItemName(), 24)
				.getInt()));
		this.itemValues.add(new ItemValue(Item.spiderEye, config
				.get(CONFIG_CATEGORY, Item.spiderEye.getItemName(), 128).getInt()));
		this.itemValues.add(new ItemValue(Item.bucketMilk, config.get(CONFIG_CATEGORY, Item.bucketMilk.getItemName(),
				833).getInt()));
		this.itemValues.add(new ItemValue(Item.enderPearl, config.get(CONFIG_CATEGORY, Item.enderPearl.getItemName(),
				1024).getInt()));

		try {
			for (Object obj : CraftingManager.getInstance().getRecipeList()) {
				if (obj instanceof IRecipe) {
					IRecipe recipe = (IRecipe) obj;
					if (!have(recipe.getRecipeOutput().getItem())) {
						calculateCost(recipe);
					}
				}
			}

			Collections.sort(itemValues, new Comparator<ItemValue>() {
				@Override
				public int compare(ItemValue o1, ItemValue o2) {
					return o1.getValue().compareTo(o2.getValue());
				}
			});
			Collections.sort(blockValues, new Comparator<BlockValue>() {
				@Override
				public int compare(BlockValue o1, BlockValue o2) {
					return o1.getValue().compareTo(o2.getValue());
				}
			});

			ComparableConversion.log("loaded " + itemValues.size() + " item values and " + blockValues.size()
					+ " block values.");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void calculateCost(IRecipe recipe) {
		try {
			List<ItemStack> ingredients = new ArrayList<ItemStack>();
			Field field = recipe.getClass().getDeclaredField("recipeItems");
			field.setAccessible(true);

			List<ItemStack> itemStacks = new ArrayList<ItemStack>();
			if (recipe instanceof ShapedRecipes) {
				itemStacks.addAll(Arrays.asList((ItemStack[]) field.get(recipe)));
			} else if (recipe instanceof ShapelessRecipes) {
				@SuppressWarnings("unchecked")
				List<ItemStack> list = (List<ItemStack>) field.get(recipe);
				itemStacks.addAll(list);
			}

			for (Object itemStack : itemStacks) {
				if (itemStack != null) {
					ingredients.add((ItemStack) itemStack);
				}
			}

			int total = 0;
			for (ItemStack ingredient : ingredients) {
				Item recipeItem = ingredient.getItem();
				if (!have(recipeItem)) {
					for (Object obj : CraftingManager.getInstance().getRecipeList()) {
						IRecipe newRecipe = (IRecipe) obj;
						if (newRecipe.getRecipeOutput().equals(ingredient)) {
							calculateCost(newRecipe);
						}
					}

				}

				try {
					total += getValue(recipeItem);
				} catch (NullPointerException ex) {
					mod.debug("Missing material: " + recipeItem);
				}
			}

			setValue(recipe.getRecipeOutput().getItem(), total);
		} catch (Exception e) {
			mod.debug(e.getMessage());
			e.printStackTrace();
		}
	}

	private boolean have(Item item) {
		return (getValue(item) > -1);
	}

	public int getValue(Item item) {
		return getValue(new ItemStack(item));
	}

	public Integer getValue(ItemStack itemStack) {
		Integer value = -1;
		if (itemStack != null) {
			Item item = itemStack.getItem();
			if (item instanceof ItemBlock) {
				ItemBlock itemBlock = (ItemBlock) item;
				Block block = Block.blocksList[itemBlock.getBlockID()];
				for (BlockValue blockValue : blockValues) {
					if (blockValue.getBlock().equals(block)) {
						return blockValue.getValue() * itemStack.stackSize;
					}
				}
			} else {
				for (ItemValue itemValue : itemValues) {
					if (itemValue.getItem().equals(item)) {
						return itemValue.getValue() * itemStack.stackSize;
					}
				}
			}
		}
		return value;
	}

	private void setValue(Item item, int value) {
		if (!have(item)) {
			if (item instanceof ItemBlock) {
				ItemBlock itemBlock = (ItemBlock) item;
				Block block = Block.blocksList[itemBlock.getBlockID()];
				blockValues.add(new BlockValue(block, value));
			} else {
				itemValues.add(new ItemValue(item, value));
			}
		}
	}

	private class ItemValue {
		private final Item item;
		private final Integer value;

		public ItemValue(Item item, Integer value) {
			this.item = item;
			this.value = value;
		}

		/**
		 * @return the item
		 */
		public Item getItem() {
			return item;
		}

		/**
		 * @return the value
		 */
		public Integer getValue() {
			return value;
		}
	}

	private class BlockValue {
		private final Block block;
		private final Integer value;

		public BlockValue(Block block, Integer value) {
			this.block = block;
			this.value = value;
		}

		/**
		 * @return the block
		 */
		public Block getBlock() {
			return block;
		}

		/**
		 * @return the value
		 */
		public Integer getValue() {
			return value;
		}
	}
}
