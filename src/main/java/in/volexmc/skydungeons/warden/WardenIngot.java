package in.volexmc.skydungeons.warden;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class WardenIngot {

    private final JavaPlugin plugin;

    private final NamespacedKey wardenIngotKey;

    public WardenIngot(JavaPlugin plugin) {

        this.plugin = plugin;

        this.wardenIngotKey =
                new NamespacedKey(
                        plugin,
                        "warden_ingot"
                );
    }

    /**
     * Creates one Warden Ingot.
     */
    public ItemStack createItem() {

        ItemStack item =
                new ItemStack(
                        Material.NETHERITE_INGOT
                );

        ItemMeta meta =
                item.getItemMeta();

        if (meta == null) {
            return item;
        }

        meta.setDisplayName(
                ChatColor.DARK_PURPLE
                        + "Warden Ingot"
        );

        meta.setLore(
                List.of(
                        ChatColor.GRAY
                                + "A powerful ingot forged",
                        ChatColor.GRAY
                                + "from ancient Warden energy.",
                        "",
                        ChatColor.DARK_PURPLE
                                + "✦ "
                                + ChatColor.LIGHT_PURPLE
                                + "Warden Material"
                )
        );

        meta.getPersistentDataContainer()
                .set(
                        wardenIngotKey,
                        PersistentDataType.BYTE,
                        (byte) 1
                );

        item.setItemMeta(meta);

        return item;
    }

    /**
     * Checks whether an item is
     * a real SkyDungeons Warden Ingot.
     */
    public boolean isWardenIngot(
            ItemStack item
    ) {

        if (item == null) {
            return false;
        }

        if (item.getType()
                != Material.NETHERITE_INGOT) {
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
                                wardenIngotKey,
                                PersistentDataType.BYTE
                        );

        return value != null
                && value == (byte) 1;
    }

    /**
     * Creates a stack containing
     * the requested amount of Warden Ingots.
     */
    public ItemStack createStack(
            int amount
    ) {

        if (amount <= 0) {
            return null;
        }

        ItemStack item =
                createItem();

        item.setAmount(
                Math.min(
                        amount,
                        item.getMaxStackSize()
                )
        );

        return item;
    }

    public NamespacedKey getWardenIngotKey() {

        return wardenIngotKey;
    }
}