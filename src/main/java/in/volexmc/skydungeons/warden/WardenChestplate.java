package in.volexmc.skydungeons.warden;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class WardenChestplate {

    private final NamespacedKey chestplateKey;

    public WardenChestplate(JavaPlugin plugin) {

        this.chestplateKey =
                new NamespacedKey(
                        plugin,
                        "warden_chestplate"
                );
    }

    public ItemStack createItem() {

        ItemStack item =
                new ItemStack(
                        Material.NETHERITE_CHESTPLATE
                );

        ItemMeta meta =
                item.getItemMeta();

        if (meta == null) {
            return item;
        }

        meta.setDisplayName(
                ChatColor.DARK_PURPLE
                        + "Warden Chestplate"
        );

        meta.setLore(
                List.of(
                        ChatColor.GRAY
                                + "Forged from ancient",
                        ChatColor.GRAY
                                + "Warden energy.",
                        "",
                        ChatColor.DARK_PURPLE
                                + "✦ "
                                + ChatColor.LIGHT_PURPLE
                                + "Warden Armor"
                )
        );

        meta.getPersistentDataContainer()
                .set(
                        chestplateKey,
                        PersistentDataType.BYTE,
                        (byte) 1
                );

        AttributeModifier armorModifier =
                new AttributeModifier(
                        new NamespacedKey(
                                chestplateKey.getNamespace(),
                                "warden_chestplate_armor"
                        ),
                        8.0,
                        AttributeModifier.Operation.ADD_NUMBER
                );

        meta.addAttributeModifier(
                Attribute.ARMOR,
                armorModifier
        );

        item.setItemMeta(meta);

        return item;
    }

    public boolean isWardenChestplate(
            ItemStack item
    ) {

        if (item == null) {
            return false;
        }

        if (item.getType()
                != Material.NETHERITE_CHESTPLATE) {
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
                                chestplateKey,
                                PersistentDataType.BYTE
                        );

        return value != null
                && value == (byte) 1;
    }

    public NamespacedKey getChestplateKey() {

        return chestplateKey;
    }
}