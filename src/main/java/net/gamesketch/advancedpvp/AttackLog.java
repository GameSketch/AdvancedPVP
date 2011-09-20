package net.gamesketch.advancedpvp;

import org.bukkit.entity.Player;

import java.util.Comparator;
import java.util.Date;

public class AttackLog {
    private final Date timestamp;
    private final Player damager;
    private final int health;
    private final int damage;

    public AttackLog(Date timestamp, Player damager, int health, int damage) {
        this.timestamp = timestamp;
        this.damager = damager;
        this.health = health;
        this.damage = damage;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public Player getDamager() {
        return damager;
    }

    public int getHealth() {
        return health;
    }

    public int getDamage() {
        return damage;
    }

    static public Comparator<AttackLog> mostRecentComparator = new Comparator<AttackLog>() {
        public int compare(AttackLog o1, AttackLog o2) {
            // order is most recent first
            return -o1.getTimestamp().compareTo(o2.getTimestamp());
        }
    };
}
