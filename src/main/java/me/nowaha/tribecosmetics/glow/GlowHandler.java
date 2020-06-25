package me.nowaha.tribecosmetics.glow;

import me.nowaha.tribecosmetics.TribeCosmetics;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GlowHandler implements Runnable {
    private TribeCosmetics main;
    public static Map<UUID, String> playersGlows = new HashMap<>();

    public GlowHandler(TribeCosmetics main) {
        this.main = main;
    }

    @Override
    public void run() {
    }
}
