package in.volexmc.skydungeons.warden;

import in.volexmc.skydungeons.SkyDungeons;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class WardenPortalEnterListener implements Listener {

    private final SkyDungeons plugin;

    // =========================================================
    // PORTAL SIZE
    // =========================================================

    /*
     * OUTER PORTAL:
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
     * Distance player can be from the portal plane.
     */
    private static final double PORTAL_DEPTH = 2.5;

    /*
     * Search radius around the PLAYER.
     *
     * IMPORTANT:
     * We no longer search only around the dungeon center.
     */
    private static final int SEARCH_RADIUS = 16;

    /*
     * Search cooldown for each player.
     */
    private static final long SEARCH_COOLDOWN_MS = 500L;

    /*
     * Teleport protection.
     */
    private static final int TELEPORT_COOLDOWN_TICKS = 40;

    /*
     * Portal cache.
     *
     * Dungeon UUID -> PortalData
     */
    private final Map<UUID, PortalData> portalCache =
            new HashMap<>();

    /*
     * Last search time.
     *
     * Player UUID -> time
     */
    private final Map<UUID, Long> lastSearch =
            new HashMap<>();

    public WardenPortalEnterListener(
            SkyDungeons plugin
    ) {
        this.plugin = plugin;
    }

    // =========================================================
    // PLAYER MOVE
    // =========================================================

    @EventHandler
    public void onPlayerMove(
            PlayerMoveEvent event
    ) {

        Location from =
                event.getFrom();

        Location to =
                event.getTo();

        if (to == null) {
            return;
        }

        /*
         * Ignore head rotation.
         *
         * We only care when the player changes
         * actual block position.
         */
        if (from.getBlockX() == to.getBlockX()
                && from.getBlockY() == to.getBlockY()
                && from.getBlockZ() == to.getBlockZ()) {

            return;
        }

        Player player =
                event.getPlayer();

        /*
         * Prevent teleport loop.
         */
        if (player.hasMetadata(
                "skydungeons_warden_teleport"
        )) {
            return;
        }

        World world =
                to.getWorld();

        if (world == null) {
            return;
        }

        /*
         * Warden portal is only in Overworld.
         */
        if (world.getEnvironment()
                != World.Environment.NORMAL) {

            return;
        }

        WardenDungeonManager manager =
                plugin.getWardenDungeonManager();

        /*
         * Find the registered Warden Dungeon.
         */
        WardenDungeonManager.WardenDungeon dungeon =
                manager.getDungeonAt(to);

        if (dungeon == null) {
            return;
        }

        /*
         * Portal must already be activated
         * by the Warden Pearl.
         */
        if (!manager.isActivated(
                dungeon.getId()
        )) {
            return;
        }

        /*
         * Find portal near the PLAYER.
         *
         * This is the important fix.
         */
        PortalData portal =
                getPortalNearPlayer(
                        dungeon,
                        player
                );

        if (portal == null) {
            return;
        }

        /*
         * Check whether player is actually
         * inside the 20 x 6 opening.
         */
        if (!isInsidePortal(
                portal,
                to
        )) {
            return;
        }

        /*
         * Player entered the Warden Portal.
         */
        teleportToBossRealm(player);
    }

    // =========================================================
    // FIND PORTAL NEAR PLAYER
    // =========================================================

    private PortalData getPortalNearPlayer(
            WardenDungeonManager.WardenDungeon dungeon,
            Player player
    ) {

        UUID dungeonId =
                dungeon.getId();

        /*
         * Check cached portal first.
         */
        PortalData cached =
                portalCache.get(dungeonId);

        if (cached != null) {

            if (cached.isValid()
                    && cached.isNear(
                    player.getLocation(),
                    SEARCH_RADIUS + 4
            )) {

                return cached;
            }

            /*
             * Cached portal is no longer valid
             * or player is far from it.
             */
            portalCache.remove(dungeonId);
        }

        UUID playerId =
                player.getUniqueId();

        long now =
                System.currentTimeMillis();

        Long last =
                lastSearch.get(playerId);

        /*
         * Search cooldown.
         */
        if (last != null
                && now - last < SEARCH_COOLDOWN_MS) {

            return null;
        }

        lastSearch.put(
                playerId,
                now
        );

        /*
         * IMPORTANT:
         *
         * Search from PLAYER location,
         * not dungeon center.
         */
        PortalData found =
                findExistingPortalNearPlayer(
                        player.getLocation()
                );

        if (found != null) {

            portalCache.put(
                    dungeonId,
                    found
            );

            return found;
        }

        return null;
    }

    // =========================================================
    // SEARCH PORTAL NEAR PLAYER
    // =========================================================

    private PortalData findExistingPortalNearPlayer(
            Location playerLocation
    ) {

        World world =
                playerLocation.getWorld();

        if (world == null) {
            return null;
        }

        int baseX =
                playerLocation.getBlockX();

        int baseY =
                playerLocation.getBlockY();

        int baseZ =
                playerLocation.getBlockZ();

        /*
         * Search around player's current position.
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
                     * Check X-plane portal.
                     */
                    PortalData xPortal =
                            checkXPlanePortal(
                                    world,
                                    centerX,
                                    bottomY,
                                    centerZ
                            );

                    if (xPortal != null) {

                        if (xPortal.isNear(
                                playerLocation,
                                SEARCH_RADIUS + 4
                        )) {

                            return xPortal;
                        }
                    }

                    /*
                     * Check Z-plane portal.
                     */
                    PortalData zPortal =
                            checkZPlanePortal(
                                    world,
                                    centerX,
                                    bottomY,
                                    centerZ
                            );

                    if (zPortal != null) {

                        if (zPortal.isNear(
                                playerLocation,
                                SEARCH_RADIUS + 4
                        )) {

                            return zPortal;
                        }
                    }
                }
            }
        }

        return null;
    }

    // =========================================================
    // CHECK X-PLANE PORTAL
    // =========================================================

    private PortalData checkXPlanePortal(
            World world,
            int centerX,
            int bottomY,
            int centerZ
    ) {

        /*
         * 22 BLOCKS WIDE.
         *
         * leftX  = centerX - 11
         * rightX = centerX + 10
         *
         * Total = 22
         */
        int leftX =
                centerX - 11;

        int rightX =
                centerX + 10;

        /*
         * 8 BLOCKS HIGH.
         */
        int topY =
                bottomY + 7;

        /*
         * =====================================================
         * BOTTOM FRAME
         * =====================================================
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
         * =====================================================
         * TOP FRAME
         * =====================================================
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
         * =====================================================
         * LEFT + RIGHT FRAME
         * =====================================================
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
         * =====================================================
         * INNER OPENING
         *
         * 20 x 6
         * =====================================================
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
                 * Reinforced Deepslate must not
                 * be inside the opening.
                 */
                if (type
                        == Material.REINFORCED_DEEPSLATE) {

                    return null;
                }
            }
        }

        /*
         * =====================================================
         * PORTAL CENTER
         * =====================================================
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
                true,
                bottomY,
                topY
        );
    }

    // =========================================================
    // CHECK Z-PLANE PORTAL
    // =========================================================

    private PortalData checkZPlanePortal(
            World world,
            int centerX,
            int bottomY,
            int centerZ
    ) {

        /*
         * 22 BLOCKS WIDE.
         *
         * leftZ  = centerZ - 11
         * rightZ = centerZ + 10
         *
         * Total = 22
         */
        int leftZ =
                centerZ - 11;

        int rightZ =
                centerZ + 10;

        /*
         * 8 BLOCKS HIGH.
         */
        int topY =
                bottomY + 7;

        /*
         * =====================================================
         * BOTTOM FRAME
         * =====================================================
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
         * =====================================================
         * TOP FRAME
         * =====================================================
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
         * =====================================================
         * LEFT + RIGHT FRAME
         * =====================================================
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
         * =====================================================
         * INNER OPENING
         *
         * 20 x 6
         * =====================================================
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
         * =====================================================
         * PORTAL CENTER
         * =====================================================
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
                false,
                bottomY,
                topY
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
    // CHECK PLAYER INSIDE PORTAL
    // =========================================================

    private boolean isInsidePortal(
            PortalData portal,
            Location playerLocation
    ) {

        if (portal == null
                || playerLocation == null) {

            return false;
        }

        World portalWorld =
                portal.center.getWorld();

        World playerWorld =
                playerLocation.getWorld();

        if (portalWorld == null
                || playerWorld == null) {

            return false;
        }

        /*
         * Must be the same world.
         */
        if (!portalWorld.getUID()
                .equals(
                        playerWorld.getUID()
                )) {

            return false;
        }

        /*
         * =====================================================
         * X-PLANE
         *
         * Width runs along X.
         * Portal plane is Z.
         * =====================================================
         */
        if (portal.xPlane) {

            /*
             * 20-block inner opening.
             */
            double minX =
                    portal.center.getX()
                            - (INNER_WIDTH / 2.0);

            double maxX =
                    portal.center.getX()
                            + (INNER_WIDTH / 2.0);

            /*
             * Opening starts one block above
             * the bottom frame.
             */
            double minY =
                    portal.bottomY + 1.0;

            /*
             * Opening is 6 blocks high.
             */
            double maxY =
                    minY + INNER_HEIGHT;

            /*
             * Distance from portal plane.
             */
            double dz =
                    Math.abs(
                            playerLocation.getZ()
                                    - portal.center.getZ()
                    );

            return playerLocation.getX() >= minX
                    && playerLocation.getX() <= maxX
                    && playerLocation.getY() >= minY
                    && playerLocation.getY() <= maxY
                    && dz <= PORTAL_DEPTH;
        }

        /*
         * =====================================================
         * Z-PLANE
         *
         * Width runs along Z.
         * Portal plane is X.
         * =====================================================
         */

        double minZ =
                portal.center.getZ()
                        - (INNER_WIDTH / 2.0);

        double maxZ =
                portal.center.getZ()
                        + (INNER_WIDTH / 2.0);

        /*
         * Opening starts one block above
         * the bottom frame.
         */
        double minY =
                portal.bottomY + 1.0;

        /*
         * Opening is 6 blocks high.
         */
        double maxY =
                minY + INNER_HEIGHT;

        /*
         * Distance from portal plane.
         */
        double dx =
                Math.abs(
                        playerLocation.getX()
                                - portal.center.getX()
                );

        return playerLocation.getZ() >= minZ
                && playerLocation.getZ() <= maxZ
                && playerLocation.getY() >= minY
                && playerLocation.getY() <= maxY
                && dx <= PORTAL_DEPTH;
    }

    // =========================================================
    // TELEPORT TO WARDEN REALM
    // =========================================================

    private void teleportToBossRealm(
            Player player
    ) {

        /*
         * Get Warden Boss Realm spawn.
         */
        Location destination =
                plugin.getWardenBossWorldManager()
                        .getBossSpawnLocation();

        /*
         * Destination unavailable.
         */
        if (destination == null) {

            player.sendMessage(
                    ChatColor.RED
                            + "The Warden Boss Realm "
                            + "is currently unavailable."
            );

            plugin.getLogger().severe(
                    "Warden Realm teleport failed for "
                            + player.getName()
                            + ": destination is null."
            );

            return;
        }

        /*
         * Prevent teleport event loop.
         */
        player.setMetadata(
                "skydungeons_warden_teleport",
                new FixedMetadataValue(
                        plugin,
                        true
                )
        );

        /*
         * Teleport player.
         */
        boolean success =
                player.teleport(
                        destination
                );

        /*
         * Teleport failed.
         */
        if (!success) {

            player.removeMetadata(
                    "skydungeons_warden_teleport",
                    plugin
            );

            player.sendMessage(
                    ChatColor.RED
                            + "Could not enter "
                            + "the Warden Realm."
            );

            plugin.getLogger().warning(
                    "Failed to teleport "
                            + player.getName()
                            + " to Warden Realm."
            );

            return;
        }

        // =====================================================
        // SPAWN WARDEN BOSS
        // =====================================================

        WardenBoss wardenBoss =
                plugin.getWardenBoss();

        /*
         * Only spawn a boss if one isn't already alive.
         */
        if (wardenBoss != null
                && !wardenBoss.isAlive()) {

            wardenBoss.spawn();
        }

        // =====================================================
        // ENTER MESSAGE
        // =====================================================

        player.sendMessage("");

        player.sendMessage(
                ChatColor.DARK_PURPLE
                        + "✦ "
                        + ChatColor.LIGHT_PURPLE
                        + "ENTERING THE WARDEN REALM"
        );

        player.sendMessage(
                ChatColor.GRAY
                        + "The ancient darkness surrounds you..."
        );

        player.sendMessage(
                ChatColor.DARK_GRAY
                        + "Something powerful awaits."
        );

        player.sendMessage("");

        // =====================================================
        // REMOVE TELEPORT PROTECTION
        // =====================================================

        plugin.getServer()
                .getScheduler()
                .runTaskLater(
                        plugin,
                        () -> {

                            if (player.isOnline()) {

                                player.removeMetadata(
                                        "skydungeons_warden_teleport",
                                        plugin
                                );
                            }

                        },
                        TELEPORT_COOLDOWN_TICKS
                );
    }

    // =========================================================
    // CLEAR CACHE
    // =========================================================

    public void clearCache() {

        portalCache.clear();
        lastSearch.clear();
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

        /*
         * Bottom and top frame Y.
         */
        private final int bottomY;
        private final int topY;

        private PortalData(
                Location center,
                boolean xPlane,
                int bottomY,
                int topY
        ) {

            this.center = center;
            this.xPlane = xPlane;
            this.bottomY = bottomY;
            this.topY = topY;
        }

        private boolean isValid() {

            return center != null
                    && center.getWorld() != null;
        }

        private boolean isNear(
                Location location,
                double distance
        ) {

            if (location == null) {
                return false;
            }

            if (!isValid()) {
                return false;
            }

            World portalWorld =
                    center.getWorld();

            World locationWorld =
                    location.getWorld();

            if (portalWorld == null
                    || locationWorld == null) {

                return false;
            }

            if (!portalWorld.getUID()
                    .equals(
                            locationWorld.getUID()
                    )) {

                return false;
            }

            return center.distanceSquared(
                    location
            ) <= distance * distance;
        }
    }
}