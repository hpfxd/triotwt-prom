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
import co.aikar.commands.annotation.*;
import co.aikar.commands.annotation.HelpCommand;
import nl.hpfxd.prom.I18n;
import nl.hpfxd.prom.Prom;
import nl.hpfxd.prom.couples.Couple;
import nl.hpfxd.prom.couples.CoupleManager;
import nl.hpfxd.prom.listeners.PlayerLimitHandler;
import nl.hpfxd.prom.util.PromUtil;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandAlias("prom")
@CommandPermission("prom.command.prom")
public class PromCommand extends BaseCommand {
    @Subcommand("reload")
    public class Reload extends BaseCommand {
        @Default
        @Description("Reload the configuration.")
        private void reload(Player player) {
            Prom.getProm().reloadConfig();
            PromUtil.playSound(player, Sound.FIREWORK_BLAST);
            PromUtil.sendCenteredMessage(player, I18n.localize("commands.prom.reload.default"));
        }

        @Subcommand("couples")
        @Description("Reload the saved couples.")
        private void couples(Player player) {
            CoupleManager.loadCouples();
            PromUtil.playSound(player, Sound.FIREWORK_BLAST);
            PromUtil.sendCenteredMessage(player, I18n.localize("commands.prom.reload.couples",
                    "couples", CoupleManager.getCouples().size()));
        }
    }

    @Subcommand("tp couples")
    @Description("Teleport all couples to you.")
    public void tpCouples(Player player) {
        for (Couple couple : CoupleManager.getCouples()) {
            if (couple.getPlayer1() != null) couple.getPlayer1().teleport(player);
            if (couple.getPlayer2() != null) couple.getPlayer2().teleport(player);
        }

        PromUtil.debug("{} teleported all couples to them.", player.getName());
    }

    @HelpCommand
    public void help(CommandSender sender, CommandHelp help) {
        help.showHelp();
    }
}
