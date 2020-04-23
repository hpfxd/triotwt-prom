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

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.Title;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.event.ServerConnectedEvent;
import net.md_5.bungee.api.event.ServerDisconnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import nl.hpfxd.prom.bungee.PromUtil;

import java.util.ArrayList;
import java.util.List;

public class QueueHandler implements Listener {
    private static ServerInfo prom = ProxyServer.getInstance().getServerInfo("prom");
    private static ServerInfo limbo = ProxyServer.getInstance().getServerInfo("limbo");

    private static List<ProxiedPlayer> queue = new ArrayList<>();

    @EventHandler
    public void onJoin(ServerConnectedEvent event) {
        if (event.getServer().getInfo().equals(limbo)) {
            ProxiedPlayer player = event.getPlayer();

            queue.add(player);
            //player.connect(prom, ServerConnectEvent.Reason.UNKNOWN);
        }
    }

    @EventHandler
    public void onDisconnect(ServerDisconnectEvent event) {
        if (event.getTarget().equals(limbo)) {
            queue.remove(event.getPlayer());
        }
    }

    public static void tick() {
        prom.ping((result, error) -> {
            if (error == null && result != null) {
                ServerPing.Players players = result.getPlayers();

                if (players.getMax() > players.getOnline()) {
                    if (queue.size() > 0) {
                        ProxiedPlayer player = queue.get(0);

                        player.sendMessage(PromUtil.msg("&aConnecting you to Prom!"));
                        player.connect(prom);
                    }
                }
            } else {
                for (ProxiedPlayer player : queue) {
                    player.sendMessage(ChatMessageType.ACTION_BAR, PromUtil.msg("&cThere was an error pinging the prom server!"));
                }
            }
        });

        for (ProxiedPlayer player : queue) {
            Title title = ProxyServer.getInstance().createTitle()
                    .title(PromUtil.msg("&7You are in queue."))
                    .subTitle(PromUtil.msg("&6#" + (queue.indexOf(player) + 1)))
                    .fadeIn(0)
                    .stay(2 * 20)
                    .fadeOut(0);

            player.sendTitle(title);
        }
    }
}
