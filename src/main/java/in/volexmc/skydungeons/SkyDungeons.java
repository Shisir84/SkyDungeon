package in.volexmc.skydungeons;

import in.volexmc.skydungeons.dungeon.Dungeon;
import in.volexmc.skydungeons.dungeon.DungeonManager;
import in.volexmc.skydungeons.party.Party;
import in.volexmc.skydungeons.party.PartyInviteManager;
import in.volexmc.skydungeons.party.PartyManager;

import in.volexmc.skydungeons.warden.WardenArmorRecipe;
import in.volexmc.skydungeons.warden.WardenBoss;
import in.volexmc.skydungeons.warden.WardenBossAbilityListener;
import in.volexmc.skydungeons.warden.WardenBossListener;
import in.volexmc.skydungeons.warden.WardenBossWorldManager;
import in.volexmc.skydungeons.warden.WardenBoots;
import in.volexmc.skydungeons.warden.WardenCastleBossBarListener;
import in.volexmc.skydungeons.warden.WardenChestplate;
import in.volexmc.skydungeons.warden.WardenDungeonListener;
import in.volexmc.skydungeons.warden.WardenDungeonManager;
import in.volexmc.skydungeons.warden.WardenHelmet;
import in.volexmc.skydungeons.warden.WardenIngot;
import in.volexmc.skydungeons.warden.WardenLeggings;
import in.volexmc.skydungeons.warden.WardenPearl;
import in.volexmc.skydungeons.warden.WardenPortalEnterListener;
import in.volexmc.skydungeons.warden.WardenPortalListener;
import in.volexmc.skydungeons.warden.WardenSword;
import in.volexmc.skydungeons.warden.WardenSwordListener;
import in.volexmc.skydungeons.warden.WardenSwordRecipe;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SkyDungeons extends JavaPlugin implements TabExecutor {

    private PartyManager partyManager;
    private PartyInviteManager partyInviteManager;
    private DungeonManager dungeonManager;

    private WardenDungeonManager wardenDungeonManager;
    private WardenPearl wardenPearl;
    private WardenIngot wardenIngot;

    private WardenHelmet wardenHelmet;
    private WardenChestplate wardenChestplate;
    private WardenLeggings wardenLeggings;
    private WardenBoots wardenBoots;

    private WardenArmorRecipe wardenArmorRecipe;

    private WardenSword wardenSword;
    private WardenSwordRecipe wardenSwordRecipe;
    private WardenSwordListener wardenSwordListener;

    private WardenBossWorldManager wardenBossWorldManager;
    private WardenBoss wardenBoss;
    private WardenBossAbilityListener wardenBossAbilityListener;

    @Override
    public void onEnable() {

        getLogger().info("=================================");
        getLogger().info("       SkyDungeons Starting");
        getLogger().info("=================================");

        /*
         * ===============================
         * PARTY SYSTEM
         * ===============================
         */

        partyManager =
                new PartyManager();

        partyInviteManager =
                new PartyInviteManager();

        /*
         * ===============================
         * DUNGEON SYSTEM
         * ===============================
         */

        dungeonManager =
                new DungeonManager();

        /*
         * ===============================
         * WARDEN SYSTEM
         * ===============================
         */

        wardenDungeonManager =
                new WardenDungeonManager(this);

        wardenPearl =
                new WardenPearl(this);

        wardenIngot =
                new WardenIngot(this);

        /*
         * ===============================
         * WARDEN ARMOR
         * ===============================
         */

        wardenHelmet =
                new WardenHelmet(this);

        wardenChestplate =
                new WardenChestplate(this);

        wardenLeggings =
                new WardenLeggings(this);

        wardenBoots =
                new WardenBoots(this);

        /*
         * ===============================
         * WARDEN ARMOR RECIPES
         * ===============================
         */

        wardenArmorRecipe =
                new WardenArmorRecipe(this);

        /*
         * ===============================
         * WARDEN SWORD
         * ===============================
         */

        wardenSword =
                new WardenSword(this);

        wardenSwordRecipe =
                new WardenSwordRecipe(this);

        wardenSwordListener =
                new WardenSwordListener(
                        this,
                        wardenSword
                );

        /*
         * ===============================
         * WARDEN BOSS SYSTEM
         * ===============================
         */

        wardenBossWorldManager =
                new WardenBossWorldManager(this);

        wardenBoss =
                new WardenBoss(this);

        /*
         * ===============================
         * WARDEN PEARL RECIPE
         * ===============================
         */

        wardenPearl.registerRecipe();

        /*
         * ===============================
         * WARDEN ARMOR RECIPES
         * ===============================
         */

        wardenArmorRecipe.registerRecipes();

        /*
         * ===============================
         * WARDEN SWORD RECIPE
         * ===============================
         */

        wardenSwordRecipe.registerRecipe();

        /*
         * ===============================
         * WARDEN BOSS WORLD
         * ===============================
         */

        wardenBossWorldManager.getWorld();

        /*
         * ===============================
         * WARDEN CASTLE BOSS BAR
         * ===============================
         */

        Bukkit.getPluginManager().registerEvents(
                new WardenCastleBossBarListener(this),
                this
        );

        /*
         * ===============================
         * EVENT LISTENERS
         * ===============================
         */

        Bukkit.getPluginManager().registerEvents(
                new WardenDungeonListener(this),
                this
        );

        Bukkit.getPluginManager().registerEvents(
                new WardenPortalListener(this),
                this
        );

        Bukkit.getPluginManager().registerEvents(
                new WardenPortalEnterListener(this),
                this
        );

        Bukkit.getPluginManager().registerEvents(
                new WardenBossListener(this),
                this
        );

        /*
         * ===============================
         * WARDEN SWORD LISTENER
         * ===============================
         */

        Bukkit.getPluginManager().registerEvents(
                wardenSwordListener,
                this
        );

        /*
         * IMPORTANT:
         * Store the ability listener in a field.
         * WardenBossListener uses this to
         * clear boss ability cooldowns.
         */

        wardenBossAbilityListener =
                new WardenBossAbilityListener(this);

        Bukkit.getPluginManager().registerEvents(
                wardenBossAbilityListener,
                this
        );

        /*
         * ===============================
         * DEFAULT DUNGEONS
         * ===============================
         */

        registerDefaultDungeons();

        /*
         * ===============================
         * COMMAND
         * ===============================
         */

        if (getCommand("skydungeons") != null) {

            getCommand("skydungeons")
                    .setExecutor(this);

            getCommand("skydungeons")
                    .setTabCompleter(this);
        }

        /*
         * ===============================
         * STARTUP LOGS
         * ===============================
         */

        getLogger().info(
                "Loaded "
                        + dungeonManager.getDungeonCount()
                        + " dungeon definitions."
        );

        getLogger().info(
                "Loaded "
                        + wardenDungeonManager.getDungeonCount()
                        + " saved Warden Dungeon(s)."
        );

        getLogger().info(
                "Private dungeon instances: DISABLED"
        );

        getLogger().info(
                "Stage system: DISABLED"
        );

        getLogger().info(
                "Template system: DISABLED"
        );

        getLogger().info(
                "Shared-world dungeon system: ENABLED"
        );

        getLogger().info(
                "Warden dungeon manager: ENABLED"
        );

        getLogger().info(
                "Warden Ancient City detection: ENABLED"
        );

        getLogger().info(
                "Warden dungeon persistence: ENABLED"
        );

        getLogger().info(
                "Warden Pearl system: ENABLED"
        );

        getLogger().info(
                "Warden Pearl recipe: ENABLED"
        );

        getLogger().info(
                "Warden Ingot system: ENABLED"
        );

        getLogger().info(
                "Warden Armor system: ENABLED"
        );

        getLogger().info(
                "Warden Helmet: ENABLED"
        );

        getLogger().info(
                "Warden Chestplate: ENABLED"
        );

        getLogger().info(
                "Warden Leggings: ENABLED"
        );

        getLogger().info(
                "Warden Boots: ENABLED"
        );

        getLogger().info(
                "Warden Armor recipes: ENABLED"
        );

        getLogger().info(
                "Warden Sword: ENABLED"
        );

        getLogger().info(
                "Warden Sword recipe: ENABLED"
        );

        getLogger().info(
                "Warden Sword ability: ENABLED"
        );

        getLogger().info(
                "Warden Portal listener: ENABLED"
        );

        getLogger().info(
                "Warden Portal teleport: ENABLED"
        );

        getLogger().info(
                "Warden Boss Realm: ENABLED"
        );

        getLogger().info(
                "Warden Boss system: ENABLED"
        );

        getLogger().info(
                "Warden Boss Listener: ENABLED"
        );

        getLogger().info(
                "Warden Boss Abilities: ENABLED"
        );

        getLogger().info(
                "Warden Castle BossBar: ENABLED"
        );

        getLogger().info(
                "SkyDungeons enabled successfully!"
        );
    }

    @Override
    public void onDisable() {

        getLogger().info(
                "SkyDungeons shutting down..."
        );

        if (wardenBoss != null) {

            wardenBoss.removeBoss();
        }

        if (wardenBossAbilityListener != null) {

            wardenBossAbilityListener.clearCooldowns();
        }

        if (wardenSwordListener != null) {

            wardenSwordListener.clearCooldowns();
        }

        if (wardenDungeonManager != null) {

            wardenDungeonManager.clear();
        }

        getLogger().info(
                "SkyDungeons disabled."
        );
    }

    /*
     * ===============================
     * DEFAULT DUNGEONS
     * ===============================
     */

    private void registerDefaultDungeons() {

        Dungeon wardenDungeon =
                new Dungeon(
                        "warden_city",
                        "Warden Dungeon",
                        "WARDEN",
                        1,
                        5,
                        1
                );

        dungeonManager.registerDungeon(
                wardenDungeon
        );

        Dungeon skyDungeon =
                new Dungeon(
                        "sky_dungeon",
                        "Sky Dungeon",
                        "SKY",
                        1,
                        5,
                        2
                );

        dungeonManager.registerDungeon(
                skyDungeon
        );

        Dungeon endDungeon =
                new Dungeon(
                        "end_dungeon",
                        "End Dungeon",
                        "END",
                        1,
                        5,
                        3
                );

        dungeonManager.registerDungeon(
                endDungeon
        );
    }

    /*
     * ===============================
     * MAIN COMMAND
     * ===============================
     */

    @Override
    public boolean onCommand(
            CommandSender sender,
            Command command,
            String label,
            String[] args
    ) {

        if (!command.getName()
                .equalsIgnoreCase("skydungeons")) {

            return false;
        }

        if (args.length == 0) {

            sendHelp(sender);

            return true;
        }

        String subCommand =
                args[0].toLowerCase();

        /*
         * HELP
         */

        if (subCommand.equals("help")) {

            sendHelp(sender);

            return true;
        }

        /*
         * LIST
         */

        if (subCommand.equals("list")) {

            sendDungeonList(sender);

            return true;
        }

        /*
         * PARTY
         */

        if (subCommand.equals("party")) {

            handlePartyCommand(
                    sender,
                    args
            );

            return true;
        }

        /*
         * DIRECT ENTRY DISABLED
         */

        if (subCommand.equals("enter")) {

            sender.sendMessage(
                    ChatColor.RED
                            + "Direct dungeon entry is disabled."
            );

            sender.sendMessage(
                    ChatColor.GRAY
                            + "Explore the world and discover dungeons manually."
            );

            return true;
        }

        /*
         * OLD STAGE SYSTEM
         */

        if (subCommand.equals("stages")) {

            sender.sendMessage(
                    ChatColor.RED
                            + "The stage system has been removed."
            );

            return true;
        }

        /*
         * OLD TEMPLATE SYSTEM
         */

        if (subCommand.equals("template")) {

            sender.sendMessage(
                    ChatColor.RED
                            + "The template system has been removed."
            );

            sender.sendMessage(
                    ChatColor.GRAY
                            + "SkyDungeons uses existing Minecraft worlds."
            );

            return true;
        }

        /*
         * UNKNOWN COMMAND
         */

        sender.sendMessage(
                ChatColor.RED
                        + "Unknown SkyDungeons command."
        );

        sendHelp(sender);

        return true;
    }

    /*
     * ===============================
     * HELP
     * ===============================
     */

    private void sendHelp(
            CommandSender sender
    ) {

        sender.sendMessage("");

        sender.sendMessage(
                ChatColor.DARK_AQUA
                        + "========== SkyDungeons =========="
        );

        sender.sendMessage("");

        sender.sendMessage(
                ChatColor.AQUA
                        + "/skydungeons list"
                        + ChatColor.GRAY
                        + " - View dungeon types"
        );

        sender.sendMessage("");

        sender.sendMessage(
                ChatColor.LIGHT_PURPLE
                        + "Party Commands"
        );

        sender.sendMessage(
                ChatColor.AQUA
                        + "/skydungeons party create"
        );

        sender.sendMessage(
                ChatColor.AQUA
                        + "/skydungeons party invite <player>"
        );

        sender.sendMessage(
                ChatColor.AQUA
                        + "/skydungeons party accept"
        );

        sender.sendMessage(
                ChatColor.AQUA
                        + "/skydungeons party leave"
        );

        sender.sendMessage(
                ChatColor.AQUA
                        + "/skydungeons party kick <player>"
        );

        sender.sendMessage(
                ChatColor.AQUA
                        + "/skydungeons party promote <player>"
        );

        sender.sendMessage("");

        sender.sendMessage(
                ChatColor.GRAY
                        + "Explore the world to discover dungeons."
        );

        sender.sendMessage("");

        sender.sendMessage(
                ChatColor.DARK_AQUA
                        + "================================"
        );

        sender.sendMessage("");
    }

    /*
     * ===============================
     * DUNGEON LIST
     * ===============================
     */

    private void sendDungeonList(
            CommandSender sender
    ) {

        sender.sendMessage("");

        sender.sendMessage(
                ChatColor.DARK_AQUA
                        + "========== Dungeon Types =========="
        );

        sender.sendMessage("");

        for (Dungeon dungeon :
                dungeonManager.getDungeons()) {

            ChatColor themeColor =
                    ChatColor.WHITE;

            if (dungeon.getTheme()
                    .equalsIgnoreCase("WARDEN")) {

                themeColor =
                        ChatColor.DARK_PURPLE;

            } else if (dungeon.getTheme()
                    .equalsIgnoreCase("SKY")) {

                themeColor =
                        ChatColor.AQUA;

            } else if (dungeon.getTheme()
                    .equalsIgnoreCase("END")) {

                themeColor =
                        ChatColor.LIGHT_PURPLE;
            }

            sender.sendMessage(
                    themeColor
                            + "◆ "
                            + ChatColor.WHITE
                            + dungeon.getName()
            );

            sender.sendMessage(
                    ChatColor.GRAY
                            + "  Theme: "
                            + themeColor
                            + dungeon.getTheme()
            );

            sender.sendMessage(
                    ChatColor.GRAY
                            + "  Discover by exploring the world."
            );

            sender.sendMessage("");
        }

        sender.sendMessage(
                ChatColor.GRAY
                        + "Total dungeon types: "
                        + ChatColor.WHITE
                        + dungeonManager.getDungeonCount()
        );

        sender.sendMessage("");
    }

    /*
     * ===============================
     * PARTY COMMANDS
     * ===============================
     */

    private void handlePartyCommand(
            CommandSender sender,
            String[] args
    ) {

        if (!(sender instanceof Player player)) {

            sender.sendMessage(
                    ChatColor.RED
                            + "Only players can use party commands."
            );

            return;
        }

        if (args.length < 2) {

            sendPartyHelp(player);

            return;
        }

        String action =
                args[1].toLowerCase();

        /*
         * CREATE
         */

        if (action.equals("create")) {

            Party party =
                    partyManager.createParty(player);

            if (party == null) {

                player.sendMessage(
                        ChatColor.RED
                                + "You are already in a party."
                );

                return;
            }

            player.sendMessage(
                    ChatColor.GREEN
                            + "Party created!"
            );

            player.sendMessage(
                    ChatColor.GRAY
                            + "Invite players using "
                            + ChatColor.WHITE
                            + "/skydungeons party invite <player>"
            );

            return;
        }

        /*
         * INVITE
         */

        if (action.equals("invite")) {

            if (args.length < 3) {

                player.sendMessage(
                        ChatColor.RED
                                + "Usage: /skydungeons party invite <player>"
                );

                return;
            }

            Party party =
                    partyManager.getParty(player);

            if (party == null) {

                player.sendMessage(
                        ChatColor.RED
                                + "You are not in a party."
                );

                return;
            }

            if (!party.isLeader(player)) {

                player.sendMessage(
                        ChatColor.RED
                                + "Only the party leader can invite."
                );

                return;
            }

            if (party.getSize() >= 5) {

                player.sendMessage(
                        ChatColor.RED
                                + "Party is full. Maximum 5 players."
                );

                return;
            }

            Player target =
                    Bukkit.getPlayer(args[2]);

            if (target == null) {

                player.sendMessage(
                        ChatColor.RED
                                + "Player is not online."
                );

                return;
            }

            if (target.equals(player)) {

                player.sendMessage(
                        ChatColor.RED
                                + "You cannot invite yourself."
                );

                return;
            }

            if (partyManager.getParty(target) != null) {

                player.sendMessage(
                        ChatColor.RED
                                + "That player is already in a party."
                );

                return;
            }

            partyInviteManager.invite(
                    target,
                    party
            );

            player.sendMessage(
                    ChatColor.GREEN
                            + "Party invitation sent to "
                            + target.getName()
            );

            target.sendMessage(
                    ChatColor.YELLOW
                            + player.getName()
                            + " invited you to a party."
            );

            target.sendMessage(
                    ChatColor.GREEN
                            + "Use "
                            + ChatColor.WHITE
                            + "/skydungeons party accept"
                            + ChatColor.GREEN
                            + " to join."
            );

            return;
        }

        /*
         * ACCEPT
         */

        if (action.equals("accept")) {

            Party party =
                    partyInviteManager.getInvitation(player);

            if (party == null) {

                player.sendMessage(
                        ChatColor.RED
                                + "You do not have a party invitation."
                );

                return;
            }

            if (party.getSize() >= 5) {

                player.sendMessage(
                        ChatColor.RED
                                + "That party is already full."
                );

                partyInviteManager.removeInvitation(
                        player
                );

                return;
            }

            boolean added =
                    partyManager.addPlayer(
                            party,
                            player
                    );

            if (!added) {

                player.sendMessage(
                        ChatColor.RED
                                + "Could not join the party."
                );

                return;
            }

            partyInviteManager.removeInvitation(
                    player
            );

            player.sendMessage(
                    ChatColor.GREEN
                            + "You joined the party!"
            );

            for (UUID uuid :
                    party.getMembers()) {

                Player member =
                        Bukkit.getPlayer(uuid);

                if (member == null) {
                    continue;
                }

                if (member.equals(player)) {
                    continue;
                }

                member.sendMessage(
                        ChatColor.GREEN
                                + player.getName()
                                + " joined the party."
                );
            }

            return;
        }

        /*
         * LEAVE
         */

        if (action.equals("leave")) {

            Party party =
                    partyManager.getParty(player);

            if (party == null) {

                player.sendMessage(
                        ChatColor.RED
                                + "You are not in a party."
                );

                return;
            }

            boolean wasLeader =
                    party.isLeader(player);

            partyManager.leaveParty(player);

            if (wasLeader) {

                player.sendMessage(
                        ChatColor.YELLOW
                                + "You left the party. The party was disbanded."
                );

            } else {

                player.sendMessage(
                        ChatColor.YELLOW
                                + "You left the party."
                );
            }

            return;
        }

        /*
         * KICK
         */

        if (action.equals("kick")) {

            if (args.length < 3) {

                player.sendMessage(
                        ChatColor.RED
                                + "Usage: /skydungeons party kick <player>"
                );

                return;
            }

            Party party =
                    partyManager.getParty(player);

            if (party == null) {

                player.sendMessage(
                        ChatColor.RED
                                + "You are not in a party."
                );

                return;
            }

            if (!party.isLeader(player)) {

                player.sendMessage(
                        ChatColor.RED
                                + "Only the party leader can kick."
                );

                return;
            }

            Player target =
                    Bukkit.getPlayer(args[2]);

            if (target == null) {

                player.sendMessage(
                        ChatColor.RED
                                + "Player is not online."
                );

                return;
            }

            if (!party.isMember(target)) {

                player.sendMessage(
                        ChatColor.RED
                                + "That player is not in your party."
                );

                return;
            }

            if (party.isLeader(target)) {

                player.sendMessage(
                        ChatColor.RED
                                + "You cannot kick yourself."
                );

                return;
            }

            boolean removed =
                    partyManager.kickPlayer(
                            party,
                            target
                    );

            if (!removed) {

                player.sendMessage(
                        ChatColor.RED
                                + "Could not kick that player."
                );

                return;
            }

            player.sendMessage(
                    ChatColor.GREEN
                            + "You kicked "
                            + target.getName()
                            + "."
            );

            target.sendMessage(
                    ChatColor.RED
                            + "You were kicked from the party."
            );

            return;
        }

        /*
         * PROMOTE
         */

        if (action.equals("promote")) {

            if (args.length < 3) {

                player.sendMessage(
                        ChatColor.RED
                                + "Usage: /skydungeons party promote <player>"
                );

                return;
            }

            Party party =
                    partyManager.getParty(player);

            if (party == null) {

                player.sendMessage(
                        ChatColor.RED
                                + "You are not in a party."
                );

                return;
            }

            if (!party.isLeader(player)) {

                player.sendMessage(
                        ChatColor.RED
                                + "Only the party leader can promote."
                );

                return;
            }

            Player target =
                    Bukkit.getPlayer(args[2]);

            if (target == null) {

                player.sendMessage(
                        ChatColor.RED
                                + "Player is not online."
                );

                return;
            }

            if (!party.isMember(target)) {

                player.sendMessage(
                        ChatColor.RED
                                + "That player is not in your party."
                );

                return;
            }

            if (target.equals(player)) {

                player.sendMessage(
                        ChatColor.RED
                                + "You are already the leader."
                );

                return;
            }

            partyManager.promotePlayer(
                    party,
                    target
            );

            player.sendMessage(
                    ChatColor.GREEN
                            + target.getName()
                            + " is now the party leader."
            );

            target.sendMessage(
                    ChatColor.GREEN
                            + "You are now the party leader!"
            );

            return;
        }

        sendPartyHelp(player);
    }

    /*
     * ===============================
     * PARTY HELP
     * ===============================
     */

    private void sendPartyHelp(
            Player player
    ) {

        player.sendMessage("");

        player.sendMessage(
                ChatColor.DARK_AQUA
                        + "========== Party =========="
        );

        player.sendMessage("");

        player.sendMessage(
                ChatColor.AQUA
                        + "/skydungeons party create"
        );

        player.sendMessage(
                ChatColor.AQUA
                        + "/skydungeons party invite <player>"
        );

        player.sendMessage(
                ChatColor.AQUA
                        + "/skydungeons party accept"
        );

        player.sendMessage(
                ChatColor.AQUA
                        + "/skydungeons party leave"
        );

        player.sendMessage(
                ChatColor.AQUA
                        + "/skydungeons party kick <player>"
        );

        player.sendMessage(
                ChatColor.AQUA
                        + "/skydungeons party promote <player>"
        );

        player.sendMessage("");
    }

    /*
     * ===============================
     * TAB COMPLETE
     * ===============================
     */

    @Override
    public List<String> onTabComplete(
            CommandSender sender,
            Command command,
            String alias,
            String[] args
    ) {

        List<String> suggestions =
                new ArrayList<>();

        if (!command.getName()
                .equalsIgnoreCase("skydungeons")) {

            return suggestions;
        }

        if (args.length == 1) {

            suggestions.add("help");
            suggestions.add("list");
            suggestions.add("party");

            return filter(
                    suggestions,
                    args[0]
            );
        }

        if (args.length == 2
                && args[0]
                .equalsIgnoreCase("party")) {

            suggestions.add("create");
            suggestions.add("invite");
            suggestions.add("accept");
            suggestions.add("leave");
            suggestions.add("kick");
            suggestions.add("promote");

            return filter(
                    suggestions,
                    args[1]
            );
        }

        if (args.length == 3
                && args[0]
                .equalsIgnoreCase("party")
                && (
                args[1].equalsIgnoreCase("invite")
                        || args[1].equalsIgnoreCase("kick")
                        || args[1].equalsIgnoreCase("promote")
        )) {

            for (Player online :
                    Bukkit.getOnlinePlayers()) {

                suggestions.add(
                        online.getName()
                );
            }

            return filter(
                    suggestions,
                    args[2]
            );
        }

        return suggestions;
    }

    /*
     * ===============================
     * TAB FILTER
     * ===============================
     */

    private List<String> filter(
            List<String> values,
            String input
    ) {

        List<String> result =
                new ArrayList<>();

        for (String value :
                values) {

            if (value.toLowerCase()
                    .startsWith(
                            input.toLowerCase()
                    )) {

                result.add(value);
            }
        }

        return result;
    }

    /*
     * ===============================
     * GETTERS
     * ===============================
     */

    public PartyManager getPartyManager() {

        return partyManager;
    }

    public PartyInviteManager getPartyInviteManager() {

        return partyInviteManager;
    }

    public DungeonManager getDungeonManager() {

        return dungeonManager;
    }

    public WardenDungeonManager getWardenDungeonManager() {

        return wardenDungeonManager;
    }

    public WardenPearl getWardenPearl() {

        return wardenPearl;
    }

    public WardenIngot getWardenIngot() {

        return wardenIngot;
    }

    public WardenHelmet getWardenHelmet() {

        return wardenHelmet;
    }

    public WardenChestplate getWardenChestplate() {

        return wardenChestplate;
    }

    public WardenLeggings getWardenLeggings() {

        return wardenLeggings;
    }

    public WardenBoots getWardenBoots() {

        return wardenBoots;
    }

    public WardenArmorRecipe getWardenArmorRecipe() {

        return wardenArmorRecipe;
    }

    public WardenSword getWardenSword() {

        return wardenSword;
    }

    public WardenSwordRecipe getWardenSwordRecipe() {

        return wardenSwordRecipe;
    }

    public WardenSwordListener getWardenSwordListener() {

        return wardenSwordListener;
    }

    public WardenBossWorldManager getWardenBossWorldManager() {

        return wardenBossWorldManager;
    }

    public WardenBoss getWardenBoss() {

        return wardenBoss;
    }

    public WardenBossAbilityListener getWardenBossAbilityListener() {

        return wardenBossAbilityListener;
    }
}