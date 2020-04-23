/*
 *     Copyright (c) 2020 hpfxd.nl
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package nl.hpfxd.prom.listeners;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import lombok.Getter;
import nl.hpfxd.prom.Prom;
import nl.hpfxd.prom.couples.Couple;
import nl.hpfxd.prom.couples.CoupleManager;
import nl.hpfxd.prom.parties.Party;
import nl.hpfxd.prom.parties.PartyManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PlayerVisibilityHandler implements Listener {
    @Getter private static List<UUID> visibilityEnabled = new ArrayList<>();

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        visibilityEnabled.add(player.getUniqueId());
        recalculatePlayer(player);
        for (Player p : Prom.getProm().getServer().getOnlinePlayers()) {
            if (shouldHide(p, player)) {
                p.hidePlayer(player);
            }
        }
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        visibilityEnabled.remove(event.getPlayer().getUniqueId());
    }

    public static void recalculatePlayer(Player player) {
        Prom.getProm().getLogger().info("Recalculating hidden players for " + player.getName());
        for (Player p : Prom.getProm().getServer().getOnlinePlayers()) {
            if (shouldHide(player, p)) {
                player.hidePlayer(p);
            } else {
                player.showPlayer(p);
            }
        }
    }

    public static boolean shouldHide(Player player, Player p) {
        Couple couple = CoupleManager.getCouple(player);
        Party party = PartyManager.getUserParty(player.getUniqueId());

        UUID partner = couple != null ? couple.getOtherUser(player.getUniqueId()) : null;
        boolean pe = party != null && party.getMembers().contains(p.getUniqueId());
        return !(visibilityEnabled.contains(player.getUniqueId()) ||
                p.getUniqueId().equals(partner) ||
                pe ||
                p.hasPermission("prom.alwaysvisible"));
    }
}
