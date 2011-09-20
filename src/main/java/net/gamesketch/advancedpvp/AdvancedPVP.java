package net.gamesketch.advancedpvp;

import net.gamesketch.advancedpvp.commands.ResetPVPCommand;
import net.gamesketch.advancedpvp.commands.SetPVPCommand;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

public class AdvancedPVP extends JavaPlugin {

    PVPGameMode serverGameMode;
    Map<World, PVPGameMode> worldGameModes;
    Map<OfflinePlayer, PVPGameMode> playerGameModes;
    private static final boolean SERVE_DEFAULT_GAME_MODE = false;

    public void onDisable() {
        PluginDescriptionFile pdfFile = this.getDescription();
        System.out.println( pdfFile.getName() + " version " + pdfFile.getVersion() + " is disabled!" );
    }

    public void onEnable() {
        PluginDescriptionFile pdfFile = this.getDescription();
        getCommand("setpvp").setExecutor(new SetPVPCommand(this));
        getCommand("resetpvp").setExecutor(new ResetPVPCommand(this));

        serverGameMode = new PVPGameMode(SERVE_DEFAULT_GAME_MODE);
        worldGameModes = new HashMap<World, PVPGameMode>();
        playerGameModes = new HashMap<OfflinePlayer, PVPGameMode>();

        System.out.println(pdfFile.getName() + " version " + pdfFile.getVersion() + " is enabled!");
    }

    public void setPlayerPVP(Player player, boolean pvp) {
        PVPGameMode pvpGameMode = playerGameModes.get(player);
        if (pvpGameMode != null) {
            pvpGameMode.setPvp(pvp);
        } else {
            playerGameModes.put(player, new PVPGameMode(pvp));
        }
    }

    public void setWorldPVP(World world, boolean pvp) {
        PVPGameMode pvpGameMode = worldGameModes.get(world);
        if (pvpGameMode != null) {
            pvpGameMode.setPvp(pvp);
        } else {
            worldGameModes.put(world, new PVPGameMode(pvp));
        }
    }

    public void setServerPVP(boolean pvp) {
        serverGameMode.setPvp(pvp);
    }

    public void resetPlayerPVP(Player player) {
        playerGameModes.remove(player);
    }

    public void resetWorldPVP(World world) {
        worldGameModes.remove(world);
    }

    public void resetServerPVP() {
        serverGameMode.setPvp(SERVE_DEFAULT_GAME_MODE);
    }

    public final PVPGameMode getPlayerPVP(OfflinePlayer player) {
        PVPGameMode pvpGameMode = playerGameModes.get(player);
        if (pvpGameMode == null) {
            World world = null;
            if (player instanceof Player) {
                world = ((Player) player).getWorld();
            }
            pvpGameMode = getWorldPVP(world); // will get server value if world is null or not set
        }
        return pvpGameMode;
    }

    public final PVPGameMode getWorldPVP(World world) {
        PVPGameMode pvpGameMode = worldGameModes.get(world);
        if (pvpGameMode == null) {
            pvpGameMode = getServerPVP();
        }
        return pvpGameMode;
    }

    public final PVPGameMode getServerPVP() {
        return this.serverGameMode;
    }

    public static boolean stringToBoolean(String arg) throws IllegalArgumentException {
        if (arg.equalsIgnoreCase(String.valueOf(true)) || arg.equalsIgnoreCase("on") || arg.equalsIgnoreCase("yes")) {
            return true;
        }
        if (arg.equalsIgnoreCase(String.valueOf(false)) || arg.equalsIgnoreCase("off") || arg.equalsIgnoreCase("no")) {
            return false;
        }
        throw new IllegalArgumentException("'"+arg+"' is not a valid representation of boolean.");
    }
}
