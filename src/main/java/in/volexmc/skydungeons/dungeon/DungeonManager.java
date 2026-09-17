package in.volexmc.skydungeons.dungeon;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class DungeonManager {

    private final Map<String, Dungeon> dungeons = new HashMap<>();

    public void registerDungeon(Dungeon dungeon) {
        dungeons.put(dungeon.getId().toLowerCase(), dungeon);
    }

    public Dungeon getDungeon(String id) {
        if (id == null) {
            return null;
        }

        return dungeons.get(id.toLowerCase());
    }

    public boolean hasDungeon(String id) {
        return getDungeon(id) != null;
    }

    public Collection<Dungeon> getDungeons() {
        return dungeons.values();
    }

    public int getDungeonCount() {
        return dungeons.size();
    }

    public void clearDungeons() {
        dungeons.clear();
    }
}