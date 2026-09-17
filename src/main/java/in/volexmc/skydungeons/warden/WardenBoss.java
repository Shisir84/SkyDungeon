package in.volexmc.skydungeons.warden;

import in.volexmc.skydungeons.SkyDungeons;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Warden;

public class WardenBoss {

    private final SkyDungeons plugin;

    private Warden boss;

    private int currentPhase = 1;

    private static final double MAX_HEALTH = 500.0;
    private static final double ATTACK_DAMAGE = 25.0;

    public WardenBoss(
            SkyDungeons plugin
    ) {
        this.plugin = plugin;
    }

    public Warden spawn() {

        if (boss != null && !boss.isDead()) {
            return boss;
        }

        Location location =
                plugin.getWardenBossWorldManager()
                        .getBossLocation();

        if (location == null) {

            plugin.getLogger().severe(
                    "Could not spawn Warden Boss: location is null."
            );

            return null;
        }

        if (location.getWorld() == null) {

            plugin.getLogger().severe(
                    "Could not spawn Warden Boss: world is null."
            );

            return null;
        }

        boss =
                (Warden) location.getWorld()
                        .spawnEntity(
                                location,
                                EntityType.WARDEN
                        );

        boss.setPersistent(true);
        boss.setRemoveWhenFarAway(false);

        if (boss.getAttribute(
                Attribute.MAX_HEALTH
        ) != null) {

            boss.getAttribute(
                    Attribute.MAX_HEALTH
            ).setBaseValue(
                    MAX_HEALTH
            );

            boss.setHealth(
                    MAX_HEALTH
            );
        }

        if (boss.getAttribute(
                Attribute.ATTACK_DAMAGE
        ) != null) {

            boss.getAttribute(
                    Attribute.ATTACK_DAMAGE
            ).setBaseValue(
                    ATTACK_DAMAGE
            );
        }

        currentPhase = 1;

        updateBossName();

        plugin.getLogger().info(
                "Ancient Warden Boss spawned at "
                        + location.getBlockX()
                        + ", "
                        + location.getBlockY()
                        + ", "
                        + location.getBlockZ()
        );

        return boss;
    }

    public boolean isBoss(
            Warden warden
    ) {

        if (warden == null) {
            return false;
        }

        return boss != null
                && boss.getUniqueId()
                .equals(
                        warden.getUniqueId()
                );
    }

    public Warden getBoss() {

        return boss;
    }

    public boolean isAlive() {

        return boss != null
                && !boss.isDead();
    }

    public int getPhase() {

        return currentPhase;
    }

    public void updatePhase() {

        if (boss == null
                || boss.isDead()) {
            return;
        }

        double health =
                boss.getHealth();

        double percentage =
                (health / MAX_HEALTH) * 100.0;

        int newPhase;

        if (percentage > 70.0) {

            newPhase = 1;

        } else if (percentage > 40.0) {

            newPhase = 2;

        } else {

            newPhase = 3;
        }

        if (newPhase == currentPhase) {
            return;
        }

        int oldPhase =
                currentPhase;

        currentPhase =
                newPhase;

        updateBossName();

        announcePhaseChange(
                oldPhase,
                newPhase
        );
    }

    private void updateBossName() {

        if (boss == null
                || boss.isDead()) {
            return;
        }

        ChatColor phaseColor;

        if (currentPhase == 1) {

            phaseColor =
                    ChatColor.LIGHT_PURPLE;

        } else if (currentPhase == 2) {

            phaseColor =
                    ChatColor.DARK_PURPLE;

        } else {

            phaseColor =
                    ChatColor.RED;
        }

        boss.setCustomName(
                phaseColor
                        + "✦ "
                        + ChatColor.LIGHT_PURPLE
                        + "Ancient Warden "
                        + ChatColor.GRAY
                        + "[Phase "
                        + currentPhase
                        + "]"
        );

        boss.setCustomNameVisible(
                true
        );
    }

    private void announcePhaseChange(
            int oldPhase,
            int newPhase
    ) {

        if (boss == null
                || boss.getWorld() == null) {
            return;
        }

        World world =
                boss.getWorld();

        Location location =
                boss.getLocation();

        world.spawnParticle(
                Particle.SCULK_SOUL,
                location.clone().add(
                        0,
                        1,
                        0
                ),
                150,
                3.0,
                3.0,
                3.0,
                0.08
        );

        world.spawnParticle(
                Particle.EXPLOSION,
                location.clone().add(
                        0,
                        1,
                        0
                ),
                8,
                1.5,
                1.5,
                1.5,
                0
        );

        world.playSound(
                location,
                Sound.ENTITY_WARDEN_ROAR,
                3.0f,
                0.5f
        );

        for (org.bukkit.entity.Player player :
                world.getPlayers()) {

            player.sendMessage("");

            if (newPhase == 2) {

                player.sendMessage(
                        ChatColor.DARK_PURPLE
                                + "✦ "
                                + ChatColor.LIGHT_PURPLE
                                + "THE WARDEN ENTERS PHASE II"
                );

                player.sendMessage(
                        ChatColor.GRAY
                                + "The ancient darkness grows stronger..."
                );

            } else if (newPhase == 3) {

                player.sendMessage(
                        ChatColor.RED
                                + "✦ "
                                + ChatColor.DARK_RED
                                + "THE WARDEN ENTERS FINAL PHASE"
                );

                player.sendMessage(
                        ChatColor.GRAY
                                + "The Ancient Warden is enraged!"
                );
            }

            player.sendMessage("");
        }

        plugin.getLogger().info(
                "Ancient Warden phase changed from "
                        + oldPhase
                        + " to "
                        + newPhase
        );
    }

    public void removeBoss() {

        if (boss == null) {
            return;
        }

        if (!boss.isDead()) {
            boss.remove();
        }

        boss = null;
        currentPhase = 1;
    }

    public double getMaxHealth() {

        return MAX_HEALTH;
    }

    public double getAttackDamage() {

        return ATTACK_DAMAGE;
    }
}