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

package nl.hpfxd.prom.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.HelpCommand;
import nl.hpfxd.prom.I18n;
import nl.hpfxd.prom.Prom;
import nl.hpfxd.prom.util.PromUtil;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.github.paperspigot.Title;

@CommandAlias("broadcast|announce|bc")
@CommandPermission("prom.command.broadcast")
public class BroadcastCommand extends BaseCommand {
    @Default
    @Description("Broadcast a message to the entire server.")
    public void broadcast(CommandSender s, String message) {
        Prom.getProm().getLogger().info("[Broadcast] " + s.getName() + ": " + message);
        String m = I18n.localize("commands.broadcast.format",
                "player", s.getName(),
                "message", message);
        Title title = new Title(PromUtil.color("&9&lAnnouncement"), PromUtil.color("&7" + message),
                Prom.getProm().getConfig().getInt("commands.broadcast.title.fadeIn"),
                Prom.getProm().getConfig().getInt("commands.broadcast.title.stay"),
                Prom.getProm().getConfig().getInt("commands.broadcast.title.fadeOut"));

        for (Player p : Prom.getProm().getServer().getOnlinePlayers()) {
            PromUtil.playSound(p, Sound.NOTE_PLING);
            PromUtil.sendCenteredMessage(p, m);
            p.sendTitle(title);
        }
    }

    @HelpCommand
    public void help(CommandSender sender, CommandHelp help) {
        help.showHelp();
    }
}
