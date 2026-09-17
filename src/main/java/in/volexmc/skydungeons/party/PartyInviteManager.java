package in.volexmc.skydungeons.party;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PartyInviteManager {

    private final Map<UUID, Party> invitations = new HashMap<>();

    public void invite(Player player, Party party) {
        invitations.put(player.getUniqueId(), party);
    }

    public Party getInvitation(Player player) {
        return invitations.get(player.getUniqueId());
    }

    public void removeInvitation(Player player) {
        invitations.remove(player.getUniqueId());
    }

    public boolean hasInvitation(Player player) {
        return invitations.containsKey(player.getUniqueId());
    }
}