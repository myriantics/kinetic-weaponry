package net.myriantics.kinetic_weaponry.recipe.stored_enchantment_reclamation;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.Level;
import net.myriantics.kinetic_weaponry.registry.misc.KWRecipeSerializers;

public class StoredEnchantmentReclamationRecipe extends CustomRecipe {

    private final Ingredient ingredient;
    private final ItemStack resultStack;

    public StoredEnchantmentReclamationRecipe(Ingredient ingredient, CraftingBookCategory category) {
        this(ingredient, new ItemStack(Items.ENCHANTED_BOOK), category);
    }

    public StoredEnchantmentReclamationRecipe(Ingredient ingredient, ItemStack resultStack, CraftingBookCategory category) {
        super(category);
        this.ingredient = ingredient;
        this.resultStack = resultStack;
    }

    @Override
    public boolean matches(CraftingInput input, Level level) {
        for (ItemStack stack : input.items()) {
            if (!stack.isEmpty() && !this.ingredient.test(stack) || stack.getOrDefault(DataComponents.STORED_ENCHANTMENTS, ItemEnchantments.EMPTY).isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public ItemStack assemble(CraftingInput input, HolderLookup.Provider registries) {
        for (ItemStack stack : input.items()) {
            if (this.ingredient.test(stack)) {
                ItemStack outputStack = this.resultStack.copy();
                if (stack.get(DataComponents.STORED_ENCHANTMENTS) instanceof ItemEnchantments enchantments) {
                    outputStack.set(DataComponents.STORED_ENCHANTMENTS, enchantments);
                    return outputStack;
                } else {
                    return ItemStack.EMPTY;
                }
            }
        }
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width > 0 && height > 0;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return KWRecipeSerializers.STORED_ENCHANTMENT_RECLAMATION.value();
    }

    public static final class Serializer implements RecipeSerializer<StoredEnchantmentReclamationRecipe> {

        public static final MapCodec<StoredEnchantmentReclamationRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Ingredient.CODEC.fieldOf("ingredient").forGetter(i -> i.ingredient),
                ItemStack.CODEC.optionalFieldOf("result", new ItemStack(Items.ENCHANTED_BOOK)).forGetter(i -> i.resultStack),
                CraftingBookCategory.CODEC.fieldOf("category").forGetter(CustomRecipe::category)
        ).apply(instance, StoredEnchantmentReclamationRecipe::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, StoredEnchantmentReclamationRecipe> STREAM_CODEC = StreamCodec.composite(
                Ingredient.CONTENTS_STREAM_CODEC, i -> i.ingredient,
                ItemStack.STREAM_CODEC, i -> i.resultStack,
                CraftingBookCategory.STREAM_CODEC, CustomRecipe::category,
                StoredEnchantmentReclamationRecipe::new
        );

        @Override
        public MapCodec<StoredEnchantmentReclamationRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, StoredEnchantmentReclamationRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
