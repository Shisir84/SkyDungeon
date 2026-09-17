package in.volexmc.skydungeons.warden;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

public class WardenPearl {

    private final JavaPlugin plugin;

    private final NamespacedKey wardenPearlKey;

    public WardenPearl(
            JavaPlugin plugin
    ) {

        this.plugin = plugin;

        this.wardenPearlKey =
                new NamespacedKey(
                        plugin,
                        "warden_pearl"
                );
    }

    // =========================================================
    // CREATE WARDEN PEARL
    // =========================================================

    public ItemStack createItem() {

        ItemStack item =
                new ItemStack(
                        Material.ENDER_PEARL
                );

        ItemMeta meta =
                item.getItemMeta();

        if (meta == null) {
            return item;
        }

        meta.setDisplayName(
                ChatColor.DARK_PURPLE
                        + "Warden Pearl"
        );

        meta.setLore(
                java.util.List.of(
                        ChatColor.GRAY
                                + "A mysterious pearl infused",
                        ChatColor.GRAY
                                + "with ancient city energy.",
                        "",
                        ChatColor.DARK_PURPLE
                                + "✦ "
                                + ChatColor.LIGHT_PURPLE
                                + "Warden Dungeon Key"
                )
        );

        /*
         * Persistent data makes this a REAL
         * SkyDungeons Warden Pearl.
         *
         * A normal Minecraft Ender Pearl
         * will NOT count as a Warden Pearl.
         */
        meta.getPersistentDataContainer()
                .set(
                        wardenPearlKey,
                        PersistentDataType.BYTE,
                        (byte) 1
                );

        item.setItemMeta(meta);

        return item;
    }

    // =========================================================
    // CHECK WARDEN PEARL
    // =========================================================

    public boolean isWardenPearl(
            ItemStack item
    ) {

        if (item == null) {
            return false;
        }

        if (item.getType()
                != Material.ENDER_PEARL) {

            return false;
        }

        ItemMeta meta =
                item.getItemMeta();

        if (meta == null) {
            return false;
        }

        Byte value =
                meta.getPersistentDataContainer()
                        .get(
                                wardenPearlKey,
                                PersistentDataType.BYTE
                        );

        return value != null
                && value == (byte) 1;
    }

    // =========================================================
    // REGISTER RECIPE
    // =========================================================

    public void registerRecipe() {

        NamespacedKey recipeKey =
                new NamespacedKey(
                        plugin,
                        "warden_pearl_recipe"
                );

        /*
         * Remove old recipe first in case
         * the plugin is reloaded/restarted.
         */
        plugin.getServer()
                .removeRecipe(recipeKey);

        ShapedRecipe recipe =
                new ShapedRecipe(
                        recipeKey,
                        createItem()
                );

        /*
         * Warden Pearl recipe:
         *
         * E E E
         * E P E
         * E E E
         *
         * E = Echo Shard
         * P = Ender Pearl
         *
         * Total:
         * 8 Echo Shards
         * 1 Ender Pearl
         */

        recipe.shape(
                "EEE",
                "EPE",
                "EEE"
        );

        recipe.setIngredient(
                'E',
                Material.ECHO_SHARD
        );

        recipe.setIngredient(
                'P',
                Material.ENDER_PEARL
        );

        plugin.getServer()
                .addRecipe(recipe);

        plugin.getLogger().info(
                "Warden Pearl recipe registered."
        );
    }

    // =========================================================
    // GET KEY
    // =========================================================

    public NamespacedKey getWardenPearlKey() {

        return wardenPearlKey;
    }
}