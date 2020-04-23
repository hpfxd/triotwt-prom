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

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import lombok.Getter;
import nl.hpfxd.prom.I18n;
import nl.hpfxd.prom.Prom;
import nl.hpfxd.prom.util.PromUtil;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class WaldoHeadHandler implements Listener {
    private static List<Location> heads = new ArrayList<>();
    @Getter private static ListMultimap<UUID, Location> found = ArrayListMultimap.create();

    public static void setup() {
        Prom.getProm().getLogger().info("Setting up heads.");

        heads.addAll(Prom.getProm().getConfig().getStringList("waldoHeads").stream()
                .map(PromUtil::deserializeLocation)
                .collect(Collectors.toList()));
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Location loc = event.getClickedBlock().getLocation();

            if (isHead(loc)) {
                Player player = event.getPlayer();

                if (!found.containsEntry(player.getUniqueId(), loc)) {
                    found.put(player.getUniqueId(), loc);

                    PromUtil.sendCenteredMessage(player, I18n.localize("heads.found",
                            "heads", found.get(player.getUniqueId()).size()));
                    PromUtil.playSound(player, Sound.FIREWORK_LARGE_BLAST);
                } else {
                    PromUtil.sendCenteredMessage(player, I18n.localize("heads.alreadyFound"));
                    PromUtil.playSound(player, Sound.ENDERMAN_TELEPORT);
                }
            }
        }
    }

    private boolean isHead(Location loc) {
        for (Location location : heads) {
            if (location.equals(loc)) return true;
        }
        return false;
    }
}
