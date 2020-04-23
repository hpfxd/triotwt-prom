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

package nl.hpfxd.prom.util;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import nl.hpfxd.prom.I18n;
import nl.hpfxd.prom.Prom;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;

public class PromUtil {
    private static final ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();

    private static final int CENTER_PX = 154;
    public static final String HLINE = StringUtils.repeat("-", 53);

    public static Location deserializeLocation(String str) {
        String[] s = str.split("\\|");
        Location loc = new Location(Prom.getProm().getServer().getWorld(s[0]), Double.parseDouble(s[1]), Double.parseDouble(s[2]), Double.parseDouble(s[3]));
        if (s.length == 6) {
            loc.setYaw(Float.parseFloat(s[4]));
            loc.setPitch(Float.parseFloat(s[5]));
        }

        return loc;
    }

    public static void playSound(Player player, Sound sound) {
        player.playSound(player.getLocation(), sound, 1, 1);
    }
    public static void playSound(Sound sound) {
        Prom.getProm().getServer().getOnlinePlayers().forEach(p -> playSound(p, sound));
    }

    public static String color(String str) {
        return ChatColor.translateAlternateColorCodes('&', str);
    }

    public static void debug(String str, Object... objs) {
        for (Object obj : objs) {
            str = str.replaceFirst("\\{}", ChatColor.YELLOW + obj.toString() + ChatColor.WHITE);
        }

        Prom.getProm().getLogger().info("[DEBUG] " + ChatColor.stripColor(str));
        Prom.getProm().getServer().broadcast(I18n.localize("debug",
                "str", str), "prom.debug");
    }

    public static void broadcastCenteredMessage(String message) {
        Prom.getProm().getServer().getOnlinePlayers().forEach(p -> sendCenteredMessage(p, message));
    }

    // https://www.spigotmc.org/threads/95872/ Thank you! <3
    public static void sendCenteredMessage(Player player, String message) {
        if (message == null || message.equals("")) {
            player.sendMessage("");
            return;
        }

        String[] s = message.split("\n");
        if (s.length > 1) {
            for (String line : s) {
                if (line.endsWith("{br}")) {
                    player.sendMessage(line.replace("{br}", "") + HLINE);
                } else if (line.startsWith("{n}")) {
                    player.sendMessage(line.replace("{n}", ""));
                } else {
                    sendCenteredMessage(player, line);
                }
            }
            return;
        }

        int messagePxSize = 0;
        boolean previousCode = false;
        boolean isBold = false;

        for (char c : message.toCharArray()) {
            if (c == '§') {
                previousCode = true;
            } else if (previousCode) {
                previousCode = false;
                isBold = c == 'l' || c == 'L';
            } else {
                DefaultFontInfo dfi = DefaultFontInfo.getDefaultFontInfo(c);
                messagePxSize += isBold ? dfi.getBoldLength() : dfi.getLength();
                messagePxSize++;
            }
        }

        int halvedMessageSize = messagePxSize / 2;
        int toCompensate = CENTER_PX - halvedMessageSize;
        int spaceLength = DefaultFontInfo.SPACE.getLength() + 1;
        int compensated = 0;
        StringBuilder sb = new StringBuilder();
        while (compensated < toCompensate) {
            sb.append(" ");
            compensated += spaceLength;
        }
        player.sendMessage(sb.toString() + message);
    }

    public static void sendActionBar(Player player, WrappedChatComponent text) {
        PacketContainer chatPacket = protocolManager.createPacket(PacketType.Play.Server.CHAT);
        chatPacket.getChatComponents().write(0, text);
        chatPacket.getBytes().write(0, (byte) 2);
        try {
            protocolManager.sendServerPacket(player, chatPacket);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
}
