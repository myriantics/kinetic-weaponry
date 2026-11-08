package net.myriantics.kinetic_weaponry.recipe.generic_cloning;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.*;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.myriantics.kinetic_weaponry.registry.misc.KWRecipeSerializers;

import java.util.List;

public class GenericCloningRecipe extends CustomRecipe {

    private final Ingredient cloningTarget;
    private final List<Ingredient> cloningIngredients;
    private final HolderSet<DataComponentType<?>> carriedComponents;

    public GenericCloningRecipe(Ingredient cloningTarget, List<Ingredient> cloningIngredients, HolderSet<DataComponentType<?>> carriedComponents, CraftingBookCategory category) {
        super(category);
        this.cloningTarget = cloningTarget;
        if (cloningIngredients.size() > 8) {
            throw new IllegalArgumentException("No more than 8 cloning ingredients are allowed");
        }
        this.cloningIngredients = cloningIngredients;
        this.carriedComponents = carriedComponents;
    }

    @Override
    public boolean matches(CraftingInput input, Level level) {
        int cloningTargetIndex = -1;
        for (int i = 0; i < input.size(); i++) {
            ItemStack potentialTarget = input.getItem(i);
            if (this.cloningTarget.test(potentialTarget)) {
                cloningTargetIndex = i;
                break;
            }
        }
        if (cloningTargetIndex == -1) {
            return false;
        }
        byte satisfiedCloningRequirements = 0;
        for (int i = 0; i < input.size(); i++) {
            if (i == cloningTargetIndex) {
                continue;
            }
            ItemStack selected = input.getItem(i);

            for (byte j = 0; j < this.cloningIngredients.size(); j++) {
                if ((satisfiedCloningRequirements & (1 << j)) > 0) {
                    continue;
                }

                if (this.cloningIngredients.get(j).test(selected)) {
                    satisfiedCloningRequirements |= (1 << j);
                }
            }
        }
        for (int i = 0; i < this.cloningIngredients.size(); i++) {
            if ((satisfiedCloningRequirements & (1 << i)) == 0) {
                return false;
            }
        }

        return true;
    }

    @SuppressWarnings("unchecked")
    @Override
    public ItemStack assemble(CraftingInput input, HolderLookup.Provider registries) {
        for (ItemStack stack : input.items()) {
            if (this.cloningTarget.test(stack)) {
                ItemStack clonedStack = new ItemStack(stack.getItem());
                for (Holder<DataComponentType<?>> type : this.carriedComponents) {
                    Object o = stack.get(type.value());
                    if (o != null) {
                        clonedStack.set((DataComponentType<? super Object>) type.value(), o);
                    }
                }
                return clonedStack;
            }
        }
        return ItemStack.EMPTY;
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(CraftingInput input) {
        int clonedIndex = -1;
        ItemStack cloningRemainder = null;
        for (int i = 0; i < input.size(); i++) {
            if (this.cloningTarget.test(input.getItem(i))) {
                clonedIndex = i;
                cloningRemainder = input.getItem(i).copyWithCount(1);
                break;
            }
        }
        if (clonedIndex == -1 || cloningRemainder == null) {
            throw new AssertionError();
        }
        NonNullList<ItemStack> remainders = super.getRemainingItems(input);
        remainders.set(clonedIndex, cloningRemainder);
        return remainders;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height > 1 + this.cloningIngredients.size();
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return KWRecipeSerializers.GENERIC_CLONING.value();
    }

    public static final class Serializer implements RecipeSerializer<GenericCloningRecipe> {

        public static final MapCodec<GenericCloningRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Ingredient.CODEC.fieldOf("cloning_target").forGetter(i -> i.cloningTarget),
                Codec.list(Ingredient.CODEC, 1, 8).fieldOf("cloning_ingredients").forGetter(i -> i.cloningIngredients),
                RegistryCodecs.homogeneousList(Registries.DATA_COMPONENT_TYPE).fieldOf("transferred_components").forGetter(i -> i.carriedComponents),
                CraftingBookCategory.CODEC.fieldOf("category").forGetter(GenericCloningRecipe::category)
        ).apply(instance, GenericCloningRecipe::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, GenericCloningRecipe> STREAM_CODEC = StreamCodec.composite(
                Ingredient.CONTENTS_STREAM_CODEC, i -> i.cloningTarget,
                Ingredient.CONTENTS_STREAM_CODEC.apply(ByteBufCodecs.list(8)), i -> i.cloningIngredients,
                ByteBufCodecs.holderSet(Registries.DATA_COMPONENT_TYPE), i -> i.carriedComponents,
                CraftingBookCategory.STREAM_CODEC, GenericCloningRecipe::category,
                GenericCloningRecipe::new
        );

        @Override
        public MapCodec<GenericCloningRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, GenericCloningRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
