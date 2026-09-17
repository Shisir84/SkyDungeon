package in.volexmc.skydungeons.warden;

import org.bukkit.ChatColor;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class WardenSwordListener implements Listener {

    private final JavaPlugin plugin;
    private final WardenSword wardenSword;

    private final Map<UUID, Long> cooldowns =
            new HashMap<>();

    private static final long COOLDOWN =
            10_000L;

    private static final double RANGE =
            8.0;

    private static final double DAMAGE =
            10.0;

    public WardenSwordListener(
            JavaPlugin plugin,
            WardenSword wardenSword
    ) {

        this.plugin = plugin;
        this.wardenSword = wardenSword;
    }

    @EventHandler
    public void onPlayerInteract(
            PlayerInteractEvent event
    ) {

        if (event.getHand()
                != EquipmentSlot.HAND) {
            return;
        }

        switch (event.getAction()) {

            case RIGHT_CLICK_AIR:
            case RIGHT_CLICK_BLOCK:
                break;

            default:
                return;
        }

        Player player =
                event.getPlayer();

        ItemStack item =
                player.getInventory()
                        .getItemInMainHand();

        if (!wardenSword.isWardenSword(item)) {
            return;
        }

        event.setCancelled(true);

        long now =
                System.currentTimeMillis();

        long lastUse =
                cooldowns.getOrDefault(
                        player.getUniqueId(),
                        0L
                );

        long remaining =
                COOLDOWN
                        - (now - lastUse);

        if (remaining > 0) {

            long seconds =
                    (remaining + 999) / 1000;

            player.sendMessage(
                    ChatColor.DARK_PURPLE
                            + "✦ "
                            + ChatColor.LIGHT_PURPLE
                            + "Warden Sword "
                            + ChatColor.GRAY
                            + "is on cooldown for "
                            + ChatColor.WHITE
                            + seconds
                            + "s"
            );

            return;
        }

        cooldowns.put(
                player.getUniqueId(),
                now
        );

        performSonicAttack(player);
    }

    private void performSonicAttack(
            Player player
    ) {

        player.sendMessage(
                ChatColor.DARK_PURPLE
                        + "✦ "
                        + ChatColor.LIGHT_PURPLE
                        + "Warden Sonic Strike!"
        );

        player.getWorld().playSound(
                player.getLocation(),
                Sound.ENTITY_WARDEN_SONIC_BOOM,
                1.5f,
                1.0f
        );

        player.getWorld().spawnParticle(
                Particle.SONIC_BOOM,
                player.getLocation()
                        .add(0, 1, 0),
                1,
                0,
                0,
                0,
                0
        );

        Vector direction =
                player.getLocation()
                        .getDirection()
                        .normalize();

        for (int i = 1; i <= 8; i++) {

            Vector offset =
                    direction.clone()
                            .multiply(i);

            player.getWorld()
                    .spawnParticle(
                            Particle.SONIC_BOOM,
                            player.getLocation()
                                    .add(0, 1, 0)
                                    .add(offset),
                            1,
                            0,
                            0,
                            0,
                            0
                    );
        }

        for (LivingEntity entity :
                player.getWorld()
                        .getLivingEntities()) {

            if (entity.equals(player)) {
                continue;
            }

            if (entity.isDead()) {
                continue;
            }

            if (entity.getLocation()
                    .distance(player.getLocation())
                    > RANGE) {
                continue;
            }

            Vector toEntity =
                    entity.getLocation()
                            .toVector()
                            .subtract(
                                    player.getLocation()
                                            .toVector()
                            )
                            .normalize();

            double dot =
                    direction.dot(toEntity);

            if (dot < 0.35) {
                continue;
            }

            entity.damage(
                    DAMAGE,
                    player
            );

            Vector knockback =
                    direction.clone()
                            .multiply(1.5);

            knockback.setY(0.35);

            entity.setVelocity(
                    knockback
            );
        }
    }

    public void clearCooldown(
            Player player
    ) {

        cooldowns.remove(
                player.getUniqueId()
        );
    }

    public void clearCooldowns() {

        cooldowns.clear();
    }
}