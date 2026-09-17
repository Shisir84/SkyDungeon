package in.volexmc.skydungeons.warden;

import in.volexmc.skydungeons.SkyDungeons;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.entity.Warden;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class WardenBossAbilityListener implements Listener {

    private final SkyDungeons plugin;

    private final Map<UUID, Long> sonicCooldown =
            new HashMap<>();

    private final Map<UUID, Long> darknessCooldown =
            new HashMap<>();

    private final Map<UUID, Long> slamCooldown =
            new HashMap<>();

    private static final long SONIC_COOLDOWN =
            8_000L;

    private static final long DARKNESS_COOLDOWN =
            15_000L;

    private static final long SLAM_COOLDOWN =
            10_000L;

    private static final double SONIC_RANGE =
            20.0;

    private static final double SLAM_RANGE =
            8.0;

    public WardenBossAbilityListener(
            SkyDungeons plugin
    ) {

        this.plugin = plugin;

        startAbilityTask();
    }

    private void startAbilityTask() {

        plugin.getServer()
                .getScheduler()
                .runTaskTimer(
                        plugin,
                        () -> {

                            WardenBoss boss =
                                    plugin.getWardenBoss();

                            if (boss == null) {
                                return;
                            }

                            if (!boss.isAlive()) {
                                return;
                            }

                            Warden warden =
                                    boss.getBoss();

                            if (warden == null
                                    || warden.isDead()) {
                                return;
                            }

                            /*
                             * Keep the boss phase
                             * synchronized with HP.
                             */
                            boss.updatePhase();

                            runAbilities(
                                    warden
                            );

                        },
                        40L,
                        20L
                );
    }

    private void runAbilities(
            Warden warden
    ) {

        WardenBoss boss =
                plugin.getWardenBoss();

        if (boss == null) {
            return;
        }

        int phase =
                boss.getPhase();

        UUID bossId =
                warden.getUniqueId();

        long now =
                System.currentTimeMillis();

        /*
         * =================================
         * PHASE 1
         * =================================
         *
         * 100% - above 70% HP
         */
        if (phase == 1) {

            if (canUse(
                    slamCooldown,
                    bossId,
                    now,
                    SLAM_COOLDOWN
            )) {

                performGroundSlam(
                        warden,
                        12.0,
                        1.2
                );

                slamCooldown.put(
                        bossId,
                        now
                );

                return;
            }

            if (canUse(
                    sonicCooldown,
                    bossId,
                    now,
                    SONIC_COOLDOWN
            )) {

                performSonicShockwave(
                        warden,
                        18.0,
                        1.5
                );

                sonicCooldown.put(
                        bossId,
                        now
                );

                return;
            }

            if (canUse(
                    darknessCooldown,
                    bossId,
                    now,
                    DARKNESS_COOLDOWN
            )) {

                performDarknessPulse(
                        warden,
                        25.0,
                        160
                );

                darknessCooldown.put(
                        bossId,
                        now
                );
            }

            return;
        }

        /*
         * =================================
         * PHASE 2
         * =================================
         *
         * Below 70% - above 40% HP
         *
         * Faster cooldowns.
         * Stronger damage.
         */
        if (phase == 2) {

            if (canUse(
                    slamCooldown,
                    bossId,
                    now,
                    7_000L
            )) {

                performGroundSlam(
                        warden,
                        18.0,
                        1.5
                );

                slamCooldown.put(
                        bossId,
                        now
                );

                return;
            }

            if (canUse(
                    sonicCooldown,
                    bossId,
                    now,
                    6_000L
            )) {

                performSonicShockwave(
                        warden,
                        24.0,
                        1.8
                );

                sonicCooldown.put(
                        bossId,
                        now
                );

                return;
            }

            if (canUse(
                    darknessCooldown,
                    bossId,
                    now,
                    11_000L
            )) {

                performDarknessPulse(
                        warden,
                        30.0,
                        200
                );

                darknessCooldown.put(
                        bossId,
                        now
                );
            }

            return;
        }

        /*
         * =================================
         * PHASE 3
         * =================================
         *
         * Below 40% HP.
         *
         * Enraged phase.
         * Very fast cooldowns.
         * Heavy damage.
         */
        if (phase == 3) {

            if (canUse(
                    slamCooldown,
                    bossId,
                    now,
                    5_000L
            )) {

                performGroundSlam(
                        warden,
                        25.0,
                        2.0
                );

                slamCooldown.put(
                        bossId,
                        now
                );

                return;
            }

            if (canUse(
                    sonicCooldown,
                    bossId,
                    now,
                    4_000L
            )) {

                performSonicShockwave(
                        warden,
                        32.0,
                        2.2
                );

                sonicCooldown.put(
                        bossId,
                        now
                );

                return;
            }

            if (canUse(
                    darknessCooldown,
                    bossId,
                    now,
                    8_000L
            )) {

                performDarknessPulse(
                        warden,
                        35.0,
                        260
                );

                darknessCooldown.put(
                        bossId,
                        now
                );
            }
        }
    }

    private boolean canUse(
            Map<UUID, Long> cooldownMap,
            UUID bossId,
            long now,
            long cooldown
    ) {

        Long lastUse =
                cooldownMap.get(bossId);

        if (lastUse == null) {
            return true;
        }

        return now - lastUse >= cooldown;
    }

    private void performGroundSlam(
            Warden warden,
            double damage,
            double knockbackStrength
    ) {

        Location center =
                warden.getLocation();

        World world =
                center.getWorld();

        if (world == null) {
            return;
        }

        world.playSound(
                center,
                Sound.ENTITY_WARDEN_SONIC_BOOM,
                2.5f,
                0.5f
        );

        world.spawnParticle(
                Particle.EXPLOSION,
                center.clone().add(
                        0,
                        0.2,
                        0
                ),
                10,
                1.5,
                0.2,
                1.5,
                0
        );

        world.spawnParticle(
                Particle.SCULK_SOUL,
                center.clone().add(
                        0,
                        0.5,
                        0
                ),
                120,
                4.0,
                0.5,
                4.0,
                0.08
        );

        for (Player player :
                world.getPlayers()) {

            if (player.isDead()) {
                continue;
            }

            if (player.getLocation()
                    .distanceSquared(center)
                    > SLAM_RANGE * SLAM_RANGE) {
                continue;
            }

            player.damage(
                    damage,
                    warden
            );

            Vector knockback =
                    player.getLocation()
                            .toVector()
                            .subtract(
                                    center.toVector()
                            );

            if (knockback.lengthSquared()
                    < 0.01) {

                knockback =
                        new Vector(
                                0,
                                knockbackStrength,
                                0
                        );

            } else {

                knockback.normalize();

                knockback.multiply(
                        knockbackStrength
                );

                knockback.setY(
                        0.7
                );
            }

            player.setVelocity(
                    knockback
            );

            player.sendActionBar(
                    ChatColor.RED
                            + "✦ "
                            + ChatColor.DARK_RED
                            + "WARDEN GROUND SLAM"
            );
        }
    }

    private void performSonicShockwave(
            Warden warden,
            double damage,
            double knockbackStrength
    ) {

        Location center =
                warden.getLocation();

        World world =
                center.getWorld();

        if (world == null) {
            return;
        }

        Player target =
                findNearestPlayer(
                        warden,
                        SONIC_RANGE
                );

        if (target == null) {
            return;
        }

        Location targetLocation =
                target.getLocation();

        world.playSound(
                center,
                Sound.ENTITY_WARDEN_SONIC_BOOM,
                3.0f,
                0.8f
        );

        world.spawnParticle(
                Particle.SONIC_BOOM,
                targetLocation.clone().add(
                        0,
                        1,
                        0
                ),
                1,
                0,
                0,
                0,
                0
        );

        target.damage(
                damage,
                warden
        );

        Vector direction =
                targetLocation.toVector()
                        .subtract(
                                center.toVector()
                        );

        if (direction.lengthSquared()
                > 0.01) {

            direction.normalize();

            direction.multiply(
                    knockbackStrength
            );

            direction.setY(
                    0.45
            );

            target.setVelocity(
                    direction
            );
        }

        target.sendActionBar(
                ChatColor.DARK_PURPLE
                        + "✦ "
                        + ChatColor.LIGHT_PURPLE
                        + "ANCIENT SONIC SHOCKWAVE"
        );
    }

    private void performDarknessPulse(
            Warden warden,
            double range,
            int duration
    ) {

        Location center =
                warden.getLocation();

        World world =
                center.getWorld();

        if (world == null) {
            return;
        }

        world.playSound(
                center,
                Sound.ENTITY_WARDEN_HEARTBEAT,
                3.0f,
                0.5f
        );

        world.spawnParticle(
                Particle.SCULK_SOUL,
                center.clone().add(
                        0,
                        1,
                        0
                ),
                150,
                5.0,
                2.0,
                5.0,
                0.04
        );

        for (Player player :
                world.getPlayers()) {

            if (player.isDead()) {
                continue;
            }

            if (player.getLocation()
                    .distanceSquared(center)
                    > range * range) {
                continue;
            }

            player.addPotionEffect(
                    new PotionEffect(
                            PotionEffectType.DARKNESS,
                            duration,
                            0,
                            false,
                            false,
                            true
                    )
            );

            player.sendActionBar(
                    ChatColor.DARK_PURPLE
                            + "✦ "
                            + ChatColor.GRAY
                            + "The darkness consumes you..."
            );
        }
    }

    private Player findNearestPlayer(
            Warden warden,
            double range
    ) {

        Location center =
                warden.getLocation();

        World world =
                center.getWorld();

        if (world == null) {
            return null;
        }

        Player nearest =
                null;

        double nearestDistance =
                range * range;

        for (Player player :
                world.getPlayers()) {

            if (player.isDead()) {
                continue;
            }

            double distance =
                    player.getLocation()
                            .distanceSquared(
                                    center
                            );

            if (distance >= nearestDistance) {
                continue;
            }

            nearestDistance =
                    distance;

            nearest =
                    player;
        }

        return nearest;
    }

    @EventHandler
    public void onBossTarget(
            EntityTargetEvent event
    ) {

        if (!(event.getEntity()
                instanceof Warden warden)) {
            return;
        }

        WardenBoss boss =
                plugin.getWardenBoss();

        if (boss == null
                || !boss.isBoss(warden)) {
            return;
        }

        /*
         * Allow the boss to target players.
         */
        if (event.getTarget()
                instanceof Player) {

            return;
        }

        /*
         * Prevent the custom boss from
         * targeting random entities.
         */
        event.setCancelled(true);
    }

    public void clearCooldowns() {

        sonicCooldown.clear();

        darknessCooldown.clear();

        slamCooldown.clear();
    }
}