package net.gamesketch.advancedpvp;

import net.gamesketch.advancedpvp.commands.ResetPVPCommand;
import net.gamesketch.advancedpvp.commands.SetPVPCommand;
import net.gamesketch.advancedpvp.listeners.PlayerAttackListener;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityListener;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class AdvancedPVP extends JavaPlugin {

    PVPGameMode serverGameMode;
    Map<World, PVPGameMode> worldGameModes;
    Map<OfflinePlayer, PVPGameMode> playerGameModes;
    private static final boolean SERVE_DEFAULT_GAME_MODE = false;
    private EntityListener playerAttackListener = new PlayerAttackListener(this);
    private Map<Player, List<AttackLog>> attackLogLists;
    private Map<Player, PvpProperties> pvpPropertiesMap;

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
        attackLogLists = new HashMap<Player, List<AttackLog>>();
        pvpPropertiesMap = new HashMap<Player, PvpProperties>();

        PluginManager pm = getServer().getPluginManager();
        pm.registerEvent(Event.Type.ENTITY_DAMAGE, playerAttackListener, Event.Priority.Normal, this);

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

    public void registerDamage(Player damagee, Player damager, int damage) {
        List<AttackLog> attackLogs = getAttackLogs(damagee);
        attackLogs.add(0, new AttackLog(new Date(), damager, damagee.getHealth(), damage)); // insert new log at start of list
    }

    public boolean isAttackBlocked(Player damagee, Player damager) {
        PvpProperties pvpProperties = pvpPropertiesMap.get(damagee);
        if (pvpProperties == null) {
            pvpProperties = new PvpProperties();
        }
        pvpPropertiesMap.put(damagee, pvpProperties);
        return pvpProperties.isInCooldown();
    }

    public void registerDeath(Player deadPlayer) {
        Date currentTime = new Date();
        Date cutOff = new Date(currentTime.getTime() - getCutOffTime(deadPlayer));
        List<AttackLog> attackLogs = attackLogLists.get(deadPlayer);
        Collections.sort(attackLogs, AttackLog.mostRecentComparator);
        Map<Player, Integer> damageCounts = new HashMap<Player, Integer>();
        for (AttackLog attackLog : attackLogs) {
            if (attackLog.getTimestamp().after(cutOff)) {
                final Player damager = attackLog.getDamager(); // might be null, means not a player
                Integer damageCount = damageCounts.get(damager);
                if (damageCount == null) {
                    damageCount = attackLog.getDamage();
                } else {
                    damageCount += attackLog.getDamage();
                }
                damageCounts.put(damager, damageCount);
            } else {
                break;
            }
        }
        int playerDamage = 0;
        int nonPlayerDamage = 0;
        for (Map.Entry<Player, Integer> entry : damageCounts.entrySet()) {
            if (entry.getKey() == null) {
                nonPlayerDamage += entry.getValue();
            } else {
                playerDamage += entry.getValue();
            }
        }
        if (playerDamage > nonPlayerDamage) {
            addPvpCooldown(deadPlayer);
        }
        /*
        // This code determines the player that caused the most damage - will be useful later
        Map.Entry<Player, Integer> mostDamage = Collections.max(damageCounts.entrySet(), new Comparator<Map.Entry<Player, Integer>>() {
            public int compare(Map.Entry<Player, Integer> o1, Map.Entry<Player, Integer> o2) {
                return o1.getValue().compareTo(o2.getValue());
            }
        });

        if (mostDamage.getKey() == null) {
            // most damage was not by a player
        } else {
            // most damage was by a player
            addPvpCooldown(deadPlayer);
        }*/
    }

    private void addPvpCooldown(Player player) {
        PvpProperties pvpProperties = pvpPropertiesMap.get(player);
        if (pvpProperties == null) {
            pvpProperties = new PvpProperties();
        }
        pvpProperties.addPvpCooldown(getCutOffTime(player));
        pvpPropertiesMap.put(player, pvpProperties);
    }

    /**
     *
     * @param player the player whose cut off time you want to know
     * @return time in milliseconds the given player is immune to pvp attack - 3 minutes for now
     */
    private long getCutOffTime(Player player) {
        return 3*60*1000; // TODO make configurable
    }

    private List<AttackLog> getAttackLogs(Player damagee) {
        List<AttackLog> attackLogs = attackLogLists.get(damagee);
        if (attackLogs == null) {
            attackLogs = new LinkedList<AttackLog>(); //use linked list because most operations will be near the start
            attackLogLists.put(damagee, attackLogs);
        }
        return attackLogs;
    }

}
