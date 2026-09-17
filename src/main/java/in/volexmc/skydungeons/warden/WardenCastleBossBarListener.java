package in.volexmc.skydungeons.warden;

import in.volexmc.skydungeons.SkyDungeons;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Step 5:
 * Shows the Warden Castle boss bar while a player is inside
 * the underground Warden Castle area.
 */
public class WardenCastleBossBarListener implements Listener {

    private static final String WORLD_NAME =
            "skydungeons_warden_realm_v5";

    private static final int CASTLE_X = 0;
    private static final int CASTLE_Z = -10;

    /*
     * The castle is approximately 116 x 108 blocks.
     * 62 blocks gives a little breathing room around the walls.
     */
    private static final double CASTLE_RADIUS = 62.0;

    private static final int MIN_Y = 48;
    private static final int MAX_Y = 110;

    private final SkyDungeons plugin;

    private final Map<UUID, BossBar> bars =
            new HashMap<>();

    public WardenCastleBossBarListener(
            SkyDungeons plugin
    ) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerMove(
            PlayerMoveEvent event
    ) {

        if (event.getTo() == null) {
            return;
        }

        /*
         * Don't run for head rotation only.
         */
        if (event.getFrom().getBlockX()
                == event.getTo().getBlockX()
                && event.getFrom().getBlockY()
                == event.getTo().getBlockY()
                && event.getFrom().getBlockZ()
                == event.getTo().getBlockZ()) {
            return;
        }

        updatePlayer(
                event.getPlayer()
        );
    }

    @EventHandler
    public void onWorldChange(
            PlayerChangedWorldEvent event
    ) {

        updatePlayer(
                event.getPlayer()
        );
    }

    @EventHandler
    public void onQuit(
            PlayerQuitEvent event
    ) {

        removeBar(
                event.getPlayer()
        );
    }

    private void updatePlayer(
            Player player
    ) {

        if (!isInsideCastle(player)) {
            removeBar(player);
            return;
        }

        BossBar bar =
                bars.get(
                        player.getUniqueId()
                );

        if (bar == null) {

            bar =
                    Bukkit.createBossBar(
                            ChatColor.DARK_PURPLE
                                    + "✦ "
                                    + ChatColor.LIGHT_PURPLE
                                    + "WARDEN KING",
                            BarColor.PURPLE,
                            BarStyle.SEGMENTED_10
                    );

            bar.setProgress(1.0);
            bar.setVisible(true);

            bars.put(
                    player.getUniqueId(),
                    bar
            );
        }

        if (!bar.getPlayers().contains(player)) {
            bar.addPlayer(player);
        }
    }

    private boolean isInsideCastle(
            Player player
    ) {

        World world =
                player.getWorld();

        if (world == null) {
            return false;
        }

        if (!world.getName().equals(WORLD_NAME)) {
            return false;
        }

        double dx =
                player.getLocation().getX()
                        - CASTLE_X;

        double dz =
                player.getLocation().getZ()
                        - CASTLE_Z;

        double distanceSquared =
                dx * dx + dz * dz;

        if (distanceSquared
                > CASTLE_RADIUS * CASTLE_RADIUS) {
            return false;
        }

        int y =
                player.getLocation().getBlockY();

        return y >= MIN_Y && y <= MAX_Y;
    }

    private void removeBar(
            Player player
    ) {

        BossBar bar =
                bars.remove(
                        player.getUniqueId()
                );

        if (bar == null) {
            return;
        }

        bar.removePlayer(player);
        bar.removeAll();
    }

    /**
     * Removes all active bars during plugin shutdown.
     */
    public void removeAllBars() {

        for (BossBar bar :
                bars.values()) {

            bar.removeAll();
        }

        bars.clear();
    }
}
