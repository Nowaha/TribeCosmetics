package me.nowaha.tribecosmetics.trail;

import me.nowaha.tribecosmetics.TribeCosmetics;

public class TrailHandler {
    TribeCosmetics main;

    public TrailHandler(TribeCosmetics main) {
        this.main = main;

        main.getServer().getScheduler().scheduleSyncRepeatingTask(main, new TrailRunnable(main), 0, 1);
    }
}
