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

import net.md_5.bungee.api.chat.TextComponent;
import nl.hpfxd.prom.I18n;
import nl.hpfxd.prom.Prom;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class TabHandler implements Listener {
    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        String header = I18n.localize("tab.header");
        String footer = I18n.localize("tab.footer");

        player.setPlayerListHeaderFooter(TextComponent.fromLegacyText(header), TextComponent.fromLegacyText(footer));
        Prom.getProm().getLogger().info("Sent tab list header + footer to " + player.getName());
    }
}
