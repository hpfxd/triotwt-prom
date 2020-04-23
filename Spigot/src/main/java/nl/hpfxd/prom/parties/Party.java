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
import lombok.Setter;
import nl.hpfxd.prom.I18n;
import nl.hpfxd.prom.Prom;
import nl.hpfxd.prom.listeners.PlayerVisibilityHandler;
import nl.hpfxd.prom.util.PromUtil;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.*;

public class Party {
    @Getter private UUID id = UUID.randomUUID();
    @Getter private List<UUID> members = new ArrayList<>();
    @Getter private Map<UUID, Long> invites = new HashMap<>();

    @Getter @Setter private UUID leader;

    @Getter @Setter private boolean muted = false;

    public Party(UUID leader) {
        this.members.add(leader);
        this.leader = leader;
    }

    public List<Player> getOnlinePlayers() {
        List<Player> players = new ArrayList<>();
        for (Player player : Prom.getProm().getServer().getOnlinePlayers()) if (this.members.contains(player.getUniqueId())) players.add(player);
        return players;
    }

    public void invite(Player player) {
        this.invites.put(player.getUniqueId(), System.currentTimeMillis());

        OfflinePlayer leader = Prom.getProm().getServer().getOfflinePlayer(this.leader);
        PromUtil.sendCenteredMessage(player, I18n.localize("commands.party.invite.invited",
                "player", leader.getName()));
        PromUtil.playSound(player, Sound.NOTE_PLING);

        this.getOnlinePlayers().forEach(p -> PromUtil.sendCenteredMessage(p, I18n.localize("commands.party.invite.success",
                "player", player.getName())));
    }

    public void join(Player player) {
        if (this.getMembers().size() >= Prom.getProm().getConfig().getInt("parties.maxSize")) {
            PromUtil.sendCenteredMessage(player, I18n.localize("commands.party.full"));
        } else {
            this.members.add(player.getUniqueId());

            PromUtil.playSound(player, Sound.FIREWORK_BLAST);
            this.getOnlinePlayers().forEach(p -> {
                PromUtil.sendCenteredMessage(p, I18n.localize("commands.party.join.success",
                        "player", player.getName()));

                p.showPlayer(player);
                player.showPlayer(p);
            });
        }
    }

    public void kick(UUID uuid) {
        OfflinePlayer player = Prom.getProm().getServer().getOfflinePlayer(uuid);
        Player pl = Prom.getProm().getServer().getPlayer(uuid);

        for (Player p : this.getOnlinePlayers()) {
            PromUtil.sendCenteredMessage(p, I18n.localize("commands.party.kick",
                    "player", player.getName()));

            if (pl != null) {
                if (PlayerVisibilityHandler.shouldHide(pl, p)) pl.hidePlayer(p);
                if (PlayerVisibilityHandler.shouldHide(p, pl)) p.hidePlayer(pl);
            }
        }
        this.members.remove(uuid);
    }

    public void disband() {
        this.getOnlinePlayers().forEach(p -> {
            PromUtil.sendCenteredMessage(p, I18n.localize("commands.party.disband"));
            PromUtil.playSound(p, Sound.ANVIL_BREAK);
        });

        PartyManager.getParties().remove(this);
    }
}
