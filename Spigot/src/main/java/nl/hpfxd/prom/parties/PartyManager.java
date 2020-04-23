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

package nl.hpfxd.prom.parties;

import lombok.Getter;
import nl.hpfxd.prom.I18n;
import nl.hpfxd.prom.Prom;
import nl.hpfxd.prom.util.PromUtil;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class PartyManager implements Listener {
    @Getter private static List<Party> parties = new ArrayList<>();
    private static HashMap<UUID, Long> offlinePlayers = new HashMap<>();

    public static Party getUserParty(UUID uuid) {
        for (Party party : parties) {
            if (party.getMembers().contains(uuid)) {
                return party;
            }
        }

        return null;
    }

    public static void tick() {
        // offline
        Iterator<Map.Entry<UUID, Long>> oit = offlinePlayers.entrySet().iterator();
        while (oit.hasNext()) {
            Map.Entry<UUID, Long> entry = oit.next();
            UUID uuid = entry.getKey();
            long since = entry.getValue();

            if (since < (System.currentTimeMillis() - TimeUnit.MINUTES.toMillis(Prom.getProm().getConfig().getInt("parties.offlineTime")))) {
                Party party = getUserParty(uuid);
                if (party != null) {
                    if (party.getLeader() == uuid) {
                        party.disband();
                    } else {
                        party.kick(uuid);
                    }
                } else {
                    oit.remove();
                }
            }
        }

        // invites
        for (Party party : parties) {
            Iterator<Map.Entry<UUID, Long>> it = party.getInvites().entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<UUID, Long> entry = it.next();
                UUID uuid = entry.getKey();
                long time = entry.getValue();

                if (time < (System.currentTimeMillis() - TimeUnit.MINUTES.toMillis(1))) {
                    OfflinePlayer player = Prom.getProm().getServer().getOfflinePlayer(uuid);
                    party.getOnlinePlayers().forEach(p -> PromUtil.sendCenteredMessage(p, I18n.localize("commands.party.invite.expired",
                            "player", player.getName())));
                    it.remove();
                }
            }
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        offlinePlayers.remove(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        Party party = PartyManager.getUserParty(player.getUniqueId());

        if (party != null) {
            offlinePlayers.put(player.getUniqueId(), System.currentTimeMillis());
        }
    }
}
