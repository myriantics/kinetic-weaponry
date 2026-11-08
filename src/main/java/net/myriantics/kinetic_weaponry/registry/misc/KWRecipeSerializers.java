package net.myriantics.kinetic_weaponry.registry.misc;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.myriantics.kinetic_weaponry.KWCommon;
import net.myriantics.kinetic_weaponry.recipe.generic_cloning.GenericCloningRecipe;
import net.myriantics.kinetic_weaponry.recipe.stored_enchantment_reclamation.StoredEnchantmentReclamationRecipe;

public abstract class KWRecipeSerializers {

    public static final Holder<StoredEnchantmentReclamationRecipe.Serializer> STORED_ENCHANTMENT_RECLAMATION = register(
            "crafting/stored_enchantment_reclamation",
            new StoredEnchantmentReclamationRecipe.Serializer()
    );

    public static final Holder<GenericCloningRecipe.Serializer> GENERIC_CLONING = register(
            "crafting/generic_cloning",
            new GenericCloningRecipe.Serializer()
    );

    @SuppressWarnings("unchecked")
    private static <T extends Recipe<?>, U extends RecipeSerializer<T>> Holder<U> register(String name, U serializer) {
        return Registry.registerForHolder((Registry<U>) (Object) BuiltInRegistries.RECIPE_SERIALIZER, KWCommon.locate(name), serializer);
    }

    public static void init() {
        KWCommon.LOGGER.info("Registered Kinetic Weaponry's Recipe Serializers!");
    }
}
