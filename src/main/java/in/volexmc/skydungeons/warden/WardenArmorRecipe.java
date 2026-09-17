package in.volexmc.skydungeons.warden;

import in.volexmc.skydungeons.SkyDungeons;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;

public class WardenArmorRecipe {

    private final SkyDungeons plugin;
    private final WardenIngot wardenIngot;

    public WardenArmorRecipe(
            SkyDungeons plugin
    ) {

        this.plugin = plugin;
        this.wardenIngot =
                plugin.getWardenIngot();
    }

    public void registerRecipes() {

        registerHelmet();
        registerChestplate();
        registerLeggings();
        registerBoots();
    }

    private void registerHelmet() {

        ItemStack result =
                plugin.getWardenHelmet()
                        .createItem();

        ShapedRecipe recipe =
                new ShapedRecipe(
                        new NamespacedKey(
                                plugin,
                                "warden_helmet_recipe"
                        ),
                        result
                );

        recipe.shape(
                "III",
                "I I",
                "   "
        );

        recipe.setIngredient(
                'I',
                wardenIngot.createItem()
        );

        plugin.getServer()
                .addRecipe(recipe);
    }

    private void registerChestplate() {

        ItemStack result =
                plugin.getWardenChestplate()
                        .createItem();

        ShapedRecipe recipe =
                new ShapedRecipe(
                        new NamespacedKey(
                                plugin,
                                "warden_chestplate_recipe"
                        ),
                        result
                );

        recipe.shape(
                "I I",
                "III",
                "III"
        );

        recipe.setIngredient(
                'I',
                wardenIngot.createItem()
        );

        plugin.getServer()
                .addRecipe(recipe);
    }

    private void registerLeggings() {

        ItemStack result =
                plugin.getWardenLeggings()
                        .createItem();

        ShapedRecipe recipe =
                new ShapedRecipe(
                        new NamespacedKey(
                                plugin,
                                "warden_leggings_recipe"
                        ),
                        result
                );

        recipe.shape(
                "III",
                "I I",
                "I I"
        );

        recipe.setIngredient(
                'I',
                wardenIngot.createItem()
        );

        plugin.getServer()
                .addRecipe(recipe);
    }

    private void registerBoots() {

        ItemStack result =
                plugin.getWardenBoots()
                        .createItem();

        ShapedRecipe recipe =
                new ShapedRecipe(
                        new NamespacedKey(
                                plugin,
                                "warden_boots_recipe"
                        ),
                        result
                );

        recipe.shape(
                "   ",
                "I I",
                "I I"
        );

        recipe.setIngredient(
                'I',
                wardenIngot.createItem()
        );

        plugin.getServer()
                .addRecipe(recipe);
    }
}