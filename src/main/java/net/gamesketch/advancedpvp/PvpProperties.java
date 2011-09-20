package net.gamesketch.advancedpvp;

import java.util.Date;

public class PvpProperties {
    private Date coolDownTill;

    public void addPvpCooldown(long time) {
        coolDownTill = new Date(new Date().getTime() + time);
    }

    public boolean isInCooldown() {
        return coolDownTill != null && coolDownTill.after(new Date());
    }
}
