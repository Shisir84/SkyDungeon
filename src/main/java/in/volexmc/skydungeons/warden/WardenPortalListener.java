package in.volexmc.skydungeons.warden;

import in.volexmc.skydungeons.SkyDungeons;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;

public class WardenPortalListener implements Listener {

    private final SkyDungeons plugin;
    private final WardenPearl wardenPearl;

    /*
     * =========================================================
     * WARDEN PORTAL SIZE
     * =========================================================
     *
     * OUTER FRAME:
     * 22 blocks wide
     * 8 blocks high
     *
     * INNER OPENING:
     * 20 blocks wide
     * 6 blocks high
     */

    private static final int PORTAL_WIDTH = 22;
    private static final int PORTAL_HEIGHT = 8;

    private static final int INNER_WIDTH = 20;
    private static final int INNER_HEIGHT = 6;

    /*
     * Search radius around the pearl impact location.
     */
    private static final int SEARCH_RADIUS = 32;

    public WardenPortalListener(
            SkyDungeons plugin
    ) {

        this.plugin = plugin;

        /*
         * Create the Warden Pearl checker.
         *
         * WardenPearl.isWardenPearl()
         * is a non-static method, so we need
         * an instance of WardenPearl.
         */
        this.wardenPearl =
                new WardenPearl(plugin);
    }

    // =========================================================
    // PROJECTILE HIT
    // =========================================================

    @EventHandler
    public void onProjectileHit(
            ProjectileHitEvent event
    ) {

        /*
         * Only Ender Pearls.
         */
        if (!(event.getEntity()
                instanceof EnderPearl pearl)) {

            return;
        }

        /*
         * Only player-thrown pearls.
         */
        if (!(pearl.getShooter()
                instanceof Player player)) {

            return;
        }

        /*
         * Pearl must hit a block.
         */
        if (event.getHitBlock() == null) {
            return;
        }

        /*
         * Get the item used by the projectile.
         */
        ItemStack item =
                pearl.getItem();

        /*
         * Check if this is the custom
         * SkyDungeons Warden Pearl.
         */
        if (!wardenPearl.isWardenPearl(item)) {
            return;
        }

        /*
         * Warden Pearl must hit
         * Reinforced Deepslate.
         */
        if (event.getHitBlock().getType()
                != Material.REINFORCED_DEEPSLATE) {

            player.sendMessage(
                    ChatColor.DARK_PURPLE
                            + "Warden Pearl "
                            + ChatColor.GRAY
                            + "must hit "
                            + ChatColor.DARK_GRAY
                            + "Reinforced Deepslate"
                            + ChatColor.GRAY
                            + "."
            );

            return;
        }

        Location hitLocation =
                event.getHitBlock()
                        .getLocation();

        /*
         * Get Warden Dungeon Manager.
         */
        WardenDungeonManager manager =
                plugin.getWardenDungeonManager();

        /*
         * Check if this location belongs
         * to a registered Warden Dungeon.
         */
        WardenDungeonManager.WardenDungeon dungeon =
                manager.getDungeonAt(hitLocation);

        if (dungeon == null) {

            player.sendMessage(
                    ChatColor.DARK_PURPLE
                            + "Warden Pearl "
                            + ChatColor.GRAY
                            + "cannot be activated here."
            );

            return;
        }

        /*
         * Find the existing portal.
         *
         * Expected:
         *
         * OUTER = 22 x 8
         * INNER = 20 x 6
         */
        PortalData portal =
                findExistingWardenPortal(
                        hitLocation
                );

        if (portal == null) {

            player.sendMessage(
                    ChatColor.DARK_PURPLE
                            + "Warden Portal "
                            + ChatColor.RED
                            + "could not be detected."
            );

            plugin.getLogger().warning(
                    "Warden portal structure could not be detected at "
                            + hitLocation.getBlockX()
                            + ", "
                            + hitLocation.getBlockY()
                            + ", "
                            + hitLocation.getBlockZ()
            );

            return;
        }

        /*
         * Check whether this dungeon
         * has already been activated.
         */
        if (manager.isActivated(
                dungeon.getId()
        )) {

            player.sendMessage(
                    ChatColor.DARK_PURPLE
                            + "Warden Portal "
                            + ChatColor.GRAY
                            + "is already activated."
            );

            return;
        }

        /*
         * Activate the Warden Dungeon.
         */
        boolean activated =
                manager.activateDungeon(
                        dungeon.getId()
                );

        if (!activated) {

            player.sendMessage(
                    ChatColor.RED
                            + "The Warden Portal "
                            + "could not be activated."
            );

            return;
        }

        /*
         * =====================================================
         * PORTAL ACTIVATION EFFECTS
         * =====================================================
         */

        World world =
                hitLocation.getWorld();

        if (world != null) {

            /*
             * Lightning effect.
             */
            world.strikeLightningEffect(
                    portal.center
            );

            /*
             * Sound.
             */
            world.playSound(
                    portal.center,
                    org.bukkit.Sound.BLOCK_RESPAWN_ANCHOR_CHARGE,
                    2.0f,
                    0.5f
            );

            /*
             * Portal particles.
             */
            world.spawnParticle(
                    org.bukkit.Particle.PORTAL,
                    portal.center,
                    150,
                    3.0,
                    3.0,
                    3.0,
                    0.5
            );

            /*
             * Reverse portal particles.
             */
            world.spawnParticle(
                    org.bukkit.Particle.REVERSE_PORTAL,
                    portal.center,
                    100,
                    2.5,
                    2.5,
                    2.5,
                    0.5
            );
        }

        /*
         * =====================================================
         * ACTIVATION MESSAGE
         * =====================================================
         */

        player.sendMessage("");

        player.sendMessage(
                ChatColor.DARK_PURPLE
                        + "✦ "
                        + ChatColor.LIGHT_PURPLE
                        + "WARDEN PORTAL ACTIVATED"
        );

        player.sendMessage(
                ChatColor.GRAY
                        + "The ancient portal awakens..."
        );

        player.sendMessage(
                ChatColor.DARK_GRAY
                        + "Something waits beyond the darkness."
        );

        player.sendMessage("");
    }

    // =========================================================
    // FIND EXISTING PORTAL
    // =========================================================

    private PortalData findExistingWardenPortal(
            Location location
    ) {

        World world =
                location.getWorld();

        if (world == null) {
            return null;
        }

        int baseX =
                location.getBlockX();

        int baseY =
                location.getBlockY();

        int baseZ =
                location.getBlockZ();

        /*
         * Search around the pearl impact.
         */
        for (int x = -SEARCH_RADIUS;
             x <= SEARCH_RADIUS;
             x++) {

            for (int y = -SEARCH_RADIUS;
                 y <= SEARCH_RADIUS;
                 y++) {

                for (int z = -SEARCH_RADIUS;
                     z <= SEARCH_RADIUS;
                     z++) {

                    int centerX =
                            baseX + x;

                    int bottomY =
                            baseY + y;

                    int centerZ =
                            baseZ + z;

                    /*
                     * Check portal where width
                     * runs along X.
                     */
                    PortalData xPortal =
                            checkXPlanePortal(
                                    world,
                                    centerX,
                                    bottomY,
                                    centerZ
                            );

                    if (xPortal != null) {
                        return xPortal;
                    }

                    /*
                     * Check portal where width
                     * runs along Z.
                     */
                    PortalData zPortal =
                            checkZPlanePortal(
                                    world,
                                    centerX,
                                    bottomY,
                                    centerZ
                            );

                    if (zPortal != null) {
                        return zPortal;
                    }
                }
            }
        }

        return null;
    }

    // =========================================================
    // X PLANE PORTAL
    // =========================================================

    private PortalData checkXPlanePortal(
            World world,
            int centerX,
            int bottomY,
            int centerZ
    ) {

        /*
         * IMPORTANT:
         *
         * Portal width is EVEN: 22.
         *
         * We use:
         *
         * leftX  = centerX - 11
         * rightX = centerX + 10
         *
         * Total = 22 blocks.
         */
        int leftX =
                centerX - 11;

        int rightX =
                centerX + 10;

        /*
         * Height = 8.
         */
        int topY =
                bottomY + 7;

        /*
         * -----------------------------------------------------
         * BOTTOM FRAME
         * -----------------------------------------------------
         */
        for (int x = leftX;
             x <= rightX;
             x++) {

            if (!isReinforcedDeepslate(
                    world,
                    x,
                    bottomY,
                    centerZ
            )) {

                return null;
            }
        }

        /*
         * -----------------------------------------------------
         * TOP FRAME
         * -----------------------------------------------------
         */
        for (int x = leftX;
             x <= rightX;
             x++) {

            if (!isReinforcedDeepslate(
                    world,
                    x,
                    topY,
                    centerZ
            )) {

                return null;
            }
        }

        /*
         * -----------------------------------------------------
         * LEFT + RIGHT FRAME
         * -----------------------------------------------------
         */
        for (int y = bottomY;
             y <= topY;
             y++) {

            if (!isReinforcedDeepslate(
                    world,
                    leftX,
                    y,
                    centerZ
            )) {

                return null;
            }

            if (!isReinforcedDeepslate(
                    world,
                    rightX,
                    y,
                    centerZ
            )) {

                return null;
            }
        }

        /*
         * -----------------------------------------------------
         * INNER OPENING
         *
         * Width:
         * 20
         *
         * Height:
         * 6
         * -----------------------------------------------------
         */
        for (int x = leftX + 1;
             x <= rightX - 1;
             x++) {

            for (int y = bottomY + 1;
                 y <= topY - 1;
                 y++) {

                Material type =
                        world.getBlockAt(
                                x,
                                y,
                                centerZ
                        ).getType();

                /*
                 * Reinforced Deepslate inside
                 * the opening means invalid portal.
                 */
                if (type
                        == Material.REINFORCED_DEEPSLATE) {

                    return null;
                }
            }
        }

        /*
         * Calculate exact center.
         */
        double centerXLocation =
                ((leftX + rightX) / 2.0) + 0.5;

        double centerYLocation =
                bottomY + 1.5;

        double centerZLocation =
                centerZ + 0.5;

        Location center =
                new Location(
                        world,
                        centerXLocation,
                        centerYLocation,
                        centerZLocation
                );

        return new PortalData(
                center,
                true
        );
    }

    // =========================================================
    // Z PLANE PORTAL
    // =========================================================

    private PortalData checkZPlanePortal(
            World world,
            int centerX,
            int bottomY,
            int centerZ
    ) {

        /*
         * 22 blocks wide.
         *
         * leftZ  = centerZ - 11
         * rightZ = centerZ + 10
         */
        int leftZ =
                centerZ - 11;

        int rightZ =
                centerZ + 10;

        /*
         * Height = 8.
         */
        int topY =
                bottomY + 7;

        /*
         * -----------------------------------------------------
         * BOTTOM FRAME
         * -----------------------------------------------------
         */
        for (int z = leftZ;
             z <= rightZ;
             z++) {

            if (!isReinforcedDeepslate(
                    world,
                    centerX,
                    bottomY,
                    z
            )) {

                return null;
            }
        }

        /*
         * -----------------------------------------------------
         * TOP FRAME
         * -----------------------------------------------------
         */
        for (int z = leftZ;
             z <= rightZ;
             z++) {

            if (!isReinforcedDeepslate(
                    world,
                    centerX,
                    topY,
                    z
            )) {

                return null;
            }
        }

        /*
         * -----------------------------------------------------
         * LEFT + RIGHT FRAME
         * -----------------------------------------------------
         */
        for (int y = bottomY;
             y <= topY;
             y++) {

            if (!isReinforcedDeepslate(
                    world,
                    centerX,
                    y,
                    leftZ
            )) {

                return null;
            }

            if (!isReinforcedDeepslate(
                    world,
                    centerX,
                    y,
                    rightZ
            )) {

                return null;
            }
        }

        /*
         * -----------------------------------------------------
         * INNER OPENING
         *
         * 20 x 6
         * -----------------------------------------------------
         */
        for (int z = leftZ + 1;
             z <= rightZ - 1;
             z++) {

            for (int y = bottomY + 1;
                 y <= topY - 1;
                 y++) {

                Material type =
                        world.getBlockAt(
                                centerX,
                                y,
                                z
                        ).getType();

                if (type
                        == Material.REINFORCED_DEEPSLATE) {

                    return null;
                }
            }
        }

        /*
         * Calculate exact center.
         */
        double centerXLocation =
                centerX + 0.5;

        double centerYLocation =
                bottomY + 1.5;

        double centerZLocation =
                ((leftZ + rightZ) / 2.0) + 0.5;

        Location center =
                new Location(
                        world,
                        centerXLocation,
                        centerYLocation,
                        centerZLocation
                );

        return new PortalData(
                center,
                false
        );
    }

    // =========================================================
    // REINFORCED DEEPSLATE CHECK
    // =========================================================

    private boolean isReinforcedDeepslate(
            World world,
            int x,
            int y,
            int z
    ) {

        return world
                .getBlockAt(
                        x,
                        y,
                        z
                )
                .getType()
                == Material.REINFORCED_DEEPSLATE;
    }

    // =========================================================
    // PORTAL DATA
    // =========================================================

    private static class PortalData {

        private final Location center;

        /*
         * true:
         * width runs along X
         *
         * false:
         * width runs along Z
         */
        private final boolean xPlane;

        private PortalData(
                Location center,
                boolean xPlane
        ) {

            this.center = center;
            this.xPlane = xPlane;
        }
    }
}