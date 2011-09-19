package net.gamesketch.advancedpvp.commands;

import net.gamesketch.advancedpvp.AdvancedPVP;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetPVPCommand implements CommandExecutor {
    private final AdvancedPVP plugin;

    public SetPVPCommand(AdvancedPVP plugin) {
        this.plugin = plugin;
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1) {
            if (!(sender instanceof Player)) {
                return false;
            }
            Player player = (Player) sender;
            return setPlayerPVP(sender, args[0], player);
        }
        if (args.length > 1) {
            if ("server".equalsIgnoreCase(args[0])) {
                return setServerPVP(sender, args[1], plugin.getServer());
            }
        }
        // These commands act on the world the sender is on, or the sender themselves
        if (args.length == 2) {
            if (!(sender instanceof Player)) {
                return false;
            }
            if ("world".equalsIgnoreCase(args[0])) {
                World world = ((Player) sender).getWorld();
                return setWorldPVP(sender, args[1], world);
            } else if ("player".equalsIgnoreCase(args[0])) {
                Player player = (Player) sender;
                return setPlayerPVP(sender, args[1], player);
            }
        }
        // These commands act on an arbitrary world or player, identified by name
        if (args.length > 2) {
            if ("world".equalsIgnoreCase(args[0])) {
                World world = plugin.getServer().getWorld(args[1]);
                return setWorldPVP(sender, args[2], world);
            } else if ("player".equalsIgnoreCase(args[0])) {
                Player player = plugin.getServer().getPlayer(args[1]);
                return setPlayerPVP(sender, args[2], player);
            }
        }
        return false;
    }

    private boolean setPlayerPVP(CommandSender sender, String pvp, Player player) {
        if (player == null) {
            return false;
        }
        if (!sender.hasPermission("advancedpvp.set.player") && !sender.hasPermission("advancedpvp.set.player." + player.getName()) && (!sender.hasPermission("advancedpvp.set.self") && player.equals(sender))) {
            sender.sendMessage("You do not have permission to use this command.");
            return true;
        }
        try {
            return setPlayerPVP(sender, AdvancedPVP.stringToBoolean(pvp), player);
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    private boolean setWorldPVP(CommandSender sender, String pvp, World world) {
        if (world == null) {
            return false;
        }
        if (!sender.hasPermission("advancedpvp.set.world") && !sender.hasPermission("advancedpvp.set.world." + world.getName())) {
            sender.sendMessage("You do not have permission to use this command.");
            return true;
        }
        try {
            return setWorldPVP(sender, AdvancedPVP.stringToBoolean(pvp), world);
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    private boolean setServerPVP(CommandSender sender, String pvp, Server server) {
        if (server == null) {
            return false;
        }
        if (!sender.hasPermission("advancedpvp.set.server") && !sender.hasPermission("advancedpvp.set.server." + server.getName())) {
            sender.sendMessage("You do not have permission to use this command.");
            return true;
        }
        try {
            return setServerPVP(sender, AdvancedPVP.stringToBoolean(pvp), server);
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    private boolean setPlayerPVP(CommandSender sender, boolean pvp, Player player) {
        if (player == null) {
            return false;
        }
        plugin.setPlayerPVP(player, pvp);
        sender.sendMessage("Player \""+player.getName()+"\" PVP mode set to "+ pvp);
        return true;
    }

    private boolean setWorldPVP(CommandSender sender, boolean pvp, World world) {
        if (world == null) {
            return false;
        }
        plugin.setWorldPVP(world, pvp);
        sender.sendMessage("World \""+world.getName()+"\" PVP mode set to "+ pvp);
        return true;
    }

    private boolean setServerPVP(CommandSender sender, boolean pvp, Server server) {
        if (server == null) {
            return false;
        }
        plugin.setServerPVP(server, pvp);
        sender.sendMessage("Server PVP mode set to "+ pvp);
        return true;
    }

}
