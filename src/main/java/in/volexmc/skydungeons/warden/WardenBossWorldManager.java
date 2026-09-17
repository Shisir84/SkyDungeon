package in.volexmc.skydungeons.warden;

import in.volexmc.skydungeons.SkyDungeons;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.generator.WorldInfo;

import java.util.Random;

public class WardenBossWorldManager {

    private final SkyDungeons plugin;

    /*
     * =========================================================
     * WARDEN REALM V5
     * =========================================================
     */

    private static final String WORLD_NAME =
            "skydungeons_warden_realm_v5";

    /*
     * Main center.
     */
    private static final int CENTER_X = 0;
    private static final int CENTER_Z = 0;

    /*
     * Future castle location.
     */
    private static final int CASTLE_X = 0;
    private static final int CASTLE_Z = -10;

    /*
     * Player arrival location.
     */
    private static final int SPAWN_X = 0;
    private static final int SPAWN_Y = 62;
    private static final int SPAWN_Z = 150;

    private World world;

    /*
     * =========================================================
     * CONSTRUCTOR
     * =========================================================
     */

    public WardenBossWorldManager(
            SkyDungeons plugin
    ) {
        this.plugin = plugin;
    }

    /*
     * =========================================================
     * GET / CREATE WORLD
     * =========================================================
     */

    public World getWorld() {

        if (world != null) {
            return world;
        }

        World existing =
                Bukkit.getWorld(WORLD_NAME);

        if (existing != null) {

            world = existing;

            configureWorld(world);

            return world;
        }

        plugin.getLogger().info(
                "======================================"
        );

        plugin.getLogger().info(
                "Creating Warden Realm V5..."
        );

        plugin.getLogger().info(
                "Custom Underground Biome"
        );

        plugin.getLogger().info(
                "Custom Trees & Vegetation"
        );

        plugin.getLogger().info(
                "======================================"
        );

        WorldCreator creator =
                new WorldCreator(
                        WORLD_NAME
                );

        creator.type(
                WorldType.NORMAL
        );

        /*
         * We don't want normal vanilla structures.
         * Our own structures will be generated later.
         */
        creator.generateStructures(
                false
        );

        creator.generator(
                new WardenUndergroundGenerator()
        );

        world =
                Bukkit.createWorld(
                        creator
                );

        if (world == null) {

            plugin.getLogger().severe(
                    "Failed to create Warden Realm V5!"
            );

            return null;
        }

        configureWorld(world);

        Location spawn =
                getBossSpawnLocation();

        if (spawn != null) {

            world.setSpawnLocation(
                    spawn
            );
        }

        world.save();

        plugin.getLogger().info(
                "Warden Realm V5 created successfully."
        );

        return world;
    }

    /*
     * =========================================================
     * WORLD SETTINGS
     * =========================================================
     */

    private void configureWorld(
            World world
    ) {

        world.setAutoSave(
                true
        );

        world.setStorm(
                false
        );

        world.setThundering(
                false
        );

        world.setSpawnFlags(
                false,
                false
        );
    }

    /*
     * =========================================================
     * PLAYER SPAWN
     * =========================================================
     */

    public Location getBossSpawnLocation() {

        World w =
                getWorld();

        if (w == null) {
            return null;
        }

        return new Location(
                w,
                SPAWN_X + 0.5,
                SPAWN_Y,
                SPAWN_Z + 0.5,
                180.0f,
                0.0f
        );
    }

    /*
     * =========================================================
     * FUTURE BOSS LOCATION
     * =========================================================
     */

    public Location getBossLocation() {

        World w =
                getWorld();

        if (w == null) {
            return null;
        }

        return new Location(
                w,
                CASTLE_X + 0.5,
                64,
                CASTLE_Z + 0.5,
                180.0f,
                0.0f
        );
    }

    public String getWorldName() {

        return WORLD_NAME;
    }

    /*
     * =========================================================
     * CUSTOM UNDERGROUND GENERATOR
     * =========================================================
     */

    private static class WardenUndergroundGenerator
            extends ChunkGenerator {

        /*
         * Lowest world level.
         */
        private static final int MIN_Y =
                -64;

        /*
         * Main cave ceiling.
         */
        private static final int BASE_CEILING =
                145;

        /*
         * =====================================================
         * GENERATE CHUNK
         * =====================================================
         */

        @Override
        public void generateNoise(
                WorldInfo worldInfo,
                Random random,
                int chunkX,
                int chunkZ,
                ChunkData data
        ) {

            /*
             * Generate every block in the chunk.
             */
            for (
                    int localX = 0;
                    localX < 16;
                    localX++
            ) {

                for (
                        int localZ = 0;
                        localZ < 16;
                        localZ++
                ) {

                    int worldX =
                            chunkX * 16
                                    +
                                    localX;

                    int worldZ =
                            chunkZ * 16
                                    +
                                    localZ;

                    /*
                     * Calculate floor.
                     */
                    int floor =
                            getFloorHeight(
                                    worldX,
                                    worldZ
                            );

                    /*
                     * Calculate cave ceiling.
                     */
                    int ceiling =
                            getCeilingHeight(
                                    worldX,
                                    worldZ
                            );

                    /*
                     * =================================================
                     * BEDROCK
                     * =================================================
                     */

                    data.setBlock(
                            localX,
                            MIN_Y,
                            localZ,
                            Material.BEDROCK
                    );

                    /*
                     * =================================================
                     * DEEP UNDERGROUND
                     * =================================================
                     */

                    for (
                            int y = MIN_Y + 1;
                            y < floor - 5;
                            y++
                    ) {

                        Material material;

                        /*
                         * Lower layer.
                         */
                        if (
                                y < -20
                        ) {

                            material =
                                    Material.DEEPSLATE;

                        } else if (
                                y % 19 == 0
                        ) {

                            material =
                                    Material.TUFF;

                        } else {

                            material =
                                    Material.COBBLED_DEEPSLATE;
                        }

                        data.setBlock(
                                localX,
                                y,
                                localZ,
                                material
                        );
                    }

                    /*
                     * =================================================
                     * FLOOR ROCK LAYER
                     * =================================================
                     */

                    for (
                            int y =
                            Math.max(
                                    MIN_Y + 1,
                                    floor - 5
                            );
                            y < floor;
                            y++
                    ) {

                        data.setBlock(
                                localX,
                                y,
                                localZ,
                                Material.DEEPSLATE
                        );
                    }

                    /*
                     * =================================================
                     * CAVERN FLOOR
                     * =================================================
                     */

                    data.setBlock(
                            localX,
                            floor,
                            localZ,
                            getFloorMaterial(
                                    worldX,
                                    worldZ
                            )
                    );

                    /*
                     * =================================================
                     * HUGE OPEN CAVERN
                     * =================================================
                     */

                    for (
                            int y = floor + 1;
                            y < ceiling;
                            y++
                    ) {

                        data.setBlock(
                                localX,
                                y,
                                localZ,
                                Material.AIR
                        );
                    }

                    /*
                     * =================================================
                     * CAVE CEILING
                     * =================================================
                     */

                    for (
                            int y = ceiling;
                            y <= 160;
                            y++
                    ) {

                        Material material;

                        if (
                                y == ceiling
                        ) {

                            material =
                                    Material.DEEPSLATE;

                        } else if (
                                y % 11 == 0
                        ) {

                            material =
                                    Material.TUFF;

                        } else {

                            material =
                                    Material.DEEPSLATE;
                        }

                        data.setBlock(
                                localX,
                                y,
                                localZ,
                                material
                        );
                    }

                    /*
                     * =================================================
                     * UNDERGROUND LAKE
                     * =================================================
                     */

                    if (
                            isLake(
                                    worldX,
                                    worldZ
                            )
                    ) {

                        int lakeLevel =
                                32;

                        if (
                                floor < lakeLevel
                        ) {

                            for (
                                    int y =
                                    floor + 1;
                                    y <= lakeLevel;
                                    y++
                            ) {

                                data.setBlock(
                                        localX,
                                        y,
                                        localZ,
                                        Material.WATER
                                );
                            }
                        }
                    }
                }
            }

            /*
             * =================================================
             * NATURAL CAVE FORMATIONS
             * =================================================
             */

            generateNaturalCavePillars(
                    data,
                    chunkX,
                    chunkZ
            );

            generateCeilingFormations(
                    data,
                    chunkX,
                    chunkZ
            );

            generateSculkAreas(
                    data,
                    chunkX,
                    chunkZ
            );

            generateRockIslands(
                    data,
                    chunkX,
                    chunkZ
            );

            /*
             * =================================================
             * STEP 3 - ANCIENT WORLD STRUCTURES
             * =================================================
             */

            generateStep3Structures(
                    data,
                    chunkX,
                    chunkZ
            );

            /*
             * =================================================
             * STEP 4 - UNDERGROUND WARDEN CASTLE
             * =================================================
             */

            generateWardenCastle(
                    data,
                    chunkX,
                    chunkZ
            );

            /*
             * =================================================
             * CUSTOM TREES
             * =================================================
             */

            generateWardenTrees(
                    data,
                    chunkX,
                    chunkZ
            );

            /*
             * =================================================
             * CUSTOM VEGETATION
             * =================================================
             */

            generateWardenVegetation(
                    data,
                    chunkX,
                    chunkZ
            );
        }

        /*
         * =========================================================
         * FLOOR HEIGHT
         * =========================================================
         */

        private int getFloorHeight(
                int x,
                int z
        ) {

            /*
             * Large terrain waves.
             */
            double large =
                    Math.sin(
                            x * 0.0045
                    )
                            *
                            Math.cos(
                                    z * 0.0052
                            );

            /*
             * Medium terrain.
             */
            double medium =
                    Math.sin(
                            x * 0.015
                    )
                            *
                            Math.cos(
                                    z * 0.017
                            );

            /*
             * Small terrain details.
             */
            double small =
                    Math.sin(
                            x * 0.055
                    )
                            *
                            Math.cos(
                                    z * 0.062
                            );

            /*
             * Huge mountain system.
             */
            double mountains =
                    Math.sin(
                            x * 0.0024
                    )
                            *
                            Math.cos(
                                    z * 0.0028
                            );

            double height =
                    45
                            +
                            large * 22
                            +
                            medium * 13
                            +
                            small * 5
                            +
                            mountains * 35;

            /*
             * Valleys.
             */
            double valley =
                    Math.sin(
                            x * 0.009
                    )
                            +
                            Math.cos(
                                    z * 0.011
                            );

            if (
                    valley > 1.25
            ) {

                height -= 22;
            }

            /*
             * Central basin.
             */
            double distance =
                    Math.sqrt(
                            x * x
                                    +
                                    z * z
                    );

            if (
                    distance < 65
            ) {

                height -= 8;
            }

            /*
             * Side mountains.
             */
            if (
                    Math.abs(x) > 65
                            &&
                            Math.abs(x) < 125
            ) {

                height += 12;
            }

            return Math.max(
                    12,
                    Math.min(
                            100,
                            (int) height
                    )
            );
        }

        /*
         * =========================================================
         * CEILING HEIGHT
         * =========================================================
         */

        private int getCeilingHeight(
                int x,
                int z
        ) {

            double large =
                    Math.sin(
                            x * 0.008
                    )
                            *
                            Math.cos(
                                    z * 0.007
                            );

            double medium =
                    Math.sin(
                            (x + z) * 0.014
                    );

            int ceiling =
                    BASE_CEILING
                            +
                            (int)
                                    (large * 10)
                            +
                            (int)
                                    (medium * 7);

            /*
             * Giant central chamber.
             */
            double distance =
                    Math.sqrt(
                            x * x
                                    +
                                    z * z
                    );

            if (
                    distance < 110
            ) {

                ceiling += 10;
            }

            return Math.max(
                    115,
                    Math.min(
                            160,
                            ceiling
                    )
            );
        }

        /*
         * =========================================================
         * LAKE
         * =========================================================
         */

        private boolean isLake(
                int x,
                int z
        ) {

            double lakeNoise =
                    Math.sin(
                            x * 0.012
                    )
                            +
                            Math.cos(
                                    z * 0.014
                            );

            double distance =
                    Math.sqrt(
                            x * x
                                    +
                                    z * z
                    );

            return distance < 115
                    &&
                    lakeNoise > 1.15;
        }

        /*
         * =========================================================
         * FLOOR MATERIAL
         * =========================================================
         */

        private Material getFloorMaterial(
                int x,
                int z
        ) {

            long seed =
                    ((long) x *
                            341873128712L)
                            ^
                            ((long) z *
                                    132897987541L);

            Random random =
                    new Random(seed);

            int value =
                    random.nextInt(
                            100
                    );

            if (
                    value < 34
            ) {

                return Material.SCULK;
            }

            if (
                    value < 48
            ) {

                return Material.SCULK_CATALYST;
            }

            if (
                    value < 65
            ) {

                return Material.DEEPSLATE;
            }

            if (
                    value < 78
            ) {

                return Material.TUFF;
            }

            if (
                    value < 90
            ) {

                return Material.COBBLED_DEEPSLATE;
            }

            return Material.POLISHED_DEEPSLATE;
        }

        /*
         * =========================================================
         * NATURAL CAVE PILLARS
         * =========================================================
         */

        private void generateNaturalCavePillars(
                ChunkData data,
                int chunkX,
                int chunkZ
        ) {

            long seed =
                    ((long) chunkX *
                            9384721L)
                            ^
                            ((long) chunkZ *
                                    8374921L);

            Random random =
                    new Random(seed);

            if (
                    random.nextInt(
                            100
                    ) > 12
            ) {

                return;
            }

            int x =
                    random.nextInt(
                            14
                    ) + 1;

            int z =
                    random.nextInt(
                            14
                    ) + 1;

            int worldX =
                    chunkX * 16 + x;

            int worldZ =
                    chunkZ * 16 + z;

            int floor =
                    getFloorHeight(
                            worldX,
                            worldZ
                    );

            int ceiling =
                    getCeilingHeight(
                            worldX,
                            worldZ
                    );

            int height =
                    20 +
                            random.nextInt(
                                    50
                            );

            height =
                    Math.min(
                            height,
                            ceiling - floor - 15
                    );

            if (
                    height < 10
            ) {

                return;
            }

            for (
                    int y = floor;
                    y < floor + height;
                    y++
            ) {

                int radius =
                        Math.max(
                                1,
                                5 -
                                        (y - floor) / 15
                        );

                for (
                        int dx = -radius;
                        dx <= radius;
                        dx++
                ) {

                    for (
                            int dz = -radius;
                            dz <= radius;
                            dz++
                    ) {

                        if (
                                dx * dx +
                                        dz * dz
                                        >
                                        radius * radius
                        ) {

                            continue;
                        }

                        int px =
                                x + dx;

                        int pz =
                                z + dz;

                        if (
                                px < 0
                                        ||
                                        px >= 16
                                        ||
                                        pz < 0
                                        ||
                                        pz >= 16
                        ) {

                            continue;
                        }

                        Material material;

                        if (
                                y % 9 == 0
                        ) {

                            material =
                                    Material.TUFF;

                        } else {

                            material =
                                    Material.DEEPSLATE;
                        }

                        data.setBlock(
                                px,
                                y,
                                pz,
                                material
                        );
                    }
                }
            }
        }

        /*
         * =========================================================
         * CEILING FORMATIONS
         * =========================================================
         */

        private void generateCeilingFormations(
                ChunkData data,
                int chunkX,
                int chunkZ
        ) {

            long seed =
                    ((long) chunkX *
                            4728391L)
                            ^
                            ((long) chunkZ *
                                    1938472L);

            Random random =
                    new Random(seed);

            int amount =
                    random.nextInt(
                            4
                    );

            for (
                    int i = 0;
                    i < amount;
                    i++
            ) {

                int x =
                        random.nextInt(
                                16
                        );

                int z =
                        random.nextInt(
                                16
                        );

                int worldX =
                        chunkX * 16 + x;

                int worldZ =
                        chunkZ * 16 + z;

                int ceiling =
                        getCeilingHeight(
                                worldX,
                                worldZ
                        );

                int floor =
                        getFloorHeight(
                                worldX,
                                worldZ
                        );

                int length =
                        7 +
                                random.nextInt(
                                        22
                                );

                if (
                        ceiling - length
                                <=
                                floor + 25
                ) {

                    continue;
                }

                for (
                        int y =
                        ceiling - 1;
                        y >
                                ceiling - length;
                        y--
                ) {

                    int progress =
                            ceiling - y;

                    int radius =
                            Math.max(
                                    0,
                                    4 -
                                            progress / 6
                            );

                    for (
                            int dx =
                            -radius;
                            dx <= radius;
                            dx++
                    ) {

                        for (
                                int dz =
                                -radius;
                                dz <= radius;
                                dz++
                        ) {

                            int px =
                                    x + dx;

                            int pz =
                                    z + dz;

                            if (
                                    px < 0
                                            ||
                                            px >= 16
                                            ||
                                            pz < 0
                                            ||
                                            pz >= 16
                            ) {

                                continue;
                            }

                            data.setBlock(
                                    px,
                                    y,
                                    pz,
                                    Material.DEEPSLATE
                            );
                        }
                    }
                }
            }
        }

        /*
         * =========================================================
         * SCULK AREAS
         * =========================================================
         */

        private void generateSculkAreas(
                ChunkData data,
                int chunkX,
                int chunkZ
        ) {

            long seed =
                    ((long) chunkX *
                            111113L)
                            ^
                            ((long) chunkZ *
                                    777719L);

            Random random =
                    new Random(seed);

            int patches =
                    2 +
                            random.nextInt(
                                    4
                            );

            for (
                    int i = 0;
                    i < patches;
                    i++
            ) {

                int centerX =
                        random.nextInt(
                                16
                        );

                int centerZ =
                        random.nextInt(
                                16
                        );

                int worldX =
                        chunkX * 16
                                +
                                centerX;

                int worldZ =
                        chunkZ * 16
                                +
                                centerZ;

                int floor =
                        getFloorHeight(
                                worldX,
                                worldZ
                        );

                /*
                 * Don't place sculk underwater.
                 */
                if (
                        isLake(
                                worldX,
                                worldZ
                        )
                ) {

                    continue;
                }

                int radius =
                        2 +
                                random.nextInt(
                                        6
                                );

                for (
                        int dx =
                        -radius;
                        dx <= radius;
                        dx++
                ) {

                    for (
                            int dz =
                            -radius;
                            dz <= radius;
                            dz++
                    ) {

                        if (
                                dx * dx +
                                        dz * dz
                                        >
                                        radius * radius
                        ) {

                            continue;
                        }

                        int px =
                                centerX + dx;

                        int pz =
                                centerZ + dz;

                        if (
                                px < 0
                                        ||
                                        px >= 16
                                        ||
                                        pz < 0
                                        ||
                                        pz >= 16
                        ) {

                            continue;
                        }

                        data.setBlock(
                                px,
                                floor,
                                pz,
                                Material.SCULK
                        );
                    }
                }

                /*
                 * Catalyst.
                 */
                if (
                        random.nextInt(
                                100
                        ) < 35
                ) {

                    data.setBlock(
                            centerX,
                            floor + 1,
                            centerZ,
                            Material.SCULK_CATALYST
                    );
                }
            }
        }

        /*
         * =========================================================
         * ROCK ISLANDS
         * =========================================================
         */

        private void generateRockIslands(
                ChunkData data,
                int chunkX,
                int chunkZ
        ) {

            long seed =
                    ((long) chunkX *
                            839201L)
                            ^
                            ((long) chunkZ *
                                    293847L);

            Random random =
                    new Random(seed);

            if (
                    random.nextInt(
                            100
                    ) > 8
            ) {

                return;
            }

            int x =
                    random.nextInt(
                            12
                    ) + 2;

            int z =
                    random.nextInt(
                            12
                    ) + 2;

            int worldX =
                    chunkX * 16 + x;

            int worldZ =
                    chunkZ * 16 + z;

            int floor =
                    getFloorHeight(
                            worldX,
                            worldZ
                    );

            if (
                    isLake(
                            worldX,
                            worldZ
                    )
            ) {

                return;
            }

            int height =
                    8 +
                            random.nextInt(
                                    18
                            );

            for (
                    int y = 0;
                    y < height;
                    y++
            ) {

                int radius =
                        Math.max(
                                1,
                                5 -
                                        y / 5
                        );

                for (
                        int dx =
                        -radius;
                        dx <= radius;
                        dx++
                ) {

                    for (
                            int dz =
                            -radius;
                            dz <= radius;
                            dz++
                    ) {

                        if (
                                dx * dx +
                                        dz * dz
                                        >
                                        radius * radius
                        ) {

                            continue;
                        }

                        int px =
                                x + dx;

                        int pz =
                                z + dz;

                        if (
                                px < 0
                                        ||
                                        px >= 16
                                        ||
                                        pz < 0
                                        ||
                                        pz >= 16
                        ) {

                            continue;
                        }

                        Material material;

                        if (
                                y == height - 1
                        ) {

                            material =
                                    Material.SCULK;

                        } else {

                            material =
                                    Material.DEEPSLATE;
                        }

                        data.setBlock(
                                px,
                                floor + y,
                                pz,
                                material
                        );
                    }
                }
            }
        }

        /*
         * =========================================================
         * STEP 3 - LARGE UNDERGROUND WORLD FEATURES
         * =========================================================
         */

        private void generateStep3Structures(
                ChunkData data,
                int chunkX,
                int chunkZ
        ) {

            long seed =
                    ((long) chunkX * 712367821L)
                            ^
                            ((long) chunkZ * 193847231L)
                            ^
                            0x5EED1234L;

            Random random = new Random(seed);

            /*
             * Large stone ruins.
             */
            if (random.nextInt(100) < 7) {
                generateAncientRuin(data, chunkX, chunkZ, random);
            }

            /*
             * Warden monuments.
             */
            if (random.nextInt(100) < 3) {
                generateWardenMonument(data, chunkX, chunkZ, random);
            }

            /*
             * Ancient bridges.
             */
            if (random.nextInt(100) < 4) {
                generateAncientBridge(data, chunkX, chunkZ, random);
            }

            /*
             * Waterfall source.
             *
             * This creates a controlled vertical water stream from
             * high cavern walls toward the lower terrain. It is only
             * generated when enough vertical space exists.
             */
            if (random.nextInt(100) < 5) {
                generateWardenWaterfall(data, chunkX, chunkZ, random);
            }
        }

        /*
         * =========================================================
         * ANCIENT RUIN
         * =========================================================
         */

        private void generateAncientRuin(
                ChunkData data,
                int chunkX,
                int chunkZ,
                Random random
        ) {

            int centerX = 8;
            int centerZ = 8;

            int worldX = chunkX * 16 + centerX;
            int worldZ = chunkZ * 16 + centerZ;

            int ground = getFloorHeight(worldX, worldZ);

            if (ground < 22 || isLake(worldX, worldZ)) {
                return;
            }

            int radius = 5 + random.nextInt(4);
            int height = 5 + random.nextInt(5);

            for (int y = 0; y <= height; y++) {

                for (int dx = -radius; dx <= radius; dx++) {

                    for (int dz = -radius; dz <= radius; dz++) {

                        int px = centerX + dx;
                        int pz = centerZ + dz;

                        if (px < 0 || px >= 16 || pz < 0 || pz >= 16) {
                            continue;
                        }

                        double distance =
                                Math.sqrt(dx * dx + dz * dz);

                        if (distance > radius) {
                            continue;
                        }

                        /*
                         * Broken outer walls.
                         */
                        boolean wall =
                                Math.abs(dx) == radius
                                        ||
                                        Math.abs(dz) == radius;

                        /*
                         * Broken corners and collapsed sections.
                         */
                        if (wall && random.nextInt(100) < 20) {
                            continue;
                        }

                        if (wall || y == 0) {

                            Material material;

                            if (random.nextInt(100) < 18) {
                                material = Material.TUFF;
                            } else if (random.nextInt(100) < 12) {
                                material = Material.COBBLED_DEEPSLATE;
                            } else {
                                material = Material.POLISHED_DEEPSLATE;
                            }

                            setSafeBlock(
                                    data,
                                    px,
                                    ground + y,
                                    pz,
                                    material
                            );
                        }
                    }
                }
            }

            /*
             * Ancient pillars.
             */
            for (int side = -1; side <= 1; side += 2) {

                for (int i = 0; i < 2; i++) {

                    int px = centerX + side * (radius - 1);
                    int pz = centerZ + (i == 0 ? -radius + 1 : radius - 1);

                    int pillarHeight =
                            3 + random.nextInt(5);

                    for (int y = 1; y <= pillarHeight; y++) {

                        setSafeBlock(
                                data,
                                px,
                                ground + y,
                                pz,
                                y % 4 == 0
                                        ? Material.TUFF
                                        : Material.DEEPSLATE_BRICKS
                        );
                    }
                }
            }

            /*
             * Central ancient altar.
             */
            setSafeBlock(
                    data,
                    centerX,
                    ground + 1,
                    centerZ,
                    Material.POLISHED_DEEPSLATE
            );

            setSafeBlock(
                    data,
                    centerX,
                    ground + 2,
                    centerZ,
                    Material.SCULK_CATALYST
            );
        }

        /*
         * =========================================================
         * WARDEN MONUMENT
         * =========================================================
         */

        private void generateWardenMonument(
                ChunkData data,
                int chunkX,
                int chunkZ,
                Random random
        ) {

            int x = 8;
            int z = 8;

            int worldX = chunkX * 16 + x;
            int worldZ = chunkZ * 16 + z;

            int ground = getFloorHeight(worldX, worldZ);

            if (ground < 28 || isLake(worldX, worldZ)) {
                return;
            }

            int height = 14 + random.nextInt(10);

            /*
             * Central tower.
             */
            for (int y = 1; y <= height; y++) {

                int radius =
                        y < 5
                                ? 4
                                : y < 10
                                ? 3
                                : 2;

                for (int dx = -radius; dx <= radius; dx++) {

                    for (int dz = -radius; dz <= radius; dz++) {

                        if (dx * dx + dz * dz > radius * radius) {
                            continue;
                        }

                        setSafeBlock(
                                data,
                                x + dx,
                                ground + y,
                                z + dz,
                                y % 5 == 0
                                        ? Material.TUFF
                                        : Material.DEEPSLATE
                        );
                    }
                }
            }

            /*
             * Four monument arms.
             */
            int armLength = 7;

            buildMonumentArm(
                    data,
                    x,
                    ground + 7,
                    z,
                    -1,
                    0,
                    armLength
            );

            buildMonumentArm(
                    data,
                    x,
                    ground + 7,
                    z,
                    1,
                    0,
                    armLength
            );

            buildMonumentArm(
                    data,
                    x,
                    ground + 9,
                    z,
                    0,
                    -1,
                    armLength
            );

            buildMonumentArm(
                    data,
                    x,
                    ground + 9,
                    z,
                    0,
                    1,
                    armLength
            );

            /*
             * Sculk heart.
             */
            setSafeBlock(
                    data,
                    x,
                    ground + height + 1,
                    z,
                    Material.SCULK_CATALYST
            );

            setSafeBlock(
                    data,
                    x,
                    ground + height + 2,
                    z,
                    Material.SCULK
            );
        }

        private void buildMonumentArm(
                ChunkData data,
                int x,
                int y,
                int z,
                int directionX,
                int directionZ,
                int length
        ) {

            for (int i = 1; i <= length; i++) {

                int px =
                        x + directionX * i;

                int pz =
                        z + directionZ * i;

                setSafeBlock(
                        data,
                        px,
                        y,
                        pz,
                        Material.POLISHED_DEEPSLATE
                );

                if (i % 2 == 0) {

                    setSafeBlock(
                            data,
                            px,
                            y + 1,
                            pz,
                            Material.SCULK
                    );
                }
            }
        }

        /*
         * =========================================================
         * ANCIENT STONE BRIDGE
         * =========================================================
         */

        private void generateAncientBridge(
                ChunkData data,
                int chunkX,
                int chunkZ,
                Random random
        ) {

            int x = 8;
            int z = 8;

            int worldX = chunkX * 16 + x;
            int worldZ = chunkZ * 16 + z;

            int ground = getFloorHeight(worldX, worldZ);

            if (ground < 35 || isLake(worldX, worldZ)) {
                return;
            }

            boolean eastWest =
                    random.nextBoolean();

            int bridgeLength =
                    10 + random.nextInt(6);

            int bridgeY =
                    ground + 8 + random.nextInt(8);

            for (int i = -bridgeLength; i <= bridgeLength; i++) {

                int px =
                        eastWest
                                ? x + i
                                : x;

                int pz =
                        eastWest
                                ? z
                                : z + i;

                /*
                 * Slight ancient arch.
                 */
                int arch =
                        Math.max(
                                0,
                                Math.abs(i) / 5
                        );

                int py =
                        bridgeY - arch;

                setSafeBlock(
                        data,
                        px,
                        py,
                        pz,
                        Material.POLISHED_DEEPSLATE
                );

                /*
                 * Bridge railing.
                 */
                if (Math.abs(i) > 1) {

                    if (eastWest) {

                        setSafeBlock(
                                data,
                                px,
                                py + 1,
                                pz - 1,
                                Material.DEEPSLATE_BRICKS
                        );

                        setSafeBlock(
                                data,
                                px,
                                py + 1,
                                pz + 1,
                                Material.DEEPSLATE_BRICKS
                        );

                    } else {

                        setSafeBlock(
                                data,
                                px - 1,
                                py + 1,
                                pz,
                                Material.DEEPSLATE_BRICKS
                        );

                        setSafeBlock(
                                data,
                                px + 1,
                                py + 1,
                                pz,
                                Material.DEEPSLATE_BRICKS
                        );
                    }
                }

                /*
                 * Hanging sculk accents.
                 */
                if (i % 4 == 0) {

                    setSafeBlock(
                            data,
                            px,
                            py - 1,
                            pz,
                            Material.SCULK_VEIN
                    );
                }
            }
        }

        /*
         * =========================================================
         * UNDERGROUND WATERFALL
         * =========================================================
         */

        private void generateWardenWaterfall(
                ChunkData data,
                int chunkX,
                int chunkZ,
                Random random
        ) {

            int x = 3 + random.nextInt(10);
            int z = 3 + random.nextInt(10);

            int worldX = chunkX * 16 + x;
            int worldZ = chunkZ * 16 + z;

            int floor = getFloorHeight(worldX, worldZ);
            int ceiling = getCeilingHeight(worldX, worldZ);

            /*
             * Need a tall cavern.
             */
            if (ceiling - floor < 65) {
                return;
            }

            if (isLake(worldX, worldZ)) {
                return;
            }

            int top =
                    Math.min(
                            ceiling - 8,
                            floor + 55 + random.nextInt(20)
                    );

            int bottom =
                    floor + 1;

            /*
             * Waterfall width 1-2 blocks.
             */
            int width =
                    1 + random.nextInt(2);

            for (int y = top; y >= bottom; y--) {

                for (int w = 0; w < width; w++) {

                    int px =
                            x + w;

                    /*
                     * Keep generation inside this chunk.
                     */
                    if (px < 0 || px >= 16) {
                        continue;
                    }

                    setSafeBlock(
                            data,
                            px,
                            y,
                            z,
                            Material.WATER
                    );
                }
            }

            /*
             * Stone lip at the source.
             */
            for (int w = -1; w <= width; w++) {

                setSafeBlock(
                        data,
                        x + w,
                        top + 1,
                        z,
                        Material.DEEPSLATE
                );
            }

            /*
             * Scattered sculk around the waterfall base.
             */
            for (int dx = -2; dx <= width + 1; dx++) {

                for (int dz = -2; dz <= 2; dz++) {

                    if (random.nextInt(100) < 45) {

                        setSafeBlock(
                                data,
                                x + dx,
                                floor + 1,
                                z + dz,
                                Material.SCULK
                        );
                    }
                }
            }
        }

        /*
         * =========================================================
         * STEP 4 - UNDERGROUND WARDEN CASTLE
         * =========================================================
         *
         * The castle is generated from world coordinates so the
         * structure continues naturally across chunk borders.
         * It is intentionally built low inside the cavern.
         */

        private static final int CASTLE_BASE_Y = 52;
        private static final int CASTLE_CENTER_X = 0;
        private static final int CASTLE_CENTER_Z = -10;

        private void generateWardenCastle(
                ChunkData data,
                int chunkX,
                int chunkZ
        ) {

            int minX = CASTLE_CENTER_X - 62;
            int maxX = CASTLE_CENTER_X + 62;
            int minZ = CASTLE_CENTER_Z - 58;
            int maxZ = CASTLE_CENTER_Z + 58;

            int chunkMinX = chunkX * 16;
            int chunkMinZ = chunkZ * 16;
            int chunkMaxX = chunkMinX + 15;
            int chunkMaxZ = chunkMinZ + 15;

            if (
                    chunkMaxX < minX ||
                            chunkMinX > maxX ||
                            chunkMaxZ < minZ ||
                            chunkMinZ > maxZ
            ) {
                return;
            }

            /*
             * Dark mountain/buttress mass around the fortress.
             */
            generateCastleMountainMass(
                    data,
                    chunkX,
                    chunkZ
            );

            /*
             * Large outer fortress wall.
             */
            buildCastleOuterWalls(
                    data,
                    chunkX,
                    chunkZ
            );

            /*
             * Four huge corner towers.
             */
            buildCastleTower(
                    data,
                    chunkX,
                    chunkZ,
                    -52,
                    -48,
                    13,
                    38
            );

            buildCastleTower(
                    data,
                    chunkX,
                    chunkZ,
                    52,
                    -48,
                    13,
                    38
            );

            buildCastleTower(
                    data,
                    chunkX,
                    chunkZ,
                    -52,
                    48,
                    13,
                    38
            );

            buildCastleTower(
                    data,
                    chunkX,
                    chunkZ,
                    52,
                    48,
                    13,
                    38
            );

            /*
             * Central keep / final boss building.
             */
            buildCastleKeep(
                    data,
                    chunkX,
                    chunkZ
            );

            /*
             * Gatehouse facing the arrival side.
             */
            buildCastleGatehouse(
                    data,
                    chunkX,
                    chunkZ
            );

            /*
             * Courtyard paths and sculk decorations.
             */
            buildCastleCourtyard(
                    data,
                    chunkX,
                    chunkZ
            );
        }

        private void generateCastleMountainMass(
                ChunkData data,
                int chunkX,
                int chunkZ
        ) {

            int worldMinX = chunkX * 16;
            int worldMinZ = chunkZ * 16;

            long seed =
                    ((long) chunkX * 92837111L) ^
                            ((long) chunkZ * 47231897L) ^
                            918273645L;

            Random random = new Random(seed);

            for (int localX = 0; localX < 16; localX++) {
                for (int localZ = 0; localZ < 16; localZ++) {

                    int x = worldMinX + localX;
                    int z = worldMinZ + localZ;

                    double dx = x - CASTLE_CENTER_X;
                    double dz = z - CASTLE_CENTER_Z;
                    double distance = Math.sqrt(dx * dx + dz * dz);

                    if (distance < 58 || distance > 78) {
                        continue;
                    }

                    double wave =
                            Math.sin(x * 0.11) * 4.0 +
                                    Math.cos(z * 0.09) * 4.0 +
                                    random.nextInt(4);

                    int height =
                            CASTLE_BASE_Y +
                                    16 +
                                    (int) Math.max(
                                            0,
                                            18 - (distance - 58)
                                    ) +
                                    (int) wave;

                    height = Math.min(height, 96);

                    for (int y = CASTLE_BASE_Y; y <= height; y++) {

                        Material material;

                        if (y >= height - 2) {
                            material = Material.POLISHED_DEEPSLATE;
                        } else if (y % 7 == 0) {
                            material = Material.TUFF;
                        } else {
                            material = Material.DEEPSLATE;
                        }

                        setWorldBlock(
                                data,
                                chunkX,
                                chunkZ,
                                x,
                                y,
                                z,
                                material
                        );
                    }
                }
            }
        }

        private void buildCastleOuterWalls(
                ChunkData data,
                int chunkX,
                int chunkZ
        ) {

            int minX = CASTLE_CENTER_X - 58;
            int maxX = CASTLE_CENTER_X + 58;
            int minZ = CASTLE_CENTER_Z - 54;
            int maxZ = CASTLE_CENTER_Z + 54;

            int topY = CASTLE_BASE_Y + 27;

            for (int x = minX; x <= maxX; x++) {
                for (int z = minZ; z <= maxZ; z++) {

                    boolean north = z >= maxZ - 5;
                    boolean south = z <= minZ + 5;
                    boolean west = x <= minX + 5;
                    boolean east = x >= maxX - 5;

                    if (!(north || south || west || east)) {
                        continue;
                    }

                    /* Main gate opening. */
                    if (
                            south &&
                                    Math.abs(x - CASTLE_CENTER_X) <= 9
                    ) {
                        continue;
                    }

                    for (int y = CASTLE_BASE_Y; y <= topY; y++) {

                        Material material =
                                (y == topY || y % 6 == 0)
                                        ? Material.POLISHED_DEEPSLATE
                                        : Material.DEEPSLATE_BRICKS;

                        setWorldBlock(
                                data,
                                chunkX,
                                chunkZ,
                                x,
                                y,
                                z,
                                material
                        );
                    }
                }
            }

            /* Battlements. */
            for (int x = minX; x <= maxX; x += 4) {
                buildMerlon(
                        data,
                        chunkX,
                        chunkZ,
                        x,
                        topY + 1,
                        minZ
                );
                buildMerlon(
                        data,
                        chunkX,
                        chunkZ,
                        x,
                        topY + 1,
                        maxZ
                );
            }

            for (int z = minZ; z <= maxZ; z += 4) {
                buildMerlon(
                        data,
                        chunkX,
                        chunkZ,
                        minX,
                        topY + 1,
                        z
                );
                buildMerlon(
                        data,
                        chunkX,
                        chunkZ,
                        maxX,
                        topY + 1,
                        z
                );
            }
        }

        private void buildMerlon(
                ChunkData data,
                int chunkX,
                int chunkZ,
                int x,
                int y,
                int z
        ) {
            for (int dy = 0; dy < 3; dy++) {
                for (int dx = -1; dx <= 1; dx++) {
                    for (int dz = -1; dz <= 1; dz++) {
                        setWorldBlock(
                                data,
                                chunkX,
                                chunkZ,
                                x + dx,
                                y + dy,
                                z + dz,
                                Material.POLISHED_DEEPSLATE
                        );
                    }
                }
            }
        }

        private void buildCastleTower(
                ChunkData data,
                int chunkX,
                int chunkZ,
                int relativeX,
                int relativeZ,
                int radius,
                int height
        ) {

            int cx = CASTLE_CENTER_X + relativeX;
            int cz = CASTLE_CENTER_Z + relativeZ;
            int topY = CASTLE_BASE_Y + height;

            for (int y = CASTLE_BASE_Y; y <= topY; y++) {

                int currentRadius = radius;

                if (y > topY - 8) {
                    currentRadius = radius + 2;
                }

                for (int dx = -currentRadius; dx <= currentRadius; dx++) {
                    for (int dz = -currentRadius; dz <= currentRadius; dz++) {

                        double distance =
                                Math.sqrt(dx * dx + dz * dz);

                        if (distance > currentRadius) {
                            continue;
                        }

                        /* Hollow interior. */
                        if (distance < currentRadius - 4 && y > CASTLE_BASE_Y + 5) {
                            continue;
                        }

                        Material material =
                                y == topY
                                        ? Material.POLISHED_DEEPSLATE
                                        : (y % 8 == 0
                                        ? Material.COBBLED_DEEPSLATE
                                        : Material.DEEPSLATE_BRICKS);

                        setWorldBlock(
                                data,
                                chunkX,
                                chunkZ,
                                cx + dx,
                                y,
                                cz + dz,
                                material
                        );
                    }
                }
            }

            /* Tower roof. */
            for (int layer = 0; layer < 7; layer++) {

                int y = topY + layer;
                int roofRadius =
                        Math.max(1, radius + 2 - layer);

                for (int dx = -roofRadius; dx <= roofRadius; dx++) {
                    for (int dz = -roofRadius; dz <= roofRadius; dz++) {

                        if (dx * dx + dz * dz > roofRadius * roofRadius) {
                            continue;
                        }

                        setWorldBlock(
                                data,
                                chunkX,
                                chunkZ,
                                cx + dx,
                                y,
                                cz + dz,
                                Material.POLISHED_DEEPSLATE
                        );
                    }
                }
            }

            /* Sculk beacon on tower top. */
            setWorldBlock(
                    data,
                    chunkX,
                    chunkZ,
                    cx,
                    topY + 7,
                    cz,
                    Material.SCULK_CATALYST
            );
        }

        private void buildCastleKeep(
                ChunkData data,
                int chunkX,
                int chunkZ
        ) {

            int cx = CASTLE_CENTER_X;
            int cz = CASTLE_CENTER_Z - 3;

            int half = 19;
            int minX = cx - half;
            int maxX = cx + half;
            int minZ = cz - half;
            int maxZ = cz + half;

            int base = CASTLE_BASE_Y + 2;
            int top = base + 38;

            /* Massive keep shell. */
            for (int x = minX; x <= maxX; x++) {
                for (int z = minZ; z <= maxZ; z++) {

                    boolean edge =
                            x <= minX + 4 ||
                                    x >= maxX - 4 ||
                                    z <= minZ + 4 ||
                                    z >= maxZ - 4;

                    if (!edge) {
                        continue;
                    }

                    for (int y = base; y <= top; y++) {

                        Material material =
                                y % 7 == 0
                                        ? Material.POLISHED_DEEPSLATE
                                        : Material.DEEPSLATE_BRICKS;

                        setWorldBlock(
                                data,
                                chunkX,
                                chunkZ,
                                x,
                                y,
                                z,
                                material
                        );
                    }
                }
            }

            /* Hollow boss chamber. */
            for (int x = minX + 5; x <= maxX - 5; x++) {
                for (int z = minZ + 5; z <= maxZ - 5; z++) {
                    for (int y = base + 1; y <= top - 3; y++) {

                        setWorldBlock(
                                data,
                                chunkX,
                                chunkZ,
                                x,
                                y,
                                z,
                                Material.AIR
                        );
                    }
                }
            }

            /* Boss chamber floor. */
            for (int x = minX + 5; x <= maxX - 5; x++) {
                for (int z = minZ + 5; z <= maxZ - 5; z++) {

                    Material floor =
                            ((x + z) & 3) == 0
                                    ? Material.SCULK
                                    : Material.DEEPSLATE_TILES;

                    setWorldBlock(
                            data,
                            chunkX,
                            chunkZ,
                            x,
                            base,
                            z,
                            floor
                    );
                }
            }

            /* Tall central boss dais. */
            for (int y = base + 1; y <= base + 5; y++) {
                int radius = 8 - (y - base - 1);

                for (int dx = -radius; dx <= radius; dx++) {
                    for (int dz = -radius; dz <= radius; dz++) {

                        if (dx * dx + dz * dz > radius * radius) {
                            continue;
                        }

                        setWorldBlock(
                                data,
                                chunkX,
                                chunkZ,
                                cx + dx,
                                y,
                                cz + dz,
                                Material.POLISHED_DEEPSLATE
                        );
                    }
                }
            }

            setWorldBlock(
                    data,
                    chunkX,
                    chunkZ,
                    cx,
                    base + 6,
                    cz,
                    Material.SCULK_CATALYST
            );

            /* Four interior pillars. */
            buildKeepPillar(data, chunkX, chunkZ, cx - 13, cz - 13, base, top - 4);
            buildKeepPillar(data, chunkX, chunkZ, cx + 13, cz - 13, base, top - 4);
            buildKeepPillar(data, chunkX, chunkZ, cx - 13, cz + 13, base, top - 4);
            buildKeepPillar(data, chunkX, chunkZ, cx + 13, cz + 13, base, top - 4);

            /* Crown roof. */
            for (int layer = 0; layer < 10; layer++) {

                int y = top + layer;
                int radius = 19 - layer * 2;

                if (radius < 1) {
                    radius = 1;
                }

                for (int dx = -radius; dx <= radius; dx++) {
                    for (int dz = -radius; dz <= radius; dz++) {

                        if (Math.abs(dx) + Math.abs(dz) > radius + 1) {
                            continue;
                        }

                        setWorldBlock(
                                data,
                                chunkX,
                                chunkZ,
                                cx + dx,
                                y,
                                cz + dz,
                                Material.POLISHED_DEEPSLATE
                        );
                    }
                }
            }
        }

        private void buildKeepPillar(
                ChunkData data,
                int chunkX,
                int chunkZ,
                int x,
                int z,
                int minY,
                int maxY
        ) {

            for (int y = minY; y <= maxY; y++) {
                for (int dx = -2; dx <= 2; dx++) {
                    for (int dz = -2; dz <= 2; dz++) {

                        setWorldBlock(
                                data,
                                chunkX,
                                chunkZ,
                                x + dx,
                                y,
                                z + dz,
                                (y % 5 == 0)
                                        ? Material.POLISHED_DEEPSLATE
                                        : Material.DEEPSLATE_BRICKS
                        );
                    }
                }
            }
        }

        private void buildCastleGatehouse(
                ChunkData data,
                int chunkX,
                int chunkZ
        ) {

            int centerX = CASTLE_CENTER_X;
            int frontZ = CASTLE_CENTER_Z + 54;
            int base = CASTLE_BASE_Y;
            int top = base + 34;

            for (int x = centerX - 16; x <= centerX + 16; x++) {
                for (int z = frontZ - 8; z <= frontZ + 8; z++) {

                    boolean shell =
                            x <= centerX - 11 ||
                                    x >= centerX + 11 ||
                                    z <= frontZ - 7 ||
                                    z >= frontZ + 7;

                    if (!shell) {
                        continue;
                    }

                    for (int y = base; y <= top; y++) {

                        /* Central gate opening. */
                        if (
                                Math.abs(x - centerX) <= 6 &&
                                        y <= base + 16
                        ) {
                            continue;
                        }

                        setWorldBlock(
                                data,
                                chunkX,
                                chunkZ,
                                x,
                                y,
                                z,
                                Material.DEEPSLATE_BRICKS
                        );
                    }
                }
            }

            /* Gate arch. */
            for (int x = centerX - 8; x <= centerX + 8; x++) {
                int distance = Math.abs(x - centerX);
                int archY = base + 16 +
                        Math.max(0, 7 - distance);

                for (int y = archY; y <= archY + 4; y++) {
                    setWorldBlock(
                            data,
                            chunkX,
                            chunkZ,
                            x,
                            y,
                            frontZ,
                            Material.POLISHED_DEEPSLATE
                    );
                }
            }

            /* Gatehouse roof and sculk crest. */
            for (int x = centerX - 17; x <= centerX + 17; x++) {
                for (int z = frontZ - 9; z <= frontZ + 9; z++) {
                    if ((Math.abs(x - centerX) + Math.abs(z - frontZ)) <= 20) {
                        setWorldBlock(
                                data,
                                chunkX,
                                chunkZ,
                                x,
                                top + 1,
                                z,
                                Material.POLISHED_DEEPSLATE
                        );
                    }
                }
            }

            setWorldBlock(
                    data,
                    chunkX,
                    chunkZ,
                    centerX,
                    top + 2,
                    frontZ,
                    Material.SCULK_CATALYST
            );
        }

        private void buildCastleCourtyard(
                ChunkData data,
                int chunkX,
                int chunkZ
        ) {

            int centerX = CASTLE_CENTER_X;
            int centerZ = CASTLE_CENTER_Z;
            int pathY = CASTLE_BASE_Y + 1;

            /* Main path from gate to keep. */
            for (int z = centerZ + 47; z >= centerZ - 3; z--) {
                for (int x = centerX - 5; x <= centerX + 5; x++) {
                    setWorldBlock(
                            data,
                            chunkX,
                            chunkZ,
                            x,
                            pathY,
                            z,
                            ((x + z) & 2) == 0
                                    ? Material.POLISHED_DEEPSLATE
                                    : Material.DEEPSLATE_TILES
                    );
                }
            }

            /* Cross path. */
            for (int x = centerX - 47; x <= centerX + 47; x++) {
                for (int z = centerZ - 3; z <= centerZ + 3; z++) {
                    setWorldBlock(
                            data,
                            chunkX,
                            chunkZ,
                            x,
                            pathY,
                            z,
                            Material.DEEPSLATE_TILES
                    );
                }
            }

            /* Sculk braziers / markers around the courtyard. */
            int[][] markers = {
                    {-30, 25}, {30, 25}, {-30, -25}, {30, -25},
                    {-20, 35}, {20, 35}, {-20, -35}, {20, -35}
            };

            for (int[] marker : markers) {

                int x = centerX + marker[0];
                int z = centerZ + marker[1];

                for (int y = pathY + 1; y <= pathY + 4; y++) {
                    setWorldBlock(
                            data,
                            chunkX,
                            chunkZ,
                            x,
                            y,
                            z,
                            Material.POLISHED_DEEPSLATE
                    );
                }

                setWorldBlock(
                        data,
                        chunkX,
                        chunkZ,
                        x,
                        pathY + 5,
                        z,
                        Material.SCULK_CATALYST
                );
            }
        }

        private void setWorldBlock(
                ChunkData data,
                int chunkX,
                int chunkZ,
                int worldX,
                int y,
                int worldZ,
                Material material
        ) {

            int targetChunkX = Math.floorDiv(worldX, 16);
            int targetChunkZ = Math.floorDiv(worldZ, 16);

            if (
                    targetChunkX != chunkX ||
                            targetChunkZ != chunkZ
            ) {
                return;
            }

            if (y < -64 || y > 160) {
                return;
            }

            int localX = Math.floorMod(worldX, 16);
            int localZ = Math.floorMod(worldZ, 16);

            data.setBlock(
                    localX,
                    y,
                    localZ,
                    material
            );
        }

        /*
         * =========================================================
         * CUSTOM WARDEN TREES
         * =========================================================
         */

        private void generateWardenTrees(
                ChunkData data,
                int chunkX,
                int chunkZ
        ) {

            long seed =
                    ((long) chunkX *
                            7843921L)
                            ^
                            ((long) chunkZ *
                                    3948271L)
                            ^
                            918273L;

            Random random =
                    new Random(seed);

            /*
             * 0-3 trees.
             */
            int treeCount =
                    random.nextInt(
                            4
                    );

            for (
                    int i = 0;
                    i < treeCount;
                    i++
            ) {

                int x =
                        2 +
                                random.nextInt(
                                        12
                                );

                int z =
                        2 +
                                random.nextInt(
                                        12
                                );

                int worldX =
                        chunkX * 16 + x;

                int worldZ =
                        chunkZ * 16 + z;

                int ground =
                        getFloorHeight(
                                worldX,
                                worldZ
                        );

                /*
                 * Avoid lakes.
                 */
                if (
                        isLake(
                                worldX,
                                worldZ
                        )
                ) {

                    continue;
                }

                /*
                 * Avoid very low terrain.
                 */
                if (
                        ground < 25
                ) {

                    continue;
                }

                int type =
                        random.nextInt(
                                3
                        );

                if (
                        type == 0
                ) {

                    buildTwistedWardenTree(
                            data,
                            x,
                            z,
                            ground,
                            random
                    );

                } else if (
                        type == 1
                ) {

                    buildAncientWardenTree(
                            data,
                            x,
                            z,
                            ground,
                            random
                    );

                } else {

                    buildCrownedWardenTree(
                            data,
                            x,
                            z,
                            ground,
                            random
                    );
                }
            }
        }

        /*
         * =========================================================
         * TWISTED WARDEN TREE
         * =========================================================
         */

        private void buildTwistedWardenTree(
                ChunkData data,
                int x,
                int z,
                int ground,
                Random random
        ) {

            int height =
                    7 +
                            random.nextInt(
                                    6
                            );

            /*
             * Twisted trunk.
             */
            for (
                    int y = 1;
                    y <= height;
                    y++
            ) {

                int offsetX;

                int offsetZ;

                if (
                        y < height / 3
                ) {

                    offsetX = 0;
                    offsetZ = 0;

                } else if (
                        y % 4 == 0
                ) {

                    offsetX = 1;
                    offsetZ = 0;

                } else {

                    offsetX = 0;
                    offsetZ = 1;
                }

                setSafeBlock(
                        data,
                        x + offsetX,
                        ground + y,
                        z + offsetZ,
                        Material.DARK_OAK_LOG
                );

                /*
                 * Sculk growth.
                 */
                if (
                        y % 3 == 0
                ) {

                    setSafeBlock(
                            data,
                            x + offsetX + 1,
                            ground + y,
                            z + offsetZ,
                            Material.SCULK_VEIN
                    );
                }
            }

            /*
             * Branches.
             */
            buildBranch(
                    data,
                    x,
                    ground + height - 2,
                    z,
                    -1,
                    0,
                    4
            );

            buildBranch(
                    data,
                    x,
                    ground + height - 3,
                    z,
                    1,
                    0,
                    4
            );

            buildBranch(
                    data,
                    x,
                    ground + height - 1,
                    z,
                    0,
                    1,
                    3
            );

            /*
             * Crown.
             */
            buildTreeCrown(
                    data,
                    x,
                    ground + height,
                    z,
                    3
            );
        }

        /*
         * =========================================================
         * ANCIENT WARDEN TREE
         * =========================================================
         */

        private void buildAncientWardenTree(
                ChunkData data,
                int x,
                int z,
                int ground,
                Random random
        ) {

            int height =
                    10 +
                            random.nextInt(
                                    8
                            );

            /*
             * Large trunk.
             */
            for (
                    int y = 1;
                    y <= height;
                    y++
            ) {

                setSafeBlock(
                        data,
                        x,
                        ground + y,
                        z,
                        Material.DARK_OAK_LOG
                );

                /*
                 * Secondary trunk.
                 */
                if (
                        y > 3 &&
                                y % 2 == 0
                ) {

                    setSafeBlock(
                            data,
                            x + 1,
                            ground + y,
                            z,
                            Material.DARK_OAK_LOG
                    );
                }

                /*
                 * Sculk growth.
                 */
                if (
                        y % 4 == 0
                ) {

                    setSafeBlock(
                            data,
                            x - 1,
                            ground + y,
                            z,
                            Material.SCULK
                    );
                }
            }

            /*
             * Four branches.
             */
            buildBranch(
                    data,
                    x,
                    ground + height - 2,
                    z,
                    -1,
                    0,
                    5
            );

            buildBranch(
                    data,
                    x,
                    ground + height - 3,
                    z,
                    1,
                    0,
                    5
            );

            buildBranch(
                    data,
                    x,
                    ground + height - 4,
                    z,
                    0,
                    -1,
                    5
            );

            buildBranch(
                    data,
                    x,
                    ground + height - 2,
                    z,
                    0,
                    1,
                    5
            );

            /*
             * Large crown.
             */
            buildTreeCrown(
                    data,
                    x,
                    ground + height,
                    z,
                    4
            );

            /*
             * Hanging roots.
             */
            for (
                    int side = -1;
                    side <= 1;
                    side += 2
            ) {

                for (
                        int i = 0;
                        i < 4;
                        i++
                ) {

                    int rootLength =
                            3 +
                                    random.nextInt(
                                            4
                                    );

                    for (
                            int y = 1;
                            y <= rootLength;
                            y++
                    ) {

                        setSafeBlock(
                                data,
                                x + side * (3 + i),
                                ground + height - y,
                                z,
                                Material.SCULK_VEIN
                        );
                    }
                }
            }
        }

        /*
         * =========================================================
         * CROWNED WARDEN TREE
         * =========================================================
         */

        private void buildCrownedWardenTree(
                ChunkData data,
                int x,
                int z,
                int ground,
                Random random
        ) {

            int height =
                    6 +
                            random.nextInt(
                                    5
                            );

            /*
             * Curved trunk.
             */
            for (
                    int y = 1;
                    y <= height;
                    y++
            ) {

                int offsetX =
                        y >= 4
                                ?
                                (
                                        y % 3 == 0
                                                ?
                                                1
                                                :
                                                0
                                )
                                :
                                0;

                setSafeBlock(
                        data,
                        x + offsetX,
                        ground + y,
                        z,
                        Material.DARK_OAK_LOG
                );
            }

            /*
             * Wide crown.
             */
            buildTreeCrown(
                    data,
                    x,
                    ground + height,
                    z,
                    4
            );

            /*
             * Hanging branches.
             */
            for (
                    int dx = -3;
                    dx <= 3;
                    dx++
            ) {

                if (
                        Math.abs(dx) < 2
                ) {

                    continue;
                }

                int length =
                        3 +
                                random.nextInt(
                                        4
                                );

                for (
                        int y = 1;
                        y <= length;
                        y++
                ) {

                    setSafeBlock(
                            data,
                            x + dx,
                            ground + height - y,
                            z,
                            Material.SCULK_VEIN
                    );
                }
            }
        }

        /*
         * =========================================================
         * TREE BRANCH
         * =========================================================
         */

        private void buildBranch(
                ChunkData data,
                int x,
                int y,
                int z,
                int directionX,
                int directionZ,
                int length
        ) {

            for (
                    int i = 1;
                    i <= length;
                    i++
            ) {

                int px =
                        x +
                                directionX * i;

                int pz =
                        z +
                                directionZ * i;

                int py =
                        y +
                                (i / 3);

                setSafeBlock(
                        data,
                        px,
                        py,
                        pz,
                        Material.DARK_OAK_LOG
                );

                /*
                 * Side branch.
                 */
                if (
                        i >= 2 &&
                                i % 2 == 0
                ) {

                    setSafeBlock(
                            data,
                            px + directionZ,
                            py,
                            pz + directionX,
                            Material.DARK_OAK_LOG
                    );
                }
            }
        }

        /*
         * =========================================================
         * TREE CROWN
         * =========================================================
         */

        private void buildTreeCrown(
                ChunkData data,
                int x,
                int y,
                int z,
                int radius
        ) {

            for (
                    int dx = -radius;
                    dx <= radius;
                    dx++
            ) {

                for (
                        int dz = -radius;
                        dz <= radius;
                        dz++
                ) {

                    double distance =
                            Math.sqrt(
                                    dx * dx +
                                            dz * dz
                            );

                    if (
                            distance >
                                    radius + 0.4
                    ) {

                        continue;
                    }

                    /*
                     * Main leaves.
                     */
                    setSafeBlock(
                            data,
                            x + dx,
                            y,
                            z + dz,
                            Material.DARK_OAK_LEAVES
                    );

                    /*
                     * Upper leaves.
                     */
                    if (
                            distance <=
                                    radius - 1
                    ) {

                        setSafeBlock(
                                data,
                                x + dx,
                                y + 1,
                                z + dz,
                                Material.DARK_OAK_LEAVES
                        );
                    }

                    /*
                     * Sculk growth.
                     */
                    if (
                            distance <= 2 &&
                                    (dx + dz) % 3 == 0
                    ) {

                        setSafeBlock(
                                data,
                                x + dx,
                                y + 2,
                                z + dz,
                                Material.SCULK
                        );
                    }
                }
            }

            /*
             * Center catalyst.
             */
            setSafeBlock(
                    data,
                    x,
                    y + 2,
                    z,
                    Material.SCULK_CATALYST
            );
        }

        /*
         * =========================================================
         * CUSTOM VEGETATION
         * =========================================================
         */

        private void generateWardenVegetation(
                ChunkData data,
                int chunkX,
                int chunkZ
        ) {

            long seed =
                    ((long) chunkX *
                            2837419L)
                            ^
                            ((long) chunkZ *
                                    8374921L)
                            ^
                            47291L;

            Random random =
                    new Random(seed);

            int amount =
                    5 +
                            random.nextInt(
                                    8
                            );

            for (
                    int i = 0;
                    i < amount;
                    i++
            ) {

                int x =
                        random.nextInt(
                                16
                        );

                int z =
                        random.nextInt(
                                16
                        );

                int worldX =
                        chunkX * 16 + x;

                int worldZ =
                        chunkZ * 16 + z;

                int ground =
                        getFloorHeight(
                                worldX,
                                worldZ
                        );

                /*
                 * Skip lake.
                 */
                if (
                        isLake(
                                worldX,
                                worldZ
                        )
                ) {

                    continue;
                }

                if (
                        ground < 20
                ) {

                    continue;
                }

                int type =
                        random.nextInt(
                                5
                        );

                if (
                        type == 0
                ) {

                    buildSculkFlower(
                            data,
                            x,
                            ground,
                            z
                    );

                } else if (
                        type == 1
                ) {

                    buildGlowPlant(
                            data,
                            x,
                            ground,
                            z
                    );

                } else if (
                        type == 2
                ) {

                    buildAncientBush(
                            data,
                            x,
                            ground,
                            z
                    );

                } else if (
                        type == 3
                ) {

                    buildHangingVegetation(
                            data,
                            x,
                            ground,
                            z,
                            random
                    );

                } else {

                    buildSculkCluster(
                            data,
                            x,
                            ground,
                            z
                    );
                }
            }
        }

        /*
         * =========================================================
         * SCULK FLOWER
         * =========================================================
         */

        private void buildSculkFlower(
                ChunkData data,
                int x,
                int ground,
                int z
        ) {

            setSafeBlock(
                    data,
                    x,
                    ground + 1,
                    z,
                    Material.SCULK
            );

            setSafeBlock(
                    data,
                    x,
                    ground + 2,
                    z,
                    Material.SCULK_CATALYST
            );

            /*
             * Four glowing arms.
             */
            setSafeBlock(
                    data,
                    x + 1,
                    ground + 2,
                    z,
                    Material.SCULK_VEIN
            );

            setSafeBlock(
                    data,
                    x - 1,
                    ground + 2,
                    z,
                    Material.SCULK_VEIN
            );

            setSafeBlock(
                    data,
                    x,
                    ground + 2,
                    z + 1,
                    Material.SCULK_VEIN
            );

            setSafeBlock(
                    data,
                    x,
                    ground + 2,
                    z - 1,
                    Material.SCULK_VEIN
            );
        }

        /*
         * =========================================================
         * GLOW PLANT
         * =========================================================
         */

        private void buildGlowPlant(
                ChunkData data,
                int x,
                int ground,
                int z
        ) {

            for (
                    int y = 1;
                    y <= 4;
                    y++
            ) {

                setSafeBlock(
                        data,
                        x,
                        ground + y,
                        z,
                        Material.GLOW_LICHEN
                );
            }

            setSafeBlock(
                    data,
                    x + 1,
                    ground + 3,
                    z,
                    Material.GLOW_LICHEN
            );

            setSafeBlock(
                    data,
                    x - 1,
                    ground + 4,
                    z,
                    Material.GLOW_LICHEN
            );
        }

        /*
         * =========================================================
         * ANCIENT BUSH
         * =========================================================
         */

        private void buildAncientBush(
                ChunkData data,
                int x,
                int ground,
                int z
        ) {

            for (
                    int dx = -2;
                    dx <= 2;
                    dx++
            ) {

                for (
                        int dz = -2;
                        dz <= 2;
                        dz++
                ) {

                    if (
                            Math.abs(dx)
                                    +
                                    Math.abs(dz)
                                    >
                                    3
                    ) {

                        continue;
                    }

                    setSafeBlock(
                            data,
                            x + dx,
                            ground + 1,
                            z + dz,
                            Material.MOSS_BLOCK
                    );
                }
            }

            /*
             * Glowing center.
             */
            setSafeBlock(
                    data,
                    x,
                    ground + 2,
                    z,
                    Material.SCULK
            );

            setSafeBlock(
                    data,
                    x,
                    ground + 3,
                    z,
                    Material.GLOW_LICHEN
            );
        }

        /*
         * =========================================================
         * HANGING VEGETATION
         * =========================================================
         */

        private void buildHangingVegetation(
                ChunkData data,
                int x,
                int ground,
                int z,
                Random random
        ) {

            int height =
                    3 +
                            random.nextInt(
                                    5
                            );

            /*
             * Ancient roots.
             */
            for (
                    int y = 1;
                    y <= height;
                    y++
            ) {

                setSafeBlock(
                        data,
                        x,
                        ground + y,
                        z,
                        Material.MANGROVE_ROOTS
                );
            }

            /*
             * Glowing tip.
             */
            setSafeBlock(
                    data,
                    x,
                    ground + height + 1,
                    z,
                    Material.GLOW_LICHEN
            );
        }

        /*
         * =========================================================
         * SCULK CLUSTER
         * =========================================================
         */

        private void buildSculkCluster(
                ChunkData data,
                int x,
                int ground,
                int z
        ) {

            setSafeBlock(
                    data,
                    x,
                    ground + 1,
                    z,
                    Material.SCULK
            );

            setSafeBlock(
                    data,
                    x + 1,
                    ground + 1,
                    z,
                    Material.SCULK
            );

            setSafeBlock(
                    data,
                    x - 1,
                    ground + 1,
                    z,
                    Material.SCULK
            );

            setSafeBlock(
                    data,
                    x,
                    ground + 1,
                    z + 1,
                    Material.SCULK
            );

            setSafeBlock(
                    data,
                    x,
                    ground + 1,
                    z - 1,
                    Material.SCULK
            );

            setSafeBlock(
                    data,
                    x,
                    ground + 2,
                    z,
                    Material.SCULK_CATALYST
            );
        }

        /*
         * =========================================================
         * SAFE BLOCK
         * =========================================================
         */

        private void setSafeBlock(
                ChunkData data,
                int x,
                int y,
                int z,
                Material material
        ) {

            /*
             * Keep generated block inside
             * current chunk.
             */
            if (
                    x < 0 ||
                            x >= 16 ||
                            z < 0 ||
                            z >= 16
            ) {

                return;
            }

            /*
             * World height protection.
             */
            if (
                    y < -64 ||
                            y > 160
            ) {

                return;
            }

            data.setBlock(
                    x,
                    y,
                    z,
                    material
            );
        }
    }
}