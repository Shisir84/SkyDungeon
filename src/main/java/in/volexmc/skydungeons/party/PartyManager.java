package in.volexmc.skydungeons.party;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PartyManager {

    private final Map<UUID, Party> parties = new HashMap<>();

    public Party createParty(Player leader) {

        if (getParty(leader) != null) {
            return null;
        }

        Party party = new Party(leader);

        for (UUID member : party.getMembers()) {
            parties.put(member, party);
        }

        return party;
    }

    public Party getParty(Player player) {
        return parties.get(player.getUniqueId());
    }

    public Party getParty(UUID uuid) {
        return parties.get(uuid);
    }

    public boolean addPlayer(Party party, Player player) {

        if (getParty(player) != null) {
            return false;
        }

        if (!party.addMember(player)) {
            return false;
        }

        parties.put(player.getUniqueId(), party);

        return true;
    }

    public boolean kickPlayer(Party party, Player player) {

        if (!party.isMember(player)) {
            return false;
        }

        if (party.isLeader(player)) {
            return false;
        }

        party.removeMember(player);
        parties.remove(player.getUniqueId());

        return true;
    }

    public boolean promotePlayer(Party party, Player player) {

        if (!party.isMember(player)) {
            return false;
        }

        return party.setLeader(player);
    }

    public void removeParty(Party party) {

        for (UUID member : party.getMembers()) {
            parties.remove(member);
        }

        party.getMembers().clear();
    }

    public void leaveParty(Player player) {

        Party party = getParty(player);

        if (party == null) {
            return;
        }

        if (party.isLeader(player)) {
            removeParty(party);
            return;
        }

        party.removeMember(player);
        parties.remove(player.getUniqueId());
    }
}