package net.gamesketch.advancedpvp.commands;

import net.gamesketch.advancedpvp.AdvancedPVP;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ResetPVPCommand implements CommandExecutor {
    private static final int SERVER_DEFAULT_VIEW_DISTANCE = 5;
    private final AdvancedPVP plugin;

    public ResetPVPCommand(AdvancedPVP plugin) {
        this.plugin = plugin;
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            if (!(sender instanceof Player)) {
                return false;
            }
            Player player = (Player) sender;
            return resetPlayerPVP(sender, player);
        }
        if (args.length > 0) {
            if ("server".equalsIgnoreCase(args[0])) {
                return resetServerPVP(sender, plugin.getServer());
            }
        }
        // These commands act on the world the sender is on, or the sender themselves
        if (args.length == 1) {
            if (!(sender instanceof Player)) {
                return false;
            }
            if ("world".equalsIgnoreCase(args[0])) {
                World world = ((Player) sender).getWorld();
                return resetWorldPVP(sender, world);
            } else if ("player".equalsIgnoreCase(args[0])) {
                Player player = (Player) sender;
                return resetPlayerPVP(sender, player);
            }
        }
        // These commands act on an arbitrary world or player, identified by name
        if (args.length > 1) {
            if ("world".equalsIgnoreCase(args[0])) {
                World world = plugin.getServer().getWorld(args[1]);
                return resetWorldPVP(sender, world);
            } else if ("player".equalsIgnoreCase(args[0])) {
                Player player = plugin.getServer().getPlayer(args[1]);
                return resetPlayerPVP(sender, player);
            }
        }
        return false;
    }

    private boolean resetPlayerPVP(CommandSender sender, Player player) {
        if (player == null) {
            return false;
        }
        if (!sender.hasPermission("advancedpvp.reset.player") && !sender.hasPermission("advancedpvp.reset.player." + player.getName()) && (!sender.hasPermission("advancedpvp.reset.self") && player.equals(sender))) {
            sender.sendMessage("You do not have permission to use this command.");
            return true;
        }
        plugin.resetPlayerPVP(player);
        sender.sendMessage("Player \""+player.getName()+"\" PVP mode reset. It is now "+plugin.getPlayerPVP(player).toString());
        return true;
    }

    private boolean resetWorldPVP(CommandSender sender, World world) {
        if (world == null) {
            return false;
        }
        if (!sender.hasPermission("advancedpvp.reset.world") && !sender.hasPermission("advancedpvp.reset.world." + world.getName())) {
            sender.sendMessage("You do not have permission to use this command.");
            return true;
        }
        plugin.resetWorldPVP(world);
        sender.sendMessage("World \""+world.getName()+"\" PVP mode reset. It is now "+plugin.getWorldPVP(world).toString());
        return true;
    }

    private boolean resetServerPVP(CommandSender sender, Server server) {
        if (server == null) {
            return false;
        }
        if (!sender.hasPermission("advancedpvp.reset.server") && !sender.hasPermission("advancedpvp.reset.server." + server.getName())) {
            sender.sendMessage("You do not have permission to use this command.");
            return true;
        }
        plugin.resetServerPVP(server);
        sender.sendMessage("Server PVP mode reset. It is now "+plugin.getServerPVP(server).toString());
        return true;
    }
}
