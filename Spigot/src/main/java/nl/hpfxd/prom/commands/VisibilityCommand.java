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
import co.aikar.commands.annotation.Subcommand;
import nl.hpfxd.prom.I18n;
import nl.hpfxd.prom.listeners.PlayerVisibilityHandler;
import nl.hpfxd.prom.util.Cooldown;
import nl.hpfxd.prom.util.PromUtil;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandAlias("visibility|vis")
public class VisibilityCommand extends BaseCommand {
    @Default
    @Description("Toggle player visibility.")
    public void toggle(Player player) {
        if (!PlayerVisibilityHandler.getVisibilityEnabled().contains(player.getUniqueId())) {
            this.show(player);
        } else {
            this.hide(player);
        }
    }

    @Subcommand("show")
    @Description("Show all players.")
    public void show(Player player) {
        if (Cooldown.applyCooldownIfNotExists(player, "visibility")) {
            PlayerVisibilityHandler.getVisibilityEnabled().add(player.getUniqueId());
            PlayerVisibilityHandler.recalculatePlayer(player);
            PromUtil.sendCenteredMessage(player, I18n.localize("commands.vis.showAll"));
            PromUtil.playSound(player, Sound.LEVEL_UP);
        }
    }

    @Subcommand("hide")
    @Description("Hide all players.")
    public void hide(Player player) {
        if (Cooldown.applyCooldownIfNotExists(player, "visibility")) {
            PlayerVisibilityHandler.getVisibilityEnabled().remove(player.getUniqueId());
            PlayerVisibilityHandler.recalculatePlayer(player);
            PromUtil.sendCenteredMessage(player, I18n.localize("commands.vis.hideAll"));
            PromUtil.playSound(player, Sound.LEVEL_UP);
        }
    }

    @HelpCommand
    public void help(CommandSender sender, CommandHelp help) {
        help.showHelp();
    }
}
