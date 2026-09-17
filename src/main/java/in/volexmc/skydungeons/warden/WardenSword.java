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

public class WardenSword {

    private final NamespacedKey swordKey;

    public WardenSword(JavaPlugin plugin) {

        this.swordKey =
                new NamespacedKey(
                        plugin,
                        "warden_sword"
                );
    }

    public ItemStack createItem() {

        ItemStack item =
                new ItemStack(
                        Material.NETHERITE_SWORD
                );

        ItemMeta meta =
                item.getItemMeta();

        if (meta == null) {
            return item;
        }

        meta.setDisplayName(
                ChatColor.DARK_PURPLE
                        + "Warden Sword"
        );

        meta.setLore(
                List.of(
                        ChatColor.GRAY
                                + "A weapon forged from",
                        ChatColor.GRAY
                                + "ancient Warden energy.",
                        "",
                        ChatColor.DARK_PURPLE
                                + "✦ "
                                + ChatColor.LIGHT_PURPLE
                                + "Warden Weapon"
                )
        );

        meta.getPersistentDataContainer()
                .set(
                        swordKey,
                        PersistentDataType.BYTE,
                        (byte) 1
                );

        AttributeModifier damageModifier =
                new AttributeModifier(
                        new NamespacedKey(
                                swordKey.getNamespace(),
                                "warden_sword_damage"
                        ),
                        5.0,
                        AttributeModifier.Operation.ADD_NUMBER
                );

        meta.addAttributeModifier(
                Attribute.ATTACK_DAMAGE,
                damageModifier
        );

        item.setItemMeta(meta);

        return item;
    }

    public boolean isWardenSword(
            ItemStack item
    ) {

        if (item == null) {
            return false;
        }

        if (item.getType()
                != Material.NETHERITE_SWORD) {
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
                                swordKey,
                                PersistentDataType.BYTE
                        );

        return value != null
                && value == (byte) 1;
    }

    public NamespacedKey getSwordKey() {

        return swordKey;
    }
}