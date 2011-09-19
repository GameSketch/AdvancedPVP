package net.gamesketch.advancedpvp;

import net.gamesketch.advancedpvp.commands.ResetPVPCommand;
import net.gamesketch.advancedpvp.commands.SetPVPCommand;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

public class AdvancedPVP extends JavaPlugin {

    public void onDisable() {
        PluginDescriptionFile pdfFile = this.getDescription();
        System.out.println( pdfFile.getName() + " version " + pdfFile.getVersion() + " is disabled!" );
    }

    public void onEnable() {
        PluginDescriptionFile pdfFile = this.getDescription();
        getCommand("setpvp").setExecutor(new SetPVPCommand(this));
        getCommand("resetpvp").setExecutor(new ResetPVPCommand(this));
        System.out.println(pdfFile.getName() + " version " + pdfFile.getVersion() + " is enabled!");
    }

    public void setPlayerPVP(Player player, boolean pvp) {
    }

    public void setWorldPVP(World world, boolean pvp) {
    }

    public void setServerPVP(Server server, boolean pvp) {
    }

    public void resetPlayerPVP(Player player) {
    }

    public void resetWorldPVP(World world) {
    }

    public void resetServerPVP(Server server) {
    }

    public Object getPlayerPVP(Player player) {
        return null;
    }

    public Object getWorldPVP(World world) {
        return null;
    }

    public Object getServerPVP(Server server) {
        return null;
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
