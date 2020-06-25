package me.nowaha.tribecosmetics;

import me.nowaha.tribecosmetics.config.SaveDataFile;
import me.nowaha.tribecosmetics.glow.GlowHandler;
import me.nowaha.tribecosmetics.namecolor.PlaceholderHandler;
import me.nowaha.tribecosmetics.trail.TrailHandler;
import me.nowaha.tribecosmetics.trail.TrailRunnable;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Team;

import java.util.*;

public final class TribeCosmetics extends JavaPlugin implements Listener {

    TrailHandler trailHandler;
    PlaceholderHandler placeholderHandler;
    GlowHandler glowHandler;

    SaveDataFile saveDataFile;

    @Override
    public void onEnable() {
        // Plugin startup logic
        saveResource("config.yml", false);
        getServer().getPluginManager().registerEvents(this, this);
        trailHandler = new TrailHandler(this);

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null){
            placeholderHandler = new PlaceholderHandler(this);
            placeholderHandler.register();
        }

        saveDataFile = new SaveDataFile(this);

        YamlConfiguration config = (YamlConfiguration) saveDataFile.getConfig();
        ConfigurationSection activeTrails = config.getConfigurationSection("activetrails");
        ConfigurationSection activeChatColors = config.getConfigurationSection("activechatcolors");
        //ConfigurationSection activeGlows = config.getConfigurationSection("activeglows");

        try {
            for (String uuid : activeTrails.getKeys(false)) {
                TrailRunnable.playersActiveTrails.put(UUID.fromString(uuid), activeTrails.getString(uuid));
                activeTrails.set(uuid, null);
            }
        } catch (Exception ignored) {

        }

        try {
            for (String uuid : activeChatColors.getKeys(false)) {
                PlaceholderHandler.playersNameColors.put(UUID.fromString(uuid), activeChatColors.getString(uuid));
                activeChatColors.set(uuid, null);
            }
        } catch (Exception ignored) {

        }

        /*try {
            for (String uuid : activeGlows.getKeys(false)) {
                GlowHandler.playersGlows.put(UUID.fromString(uuid), activeGlows.getString(uuid));
                activeGlows.set(uuid, null);
            }
        } catch (Exception ignored) {

        }*/

        saveDataFile.saveConfig();

        /*rankChars.put("owner", 'a');
        rankChars.put("admin", 'b');
        rankChars.put("srmod", 'c');
        rankChars.put("mod", 'd');
        rankChars.put("helper", 'e');
        rankChars.put("ultimate", 'f');
        rankChars.put("legend", 'g');
        rankChars.put("titan", 'h');
        rankChars.put("elite", 'i');
        rankChars.put("hero", 'j');
        rankChars.put("default", 'z');

        setupTeams();
        glowHandler = new GlowHandler(this);
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, glowHandler, 10, 10);*/


    }

    /*@EventHandler(ignoreCancelled = true)
    public void onPlayerJoin(PlayerJoinEvent e) {
        e.getPlayer().setGlowing(false);
        Bukkit.getScheduler().scheduleSyncDelayedTask(this, () -> {
            Player player = e.getPlayer();
            Map<UUID, String> playersGlows = GlowHandler.playersGlows;
            if (playersGlows.containsKey(player.getUniqueId())) {
                player.setGlowing(true);
                Bukkit.getScoreboardManager().getMainScoreboard().getTeam(getRankPrefix(player) + "GLOWS_" + playersGlows.get(player.getUniqueId())).addEntry(player.getName());
            } else {
                player.setGlowing(false);
                Bukkit.getScoreboardManager().getMainScoreboard().getTeam(getRankPrefix(player) + "GLOWS_DEFAULT").addEntry(player.getName());
            }
        }, 10);
    }*/



    //LinkedList<String> teams = new LinkedList<>();

    //LinkedHashMap<String, Character> rankChars = new LinkedHashMap<>();

    /*private void setupTeams() {
        for (String team :
                teams) {
            try {
                Team teame = Bukkit.getScoreboardManager().getMainScoreboard().getTeam(team);
                teame.unregister();
            } catch (NullPointerException ignored) {}
        }
        teams.clear();
        for (String key :
                getConfig().getConfigurationSection("glows").getKeys(false)) {
            for (Character key2 :
                    rankChars.values()) {
                Team team = Bukkit.getScoreboardManager().getMainScoreboard().registerNewTeam(key2 + "GLOWS_" + key);
                team.setColor(ChatColor.valueOf(getConfig().getString("glows." + key + ".color").toUpperCase()));
                teams.add(team.getName());
            }
        }

        for (Character key2 :
                rankChars.values()) {
            Team team2 = Bukkit.getScoreboardManager().getMainScoreboard().registerNewTeam(key2 + "GLOWS_DEFAULT");
            team2.setColor(ChatColor.WHITE);
            teams.add(team2.getName());
        }
    }*/

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        YamlConfiguration config = (YamlConfiguration) saveDataFile.getConfig();
        for (UUID uuid : TrailRunnable.playersActiveTrails.keySet()) {
            config.set("activetrails." + uuid.toString(), TrailRunnable.playersActiveTrails.get(uuid));
        }

        for (UUID uuid : PlaceholderHandler.playersNameColors.keySet()) {
            config.set("activechatcolors." + uuid.toString(), PlaceholderHandler.playersNameColors.get(uuid));
        }

        /*for (UUID uuid : GlowHandler.playersGlows.keySet()) {
            config.set("activeglows." + uuid.toString(), GlowHandler.playersGlows.get(uuid));
        }

        for (String team :
                teams) {
            try {
                Bukkit.getScoreboardManager().getMainScoreboard().getTeam(team).unregister();
            } catch (Exception ignored) {}

        }*/

        saveDataFile.saveConfig();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("cosmetics.admin")) return true;

        if (command.getLabel().equalsIgnoreCase("trail")) {
            if (args.length > 0) {
                if (args[0].equalsIgnoreCase("list")) {
                    sender.sendMessage("§9§lCosmetics §cTrail list:");
                    sendCosmeticsList(sender, "trails", "§c");
                } else if (args[0].equalsIgnoreCase("remove")) {
                    OfflinePlayer player = Bukkit.getOfflinePlayer(args[1]);
                    if (args.length >= 2) {
                        if (TrailRunnable.playersActiveTrails.containsKey(player.getUniqueId())) {
                            TrailRunnable.playersActiveTrails.remove(player.getUniqueId());
                            sender.sendMessage("§aRemoved " + player.getName() + "'s trail!");
                        } else {
                            sender.sendMessage("§ePlayer " + player.getName() + " doesn't have a trail.");
                        }
                    } else {
                        sender.sendMessage("§cUsage: /trail remove <player>");
                    }
                } else if (args[0].equalsIgnoreCase("set")) {
                    if (args.length >= 3) {
                        Player player = Bukkit.getPlayer(args[1]);
                        if (player != null) {
                            if (getConfig().isSet("trails." + args[2])) {
                                TrailRunnable.playersActiveTrails.put(player.getUniqueId(), args[2]);
                                sender.sendMessage("§aSet " + player.getName() + "'s trail to " + args[2] + ".");
                            } else {
                                sender.sendMessage("§eTrail " + args[2] + " doesn't exist.");
                            }
                        } else {
                            sender.sendMessage("§eTrail " + args[1] + " is not online.");
                        }
                    } else {
                        sender.sendMessage("§cUsage: /trail set <player> <trail>");
                    }
                }
            } else {
                sender.sendMessage("§9§lCosmetics §cTrail commands:");
                sender.sendMessage(" §c/trail list §f- View the set-up trails.");
                sender.sendMessage(" §c/trail set <player> <trail> §f- Activate a trail for a player.");
                sender.sendMessage(" §c/trail remove <player> §f- Remove all trails from a player.");
                
                return true;
            }
        } else if (command.getLabel().equalsIgnoreCase("namecolor")) {
            if (args.length > 0) {
                if (args[0].equalsIgnoreCase("list")) {
                    sender.sendMessage("§9§lCosmetics §aName Color list:");
                    sendCosmeticsList(sender, "namecolors", "§a");
                } else if (args[0].equalsIgnoreCase("remove")) {
                    if (args.length >= 2) {
                        OfflinePlayer player = Bukkit.getOfflinePlayer(args[1]);
                        if (PlaceholderHandler.playersNameColors.containsKey(player.getUniqueId())) {
                            PlaceholderHandler.playersNameColors.remove(player.getUniqueId());
                            PlaceholderHandler.playersAnimationStages.remove(player.getUniqueId());
                            sender.sendMessage("§aRemoved " + player.getName() + "'s name color!");
                            if (player.isOnline()) {
                                Player playerOn = player.getPlayer();
                                playerOn.sendMessage("§eYour name color has been reset.");
                            }
                        } else {
                            sender.sendMessage("§ePlayer " + player.getName() + " doesn't have a name color.");
                        }
                    } else {
                        sender.sendMessage("§cUsage: /namecolor remove <player>");
                    }
                } else if (args[0].equalsIgnoreCase("set")) {
                    if (args.length >= 3) {
                        Player player = Bukkit.getPlayer(args[1]);
                        if (player != null) {
                            if (getConfig().isSet("namecolors." + args[2])) {
                                PlaceholderHandler.playersNameColors.put(player.getUniqueId(), args[2]);
                                PlaceholderHandler.playersAnimationStages.put(player.getUniqueId(), 0);
                                sender.sendMessage("§aSet " + player.getName() + "'s name color pattern to " + args[2] + ".");
                                player.sendMessage("§aYour name color has changed: §r" + placeholderHandler.formatName(player.getName(), args[2] , player));
                            } else {
                                sender.sendMessage("§eName color " + args[2] + " doesn't exist.");
                            }
                        } else {
                            sender.sendMessage("§ePlayer " + args[1] + " is not online.");
                        }
                    } else {
                        sender.sendMessage("§cUsage: /namecolor set <player> <namecolor>");
                    }
                }
            } else {
                sender.sendMessage("§9§lCosmetics §aName Color commands:");
                sender.sendMessage(" §a/namecolor list §f- View the set-up name color patterns.");
                sender.sendMessage(" §a/namecolor set <player> <pattern> §f- Set player's name color.");
                sender.sendMessage(" §a/namecolor remove <player> §f- Remove player's name color.");
                return true;
            }
        } /*else if (command.getLabel().equalsIgnoreCase("glow")) {
            if (args.length > 0) {
                if (args[0].equalsIgnoreCase("list")) {
                    sender.sendMessage("§9§lCosmetics §eGlow list:");
                    sendCosmeticsList(sender, "glows", "§e");
                } else if (args[0].equalsIgnoreCase("remove")) {
                    Player player = Bukkit.getPlayer(args[1]);
                    if (args.length >= 2) {
                        if (player != null && GlowHandler.playersGlows.containsKey(player.getUniqueId())) {
                            GlowHandler.playersGlows.remove(player.getUniqueId());
                            for (Team team :
                                    Bukkit.getScoreboardManager().getMainScoreboard().getTeams()) {
                                team.removeEntry(player.getName());
                            }

                            player.setGlowing(false);
                            Bukkit.getScoreboardManager().getMainScoreboard().getTeam(getRankPrefix(player) + "GLOWS_DEFAULT").addEntry(player.getName());
                            sender.sendMessage("§aRemoved " + player.getName() + "'s glow!");
                        } else {
                            sender.sendMessage("§ePlayer " + args[1] + " doesn't have a glow.");
                        }
                    } else {
                        sender.sendMessage("§cUsage: /glow remove <player>");
                    }
                } else if (args[0].equalsIgnoreCase("set")) {
                    if (args.length >= 3) {
                        Player player = Bukkit.getPlayer(args[1]);
                        if (player != null) {
                            if (getConfig().isSet("glows." + args[2])) {
                                GlowHandler.playersGlows.put(player.getUniqueId(), args[2]);
                                player.setGlowing(true);
                                Bukkit.getScoreboardManager().getMainScoreboard().getTeam(getRankPrefix(player) + "GLOWS_" + args[2]).addEntry(player.getName());
                                sender.sendMessage("§aSet " + player.getName() + "'s glow to " + args[2] + ".");
                            } else {
                                sender.sendMessage("§eGlow " + args[2] + " doesn't exist.");
                            }
                        } else {
                            sender.sendMessage("§eGlow " + args[1] + " is not online.");
                        }
                    } else {
                        sender.sendMessage("§cUsage: /glow set <player> <glow>");
                    }
                }
            } else {
                sender.sendMessage("§9§lCosmetics §eGlow commands:");
                sender.sendMessage(" §e/glow list §f- View the set-up glows.");
                sender.sendMessage(" §e/glow set <player> <glow> §f- Activate a glow for a player.");
                sender.sendMessage(" §e/glow remove <player> §f- Remove all glows from a player.");
                return true;
            }
        }*/ else if (command.getLabel().equalsIgnoreCase("cosmetics")) {
            if (args.length > 0) {
                if (args[0].equalsIgnoreCase("reload")) {
                    sender.sendMessage("§eSure, one second!");
                    reloadConfig();
                    //setupTeams();
                    sender.sendMessage("§aConfig reloaded!");
                }
            } else {
                sender.sendMessage("§9§lCosmetics commands:");
                sender.sendMessage(" §9/cosmetics reload §f- Reload the cosmetics config.");
                //sender.sendMessage(" §9/glow §f- View the glow commands.");
                sender.sendMessage(" §9/trail §f- View the trail commands.");
                sender.sendMessage(" §9/namecolor §f- View the name color commands.");
            }
        }

        return super.onCommand(sender, command, label, args);
    }

    public void sendCosmeticsList(CommandSender sender, String type, String color) {
        for (String key : getConfig().getConfigurationSection(type).getKeys(false)) {
            sender.sendMessage(color + " " + key);
        }
    }

    /*public Character getRankPrefix(Player player) {
        for (String key :
                rankChars.keySet()) {
            if (player.hasPermission("nte." + key)) {
                return rankChars.get(key);
            }
        }

        return 'z';
    }*/

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (command.getLabel().equalsIgnoreCase("trail") && args.length >= 3) {
            if (args[0].equalsIgnoreCase("set")) {
                List<String> result = new ArrayList<>();
                for (String string :
                        getConfig().getConfigurationSection("trails").getKeys(false)) {
                    if (string.contains(args[2])) {
                        result.add(string);
                    }
                }
                return result;
            }
        } else if (command.getLabel().equalsIgnoreCase("namecolor") && args.length >= 3) {
            if (args[0].equalsIgnoreCase("set")) {
                List<String> result = new ArrayList<>();
                for (String string : getConfig().getConfigurationSection("namecolors").getKeys(false)) {
                    if (string.contains(args[2])) {
                        result.add(string);
                    }
                }

                return result;
            }
        } /*else if (command.getLabel().equalsIgnoreCase("glow") && args.length >= 3) {
            if (args[0].equalsIgnoreCase("set")) {
                return new ArrayList<>(getConfig().getConfigurationSection("glows").getKeys(false));
            }
        }*/

        return super.onTabComplete(sender, command, alias, args);
    }
}
