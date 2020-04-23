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
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.HelpCommand;
import nl.hpfxd.prom.I18n;
import nl.hpfxd.prom.listeners.WaldoHeadHandler;
import nl.hpfxd.prom.util.PromUtil;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandAlias("waldohead|waldo|heads")
public class WaldoHeadCommand extends BaseCommand {
    @Default
    @Description("Show your current waldo heads.")
    public void heads(Player player) {
        if (WaldoHeadHandler.getFound().containsKey(player.getUniqueId())) {
            PromUtil.sendCenteredMessage(player, I18n.localize("waldoHead.displayFound",
                    "heads", WaldoHeadHandler.getFound().get(player.getUniqueId()).size()));
            PromUtil.playSound(player, Sound.NOTE_SNARE_DRUM);
        } else {
            PromUtil.sendCenteredMessage(player, I18n.localize("waldoHead.displayFoundNone"));
            PromUtil.playSound(player, Sound.NOTE_SNARE_DRUM);
        }
    }

    @HelpCommand
    public void help(CommandSender sender, CommandHelp help) {
        help.showHelp();
    }
}
