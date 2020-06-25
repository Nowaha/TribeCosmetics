package me.nowaha.tribecosmetics.trail;

import me.nowaha.tribecosmetics.TribeCosmetics;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Particle;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TrailRunnable implements Runnable {
    TribeCosmetics main;

    public static Map<UUID, String> playersActiveTrails = new HashMap<>();

    public TrailRunnable(TribeCosmetics main) {
        this.main = main;
    }

    @Override
    public void run() {
        for (UUID uuid :
                playersActiveTrails.keySet()) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) {
                spawnTrail(playersActiveTrails.get(uuid), player);
            }
        }
    }

    public void spawnTrail(String name, Player player) {
        ConfigurationSection data = main.getConfig().getConfigurationSection("trails." + name);

        if (data != null) {
            if (!data.getBoolean("multiple", false)) {
                if (data.getString("type", "BARRIER").equalsIgnoreCase("REDSTONE")) {
                    player.getWorld().spawnParticle(Particle.REDSTONE,
                            player.getLocation(), data.getInt("amount", 1),
                            new Particle.DustOptions(Color.fromRGB(data.getInt("color.r", 255), data.getInt("color.g", 255), data.getInt("color.b", 255)), data.getInt("size", 10))
                    );
                } else {
                    player.getWorld().spawnParticle(Particle.valueOf(data.getString("type", "BARRIER")), player.getLocation(), data.getInt("amount", 1));
                }
            } else {
                for (String type :
                        data.getConfigurationSection("type").getKeys(false)) {
                    ConfigurationSection particleData = data.getConfigurationSection("type." + type);

                    if (type.replaceAll("-", "").equalsIgnoreCase("REDSTONE")) {
                        player.getWorld().spawnParticle(Particle.REDSTONE,
                                player.getLocation(), particleData.getInt("amount", 1),
                                new Particle.DustOptions(Color.fromRGB(particleData.getInt("color.r", 255), particleData.getInt("color.g", 255), particleData.getInt("color.b", 255)), particleData.getInt("size", 10))
                        );
                    } else {
                        player.getWorld().spawnParticle(Particle.valueOf(type.replaceAll("-", "")), player.getLocation(), particleData.getInt("amount", 1));
                    }
                }
            }
        }
    }
}
