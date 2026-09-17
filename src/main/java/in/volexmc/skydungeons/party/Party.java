package in.volexmc.skydungeons.party;

import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Party {

    private UUID leader;
    private final List<UUID> members = new ArrayList<>();

    public Party(Player leader) {
        this.leader = leader.getUniqueId();
        this.members.add(leader.getUniqueId());
    }

    public UUID getLeader() {
        return leader;
    }

    public List<UUID> getMembers() {
        return members;
    }

    public boolean addMember(Player player) {

        if (members.contains(player.getUniqueId())) {
            return false;
        }

        if (members.size() >= 5) {
            return false;
        }

        members.add(player.getUniqueId());
        return true;
    }

    public boolean removeMember(Player player) {
        return members.remove(player.getUniqueId());
    }

    public boolean removeMember(UUID uuid) {
        return members.remove(uuid);
    }

    public boolean isMember(Player player) {
        return members.contains(player.getUniqueId());
    }

    public boolean isLeader(Player player) {
        return leader.equals(player.getUniqueId());
    }

    public boolean setLeader(Player player) {

        if (!members.contains(player.getUniqueId())) {
            return false;
        }

        leader = player.getUniqueId();
        return true;
    }

    public int getSize() {
        return members.size();
    }
}