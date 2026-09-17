package in.volexmc.skydungeons.warden;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.generator.structure.Structure;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.StructureSearchResult;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class WardenDungeonManager {

    private final JavaPlugin plugin;

    private final List<WardenDungeon> dungeons =
            new ArrayList<>();

    private final Set<UUID> activatedDungeons =
            new HashSet<>();

    private final File dataFile;

    private final YamlConfiguration data;

    public WardenDungeonManager(
            JavaPlugin plugin
    ) {

        this.plugin = plugin;

        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdirs();
        }

        dataFile =
                new File(
                        plugin.getDataFolder(),
                        "warden-dungeons.yml"
                );

        data =
                YamlConfiguration.loadConfiguration(
                        dataFile
                );

        loadDungeons();
    }

    public Location findNearestAncientCity(
            Location origin,
            int radius
    ) {

        if (origin == null) {
            return null;
        }

        World world =
                origin.getWorld();

        if (world == null) {
            return null;
        }

        if (world.getEnvironment()
                != World.Environment.NORMAL) {
            return null;
        }

        StructureSearchResult result =
                world.locateNearestStructure(
                        origin,
                        Structure.ANCIENT_CITY,
                        radius,
                        false
                );

        if (result == null) {
            return null;
        }

        return result.getLocation();
    }

    public UUID registerWardenDungeon(
            Location location
    ) {

        if (location == null) {
            return null;
        }

        if (isRegistered(location)) {
            return null;
        }

        UUID id =
                UUID.randomUUID();

        WardenDungeon dungeon =
                new WardenDungeon(
                        id,
                        location.clone()
                );

        dungeons.add(dungeon);

        saveDungeons();

        return id;
    }

    public boolean isRegistered(
            Location location
    ) {

        if (location == null) {
            return false;
        }

        World locationWorld =
                location.getWorld();

        if (locationWorld == null) {
            return false;
        }

        for (WardenDungeon dungeon :
                dungeons) {

            Location registered =
                    dungeon.getLocation();

            World registeredWorld =
                    registered.getWorld();

            if (registeredWorld == null) {
                continue;
            }

            if (!registeredWorld.getUID()
                    .equals(
                            locationWorld.getUID()
                    )) {
                continue;
            }

            if (registered.distanceSquared(
                    location
            ) <= 16 * 16) {

                return true;
            }
        }

        return false;
    }

    public boolean isWardenDungeon(
            Location location
    ) {

        if (location == null) {
            return false;
        }

        World locationWorld =
                location.getWorld();

        if (locationWorld == null) {
            return false;
        }

        for (WardenDungeon dungeon :
                dungeons) {

            Location center =
                    dungeon.getLocation();

            World centerWorld =
                    center.getWorld();

            if (centerWorld == null) {
                continue;
            }

            if (!centerWorld.getUID()
                    .equals(
                            locationWorld.getUID()
                    )) {
                continue;
            }

            if (center.distanceSquared(
                    location
            ) <= 128 * 128) {

                return true;
            }
        }

        return false;
    }

    public WardenDungeon getDungeonAt(
            Location location
    ) {

        if (location == null) {
            return null;
        }

        World locationWorld =
                location.getWorld();

        if (locationWorld == null) {
            return null;
        }

        for (WardenDungeon dungeon :
                dungeons) {

            Location center =
                    dungeon.getLocation();

            World centerWorld =
                    center.getWorld();

            if (centerWorld == null) {
                continue;
            }

            if (!centerWorld.getUID()
                    .equals(
                            locationWorld.getUID()
                    )) {
                continue;
            }

            if (center.distanceSquared(
                    location
            ) <= 128 * 128) {

                return dungeon;
            }
        }

        return null;
    }

    public boolean activateDungeon(
            Location location
    ) {

        WardenDungeon dungeon =
                getDungeonAt(location);

        if (dungeon == null) {
            return false;
        }

        return activateDungeon(
                dungeon.getId()
        );
    }

    public boolean activateDungeon(
            UUID dungeonId
    ) {

        if (dungeonId == null) {
            return false;
        }

        WardenDungeon dungeon =
                getDungeon(dungeonId);

        if (dungeon == null) {
            return false;
        }

        if (activatedDungeons.contains(
                dungeonId
        )) {
            return false;
        }

        activatedDungeons.add(
                dungeonId
        );

        saveDungeons();

        return true;
    }

    public boolean isActivated(
            UUID dungeonId
    ) {

        if (dungeonId == null) {
            return false;
        }

        return activatedDungeons.contains(
                dungeonId
        );
    }

    public boolean isActivated(
            Location location
    ) {

        WardenDungeon dungeon =
                getDungeonAt(location);

        if (dungeon == null) {
            return false;
        }

        return isActivated(
                dungeon.getId()
        );
    }

    public List<WardenDungeon> getDungeons() {

        return new ArrayList<>(
                dungeons
        );
    }

    public int getDungeonCount() {

        return dungeons.size();
    }

    public WardenDungeon getDungeon(
            UUID id
    ) {

        if (id == null) {
            return null;
        }

        for (WardenDungeon dungeon :
                dungeons) {

            if (dungeon.getId()
                    .equals(id)) {

                return dungeon;
            }
        }

        return null;
    }

    public WardenDungeon getActivatedDungeonAt(
            Location location
    ) {

        WardenDungeon dungeon =
                getDungeonAt(location);

        if (dungeon == null) {
            return null;
        }

        if (!isActivated(
                dungeon.getId()
        )) {
            return null;
        }

        return dungeon;
    }

    private void saveDungeons() {

        data.set(
                "dungeons",
                null
        );

        int index = 0;

        for (WardenDungeon dungeon :
                dungeons) {

            String path =
                    "dungeons."
                            + index;

            Location location =
                    dungeon.getLocation();

            if (location.getWorld() == null) {
                continue;
            }

            UUID dungeonId =
                    dungeon.getId();

            data.set(
                    path + ".id",
                    dungeonId.toString()
            );

            data.set(
                    path + ".world",
                    location.getWorld()
                            .getUID()
                            .toString()
            );

            data.set(
                    path + ".world-name",
                    location.getWorld()
                            .getName()
            );

            data.set(
                    path + ".x",
                    location.getX()
            );

            data.set(
                    path + ".y",
                    location.getY()
            );

            data.set(
                    path + ".z",
                    location.getZ()
            );

            data.set(
                    path + ".activated",
                    activatedDungeons.contains(
                            dungeonId
                    )
            );

            index++;
        }

        try {

            data.save(
                    dataFile
            );

        } catch (IOException exception) {

            plugin.getLogger().severe(
                    "Could not save warden-dungeons.yml!"
            );

            exception.printStackTrace();
        }
    }

    private void loadDungeons() {

        ConfigurationSection section =
                data.getConfigurationSection(
                        "dungeons"
                );

        if (section == null) {
            return;
        }

        for (String key :
                section.getKeys(false)) {

            String path =
                    "dungeons."
                            + key;

            String idString =
                    data.getString(
                            path + ".id"
                    );

            String worldUuidString =
                    data.getString(
                            path + ".world"
                    );

            double x =
                    data.getDouble(
                            path + ".x"
                    );

            double y =
                    data.getDouble(
                            path + ".y"
                    );

            double z =
                    data.getDouble(
                            path + ".z"
                    );

            boolean activated =
                    data.getBoolean(
                            path + ".activated",
                            false
                    );

            if (idString == null
                    || worldUuidString == null) {
                continue;
            }

            try {

                UUID id =
                        UUID.fromString(
                                idString
                        );

                UUID worldUuid =
                        UUID.fromString(
                                worldUuidString
                        );

                World world =
                        findWorld(
                                worldUuid
                        );

                if (world == null) {

                    plugin.getLogger().warning(
                            "Could not load Warden Dungeon "
                                    + id
                                    + ": world is not loaded."
                    );

                    continue;
                }

                Location location =
                        new Location(
                                world,
                                x,
                                y,
                                z
                        );

                dungeons.add(
                        new WardenDungeon(
                                id,
                                location
                        )
                );

                if (activated) {

                    activatedDungeons.add(
                            id
                    );
                }

            } catch (IllegalArgumentException exception) {

                plugin.getLogger().warning(
                        "Invalid Warden Dungeon data at "
                                + path
                );
            }
        }

        plugin.getLogger().info(
                "Loaded "
                        + dungeons.size()
                        + " Warden Dungeon(s)."
        );

        plugin.getLogger().info(
                "Loaded "
                        + activatedDungeons.size()
                        + " activated Warden Portal(s)."
        );
    }

    private World findWorld(
            UUID uuid
    ) {

        for (World world :
                plugin.getServer().getWorlds()) {

            if (world.getUID()
                    .equals(uuid)) {

                return world;
            }
        }

        return null;
    }

    public void clear() {

        dungeons.clear();

        activatedDungeons.clear();
    }

    public static class WardenDungeon {

        private final UUID id;

        private final Location location;

        public WardenDungeon(
                UUID id,
                Location location
        ) {

            this.id = id;

            this.location =
                    location.clone();
        }

        public UUID getId() {

            return id;
        }

        public Location getLocation() {

            return location.clone();
        }
    }
}