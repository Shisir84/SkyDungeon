package in.volexmc.skydungeons.warden;

import in.volexmc.skydungeons.SkyDungeons;
import org.bukkit.NamespacedKey;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;

public class WardenSwordRecipe {

    private final SkyDungeons plugin;
    private final WardenIngot wardenIngot;

    public WardenSwordRecipe(SkyDungeons plugin) {

        this.plugin = plugin;

        this.wardenIngot =
                plugin.getWardenIngot();
    }

    public void registerRecipe() {

        ItemStack result =
                plugin.getWardenSword()
                        .createItem();

        ShapedRecipe recipe =
                new ShapedRecipe(
                        new NamespacedKey(
                                plugin,
                                "warden_sword_recipe"
                        ),
                        result
                );

        recipe.shape(
                " I ",
                " I ",
                " S "
        );

        recipe.setIngredient(
                'I',
                wardenIngot.createItem()
        );

        recipe.setIngredient(
                'S',
                Material.STICK
        );

        plugin.getServer()
                .addRecipe(recipe);
    }
}