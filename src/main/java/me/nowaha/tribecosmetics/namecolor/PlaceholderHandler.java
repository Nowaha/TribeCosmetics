package me.nowaha.tribecosmetics.namecolor;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.nowaha.tribecosmetics.TribeCosmetics;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlaceholderHandler extends PlaceholderExpansion {

    public TribeCosmetics plugin;

    public static Map<UUID, String> playersNameColors = new HashMap<>();
    public static Map<UUID, Integer> playersAnimationStages = new HashMap<>();

    public PlaceholderHandler(TribeCosmetics plugin) {
        this.plugin = plugin;
    }

    @Override
    public String onPlaceholderRequest(Player player, String identifier) {
        if (player == null){
            return "";
        }

        if (identifier.equals("name")){
            if (!playersNameColors.containsKey(player.getUniqueId())) {
                return player.getName();
            } else {
                return formatName(player.getName(), playersNameColors.get(player.getUniqueId()), player);
            }
        }

        return null;
    }

    public String formatName(String name, String nameType, Player player) {
        UUID uuid = player.getUniqueId();

        ConfigurationSection section = plugin.getConfig().getConfigurationSection("namecolors." + nameType);
        String patternType = section.getString("type", "single");
        if (patternType.equalsIgnoreCase("single")) {
            return "§" + section.getString("colors") + (player.hasPermission("deluxechat.format.staff") ? "§l" : "") + name;
        } else if (patternType.equalsIgnoreCase("pattern")) {
            return applyPattern(name, section.getString("colors").toCharArray(), player.hasPermission("deluxechat.format.staff"));
        } else if (patternType.equalsIgnoreCase("animatedsingle")) {
            Integer currentStage = playersAnimationStages.getOrDefault(uuid, 0);

            if (section.getConfigurationSection("colors").contains((currentStage + 1) + "")) {
                playersAnimationStages.put(uuid, currentStage + 1);
            } else {
                playersAnimationStages.put(uuid, 0);
            }

            return "§" + section.getString("colors." + currentStage) + (player.hasPermission("deluxechat.format.staff") ? "§l" : "") + name;
        } else if (patternType.equalsIgnoreCase("animatedpattern")) {
            Integer currentStage = playersAnimationStages.getOrDefault(uuid, 0);

            if (section.getConfigurationSection("colors").contains((currentStage + 1) + "")) {
                playersAnimationStages.put(uuid, currentStage + 1);
            } else {
                playersAnimationStages.put(uuid, 0);
            }
            return applyPattern(name, section.getString("colors." + currentStage).toCharArray(), player.hasPermission("deluxechat.format.staff"));
        }

        return name;
    }

    String applyPattern(String target, char[] pattern, boolean bold) {
        int colorIndex = 0;
        String result = "";

        for (char c : target.toCharArray()) {
            result += "§" + pattern[colorIndex] + (bold ? "§l" : "") + c;
            colorIndex++;
            if (colorIndex >= pattern.length) {
                colorIndex = 0;
            }
        }

        return result;
    }




    @Override
    public boolean persist(){
        return true;
    }

    @Override
    public boolean canRegister(){
        return true;
    }

    @Override
    public String getIdentifier() {
        return "cosmetics";
    }

    @Override
    public String getAuthor() {
        return "Nowaha";
    }

    @Override
    public String getVersion() {
        return "1.0";
    }
}
