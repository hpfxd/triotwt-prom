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

package nl.hpfxd.prom.bungee.listeners;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.event.ServerKickEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class KickHandler implements Listener {
    private static ServerInfo limbo = ProxyServer.getInstance().getServerInfo("limbo");

    @EventHandler
    public void onKick(ServerKickEvent event) {
        if (!event.getKickedFrom().equals(limbo) && !event.getKickReason().contains("ban")) {
            if (!event.getPlayer().getServer().getInfo().getName().equals("limbo")) {
                event.getPlayer().sendMessage(event.getKickReasonComponent());
                event.setCancelServer(limbo);
                event.setCancelled(true);
            }
        }
    }
}
