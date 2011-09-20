package net.gamesketch.advancedpvp;

public class PVPGameMode {
    private boolean pvp;

    public PVPGameMode(boolean pvp) {
        this.pvp = pvp;
    }

    public boolean isPvp() {
        return pvp;
    }

    public void setPvp(boolean pvp) {
        this.pvp = pvp;
    }
}
