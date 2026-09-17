package in.volexmc.skydungeons.warden;

import in.volexmc.skydungeons.SkyDungeons;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class WardenDungeonListener implements Listener {

    private final SkyDungeons plugin;

    /*
     * Last location checked for each player.
     *
     * This prevents structure searches from happening
     * on every single PlayerMoveEvent.
     */
    private final Map<UUID, Location> lastChecked =
            new HashMap<>();

    /*
     * Search again after the player has moved
     * approximately this many blocks.
     */
    private static final double CHECK_DISTANCE = 128.0;

    /*
     * Ancient City search radius in chunks.
     *
     * 32 chunks = approximately 512 blocks.
     */
    private static final int SEARCH_RADIUS = 32;

    public WardenDungeonListener(
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

        Location to =
                event.getTo();

        if (to == null) {
            return;
        }

        Location from =
                event.getFrom();

        /*
         * Ignore head rotation and movement inside
         * the same block.
         */
        if (from.getBlockX() == to.getBlockX()
                && from.getBlockY() == to.getBlockY()
                && from.getBlockZ() == to.getBlockZ()) {

            return;
        }

        Player player =
                event.getPlayer();

        // =====================================================
        // OVERWORLD ONLY
        // =====================================================

        World world =
                player.getWorld();

        if (world.getEnvironment()
                != World.Environment.NORMAL) {

            return;
        }

        // =====================================================
        // DISTANCE CHECK
        // =====================================================

        UUID playerId =
                player.getUniqueId();

        Location previous =
                lastChecked.get(playerId);

        if (previous != null) {

            /*
             * If the player has not moved far enough,
             * do not search again.
             */
            if (previous.getWorld() != null
                    && previous.getWorld()
                    .getUID()
                    .equals(world.getUID())
                    && previous.distanceSquared(to)
                    < CHECK_DISTANCE * CHECK_DISTANCE) {

                return;
            }
        }

        /*
         * Remember this location as the latest
         * structure-search point.
         */
        lastChecked.put(
                playerId,
                to.clone()
        );

        // =====================================================
        // FIND NEAREST ANCIENT CITY
        // =====================================================

        Location nearest =
                plugin.getWardenDungeonManager()
                        .findNearestAncientCity(
                                to,
                                SEARCH_RADIUS
                        );

        if (nearest == null) {
            return;
        }

        // =====================================================
        // ALREADY REGISTERED
        // =====================================================

        if (plugin.getWardenDungeonManager()
                .isRegistered(nearest)) {

            return;
        }

        // =====================================================
        // REGISTER DUNGEON
        // =====================================================

        UUID dungeonId =
                plugin.getWardenDungeonManager()
                        .registerWardenDungeon(
                                nearest
                        );

        if (dungeonId == null) {
            return;
        }

        // =====================================================
        // PLAYER MESSAGE
        // =====================================================

        player.sendMessage(
                "§8[§5SkyDungeons§8] §5✦ §d"
                        + "You sense an ancient presence nearby..."
        );

        // =====================================================
        // SERVER LOG
        // =====================================================

        plugin.getLogger().info(
                "Warden Dungeon discovered by "
                        + player.getName()
                        + " at "
                        + nearest.getBlockX()
                        + ", "
                        + nearest.getBlockY()
                        + ", "
                        + nearest.getBlockZ()
                        + " | ID: "
                        + dungeonId
        );
    }

    // =========================================================
    // PLAYER QUIT
    // =========================================================

    @EventHandler
    public void onPlayerQuit(
            org.bukkit.event.player.PlayerQuitEvent event
    ) {

        /*
         * Remove the player's temporary search data.
         *
         * The actual dungeon locations are safely stored
         * by WardenDungeonManager.
         */
        lastChecked.remove(
                event.getPlayer().getUniqueId()
        );
    }

    // =========================================================
    // CLEAR CACHE
    // =========================================================

    public void clearCache() {

        lastChecked.clear();
    }
}