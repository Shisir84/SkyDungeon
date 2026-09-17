package in.volexmc.skydungeons.warden;

import in.volexmc.skydungeons.SkyDungeons;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Warden;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public class WardenBossListener implements Listener {

    private final SkyDungeons plugin;

    public WardenBossListener(
            SkyDungeons plugin
    ) {
        this.plugin = plugin;
    }

    /*
     * ==========================================
     * BOSS DAMAGE
     * ==========================================
     */

    @EventHandler
    public void onBossDamage(
            EntityDamageEvent event
    ) {

        Entity entity =
                event.getEntity();

        if (!(entity instanceof Warden warden)) {
            return;
        }

        WardenBoss boss =
                plugin.getWardenBoss();

        if (boss == null) {
            return;
        }

        /*
         * Make sure this is our
         * custom Ancient Warden.
         */
        if (!boss.isBoss(warden)) {
            return;
        }

        /*
         * Prevent negative damage.
         */
        if (event.getDamage() < 0) {

            event.setDamage(0);
        }

        /*
         * Update boss phase.
         */
        boss.updatePhase();

        /*
         * Damage visual effect.
         */
        World world =
                warden.getWorld();

        if (world != null) {

            Location location =
                    warden.getLocation();

            world.spawnParticle(
                    Particle.SCULK_SOUL,
                    location.clone().add(
                            0,
                            1,
                            0
                    ),
                    8,
                    0.5,
                    0.5,
                    0.5,
                    0.03
            );
        }
    }

    /*
     * ==========================================
     * BOSS DEATH
     * ==========================================
     */

    @EventHandler
    public void onBossDeath(
            EntityDeathEvent event
    ) {

        Entity entity =
                event.getEntity();

        if (!(entity instanceof Warden warden)) {
            return;
        }

        WardenBoss boss =
                plugin.getWardenBoss();

        if (boss == null) {
            return;
        }

        /*
         * Make sure this is our
         * custom Ancient Warden.
         */
        if (!boss.isBoss(warden)) {
            return;
        }

        Location location =
                warden.getLocation();

        World world =
                location.getWorld();

        /*
         * ======================================
         * REMOVE VANILLA REWARDS
         * ======================================
         */

        event.getDrops().clear();

        event.setDroppedExp(0);

        /*
         * ======================================
         * FIND KILLER
         * ======================================
         */

        Player killer =
                warden.getKiller();

        /*
         * ======================================
         * GIVE WARDEN REWARD
         * ======================================
         */

        if (killer != null) {

            giveWardenIngots(
                    killer,
                    32
            );

        } else if (world != null) {

            /*
             * Fallback if Minecraft cannot
             * identify the killer.
             *
             * Players currently inside the
             * Warden Boss Realm receive
             * the reward.
             */

            for (Player player :
                    world.getPlayers()) {

                giveWardenIngots(
                        player,
                        32
                );
            }
        }

        /*
         * ======================================
         * DEATH EFFECTS
         * ======================================
         */

        if (world != null) {

            world.spawnParticle(
                    Particle.EXPLOSION,
                    location.clone().add(
                            0,
                            1,
                            0
                    ),
                    10,
                    1.0,
                    1.0,
                    1.0,
                    0
            );

            world.spawnParticle(
                    Particle.SCULK_SOUL,
                    location.clone().add(
                            0,
                            1,
                            0
                    ),
                    150,
                    2.5,
                    3.0,
                    2.5,
                    0.08
            );

            world.playSound(
                    location,
                    Sound.ENTITY_WARDEN_DEATH,
                    3.0f,
                    0.6f
            );
        }

        /*
         * ======================================
         * VICTORY ANNOUNCEMENT
         * ======================================
         */

        if (world != null) {

            for (Player player :
                    world.getPlayers()) {

                player.sendMessage("");

                player.sendMessage(
                        ChatColor.DARK_PURPLE
                                + "✦ "
                                + ChatColor.LIGHT_PURPLE
                                + "ANCIENT WARDEN DEFEATED"
                );

                player.sendMessage(
                        ChatColor.GRAY
                                + "The ancient darkness has fallen."
                );

                player.sendMessage(
                        ChatColor.GREEN
                                + "+32 Warden Ingots"
                );

                player.sendMessage("");
            }
        }

        /*
         * ======================================
         * CLEAR BOSS
         * ======================================
         */

        boss.removeBoss();

        /*
         * ======================================
         * CLEAR ABILITY COOLDOWNS
         * ======================================
         */

        WardenBossAbilityListener abilityListener =
                plugin.getWardenBossAbilityListener();

        if (abilityListener != null) {

            abilityListener.clearCooldowns();
        }

        /*
         * ======================================
         * LOG
         * ======================================
         */

        plugin.getLogger().info(
                "================================="
        );

        plugin.getLogger().info(
                "Ancient Warden Boss defeated."
        );

        if (killer != null) {

            plugin.getLogger().info(
                    "Rewarded 32 Warden Ingots to "
                            + killer.getName()
            );

        } else {

            plugin.getLogger().info(
                    "No direct killer found. "
                            + "Fallback reward given to "
                            + "players inside the Warden Realm."
            );
        }

        plugin.getLogger().info(
                "================================="
        );
    }

    /*
     * ==========================================
     * GIVE WARDEN INGOTS
     * ==========================================
     */

    private void giveWardenIngots(
            Player player,
            int amount
    ) {

        if (player == null) {
            return;
        }

        WardenIngot wardenIngot =
                plugin.getWardenIngot();

        if (wardenIngot == null) {

            plugin.getLogger().severe(
                    "WardenIngot system is not available!"
            );

            return;
        }

        int remaining =
                amount;

        /*
         * Give the reward safely.
         *
         * Maximum stack size is 64,
         * so 32 will fit into one stack.
         */

        while (remaining > 0) {

            int stackAmount =
                    Math.min(
                            remaining,
                            64
                    );

            ItemStack reward =
                    wardenIngot.createStack(
                            stackAmount
                    );

            if (reward == null) {

                plugin.getLogger().severe(
                        "Could not create Warden Ingot reward."
                );

                return;
            }

            Map<Integer, ItemStack> leftover =
                    player.getInventory()
                            .addItem(
                                    reward
                            );

            /*
             * If inventory is full,
             * drop the leftover items
             * at the player's location.
             */

            if (!leftover.isEmpty()) {

                for (ItemStack item :
                        leftover.values()) {

                    player.getWorld()
                            .dropItemNaturally(
                                    player.getLocation(),
                                    item
                            );
                }
            }

            remaining -= stackAmount;
        }

        /*
         * ======================================
         * PLAYER REWARD MESSAGE
         * ======================================
         */

        player.sendMessage("");

        player.sendMessage(
                ChatColor.DARK_PURPLE
                        + "✦ "
                        + ChatColor.LIGHT_PURPLE
                        + "WARDEN REWARD"
        );

        player.sendMessage(
                ChatColor.GRAY
                        + "You received "
                        + ChatColor.DARK_PURPLE
                        + "32 Warden Ingots"
        );

        player.sendMessage("");

        /*
         * Reward sound.
         */

        player.playSound(
                player.getLocation(),
                Sound.ENTITY_PLAYER_LEVELUP,
                1.0f,
                1.2f
        );
    }
}