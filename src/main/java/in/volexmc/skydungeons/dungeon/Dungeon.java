package in.volexmc.skydungeons.dungeon;

public class Dungeon {

    private final String id;
    private final String name;
    private final String theme;
    private final int minPlayers;
    private final int maxPlayers;
    private final int order;

    public Dungeon(
            String id,
            String name,
            String theme,
            int minPlayers,
            int maxPlayers,
            int order
    ) {

        this.id = id;
        this.name = name;
        this.theme = theme;
        this.minPlayers = minPlayers;
        this.maxPlayers = maxPlayers;
        this.order = order;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getTheme() {
        return theme;
    }

    public int getMinPlayers() {
        return minPlayers;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public int getOrder() {
        return order;
    }
}