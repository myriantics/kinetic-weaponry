package net.myriantics.kinetic_weaponry.datagen.impl.recipe;

import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.myriantics.kinetic_weaponry.datagen.impl.tag.KWItemTagProvider;
import net.myriantics.kinetic_weaponry.registry.item.KWItems;
import net.myriantics.kinetic_weaponry.tag.KWItemTags;
import net.myriantics.myrror.datagen.template.recipe.MyrrorRecipeProvider;
import net.myriantics.myrror.datagen.template.recipe.providers.CraftingRecipeProvider;

public class KWCraftingRecipeProvider extends CraftingRecipeProvider {
    public KWCraftingRecipeProvider(MyrrorRecipeProvider provider, RecipeOutput output) {
        super(provider, output);
    }

    @Override
    protected void generateRecipes() {
        add2x2PackingUnpackingRecipes(KWItems.TRIAL_WEAVE, KWItems.TRIAL_TWINE, craftingBuilder -> craftingBuilder);
        addShapedCraftingRecipe(
                new String[] {
                        "CBT",
                        "B T",
                        "HBT"
                },
                new ItemStack(KWItems.KINETIC_SHORTBOW),
                builder -> builder
                        .associate('C', KWItemTags.HEAT_SINKS)
                        .associate('T', KWItems.TRIAL_TWINE)
                        .associate('H', Items.HEAVY_CORE)
                        .associate('B', Items.BREEZE_ROD)
                        .category(CraftingBookCategory.EQUIPMENT)
        );
        addKineticCrossbowRecipe(Items.CHISELED_COPPER);
        addKineticCrossbowRecipe(Items.WAXED_CHISELED_COPPER);
        addKineticDetonatorRecipe(KWItems.KINETIC_DETONATOR, Items.CHISELED_COPPER);
        addKineticDetonatorRecipe(KWItems.KINETIC_DETONATOR,  Items.WAXED_CHISELED_COPPER);
        addKineticChargingBusRecipe(KWItems.KINETIC_CHARGING_BUS, Items.CHISELED_COPPER);
        addKineticChargingBusRecipe(KWItems.KINETIC_CHARGING_BUS, Items.WAXED_CHISELED_COPPER);
        addStandardKineticRetentionModuleRecipe(KWItems.KINETIC_RETENTION_MODULE, Items.CHISELED_COPPER);
        addStandardKineticRetentionModuleRecipe(KWItems.KINETIC_RETENTION_MODULE, Items.WAXED_CHISELED_COPPER);
    }

    private void addKineticCrossbowRecipe(Item chiseledBlock) {
        addShapedCraftingRecipe(
                new String[] {
                        "BHB",
                        "TCT",
                        " c "
                },
                new ItemStack(KWItems.KINETIC_CROSSBOW),
                builder -> builder
                        .associate('B', Items.BREEZE_ROD)
                        .associate('T', KWItems.TRIAL_TWINE)
                        .associate('H', Items.HEAVY_CORE)
                        .associate('C', chiseledBlock)
                        .associate('c', Items.COPPER_INGOT)
        );
    }

    private void addStandardKineticRetentionModuleRecipe(Item result, Item chiseledBlock) {
        addShapedCraftingRecipe(
                new String[]{
                        " H ",
                        "BWB",
                        " C "
                },
                new ItemStack(result),
                builder -> builder
                        .associate('H', Items.HEAVY_CORE)
                        .associate('W', KWItems.TRIAL_WEAVE)
                        .associate('B', Items.BREEZE_ROD)
                        .associate('C', chiseledBlock)
        );
    }

    private void addKineticChargingBusRecipe(Item result, Item chiseledBlock) {
        addShapedCraftingRecipe(
                new String[]{
                        "IHI",
                        "RWR",
                        "ICI"
                },
                new ItemStack(result),
                builder -> builder
                        .associate('I', Items.COPPER_INGOT)
                        .associate('R', Items.BREEZE_ROD)
                        .associate('W', KWItems.TRIAL_WEAVE)
                        .associate('H', Items.HEAVY_CORE)
                        .associate('C', chiseledBlock)
        );
    }

    private void addKineticDetonatorRecipe(Item result, Item chiseledBlock) {
        addShapedCraftingRecipe(
                new String[]{
                        "IHI",
                        "RMR",
                        "ICI"
                },
                new ItemStack(result),
                builder -> builder
                        .associate('I', Items.COPPER_INGOT)
                        .associate('R', Items.BREEZE_ROD)
                        .associate('H', Items.HEAVY_CORE)
                        .associate('M', Items.TNT_MINECART)
                        .associate('C', chiseledBlock)
        );
    }
}
