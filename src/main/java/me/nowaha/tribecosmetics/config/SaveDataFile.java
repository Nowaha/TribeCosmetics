package me.nowaha.tribecosmetics.config;

import me.nowaha.tribecosmetics.TribeCosmetics;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class SaveDataFile {
    TribeCosmetics main;

    private File customConfigFile;
    private FileConfiguration customConfig;

    public SaveDataFile(TribeCosmetics main) {
        this.main = main;

        reloadConfig();
    }

    public void reloadConfig() {
        customConfigFile = new File(main.getDataFolder(), "savedata.yml");
        if (!customConfigFile.exists()) {
            customConfigFile.getParentFile().mkdirs();
            main.saveResource("savedata.yml", false);
        }

        customConfig = new YamlConfiguration();
        try {
            customConfig.load(customConfigFile);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    public FileConfiguration getConfig() {
        return customConfig;
    }

    public void saveConfig() {
        try {
            customConfig.save(customConfigFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
