// $Id$
/*
 * WorldGuard
 * Copyright (C) 2010 sk89q <http://www.sk89q.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
*/

package com.sk89q.worldguard.bukkit;

import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.*;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import com.sk89q.worldedit.Vector;
import static com.sk89q.worldguard.bukkit.BukkitUtil.*;

public class WorldGuardEntityListener extends EntityListener {
    /**
     * Plugin.
     */
    private WorldGuardPlugin plugin;
    
    /**
     * Construct the object;
     * 
     * @param plugin
     */
    public WorldGuardEntityListener(WorldGuardPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onEntityDamageByBlock(EntityDamageByBlockEvent event) {
        Entity defender = event.getEntity();
        DamageCause type = event.getCause();
        
        if (defender instanceof Player) {
            Player player = (Player)defender;

            if (plugin.invinciblePlayers.contains(player.getName())) {
                event.setCancelled(true);
                return;
            }
            
            if (plugin.disableFallDamage && type == DamageCause.FALL) {
                event.setCancelled(true);
                return;
            }

            if (plugin.disableLavaDamage && type == DamageCause.LAVA) {
                event.setCancelled(true);
                return;
            }

            if (plugin.disableFireDamage && (type == DamageCause.FIRE
                    || type == DamageCause.FIRE_TICK)) {
                event.setCancelled(true);
                return;
            }

            if (plugin.disableDrowningDamage && type == DamageCause.DROWNING) {
                event.setCancelled(true);
                return;
            }

            if (type == DamageCause.DROWNING
                    && plugin.amphibiousPlayers.contains(player.getName())) {
                event.setCancelled(true);
                return;
            }
        }
    }

    @Override
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        Entity attacker = event.getDamager();
        Entity defender = event.getEntity();
        
        if (defender instanceof Player) {
            Player player = (Player)defender;

            if (plugin.invinciblePlayers.contains(player.getName())) {
                event.setCancelled(true);
                return;
            }
            
            if (attacker != null && attacker instanceof Player) {
                if (plugin.useRegions) {
                    Vector pt = toVector(defender.getLocation());
                    
                    if (!plugin.regionManager.getApplicableRegions(pt).allowsPvP()) {
                        player.sendMessage(ChatColor.DARK_RED + "You are in a no-PvP area.");
                        event.setCancelled(true);
                        return;
                    }
                }
            }
        }
    }
}