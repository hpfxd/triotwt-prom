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
import co.aikar.commands.bukkit.contexts.OnlinePlayer;
import net.jodah.expiringmap.ExpiringMap;
import nl.hpfxd.prom.I18n;
import nl.hpfxd.prom.util.Cooldown;
import nl.hpfxd.prom.util.PromUtil;
import nl.hpfxd.prom.couples.Couple;
import nl.hpfxd.prom.couples.CoupleManager;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@CommandAlias("couple|marry|partner|c")
public class CoupleCommand extends BaseCommand {
    private Map<UUID, UUID> requests = ExpiringMap.builder()
            .expiration(5, TimeUnit.MINUTES)
            .build();

    @Default
    @Description("View your current partner.")
    public void couple(Player player) {
        Couple couple = CoupleManager.getCouple(player);

        if (couple == null) {
            PromUtil.sendCenteredMessage(player, I18n.localize("commands.couple.view.noPartner"));
            PromUtil.playSound(player, Sound.ANVIL_LAND);
        } else {
            PromUtil.sendCenteredMessage(player, I18n.localize("commands.couple.view.partner",
                    "player", couple.getOtherPlayer(player).getName()));
            PromUtil.playSound(player, Sound.NOTE_PLING);
        }
    }

    @Description("Propose a partner request to a player.")
    @CommandCompletion("@players")
    @Default
    public void propose(Player player, OnlinePlayer player2) {
        if (CoupleManager.getCouple(player) != null) {
            PromUtil.sendCenteredMessage(player, I18n.localize("commands.couple.propose.alreadyHasPartnerSelf"));
            PromUtil.playSound(player, Sound.ANVIL_LAND);
        } else if (CoupleManager.getCouple(player2.player) != null) {
            PromUtil.sendCenteredMessage(player, I18n.localize("commands.couple.propose.alreadyHasPartnerOther"));
            PromUtil.playSound(player, Sound.ANVIL_LAND);
        } else if (requests.containsKey(player.getUniqueId()) && requests.get(player.getUniqueId()).equals(player2.player.getUniqueId())) {
            PromUtil.sendCenteredMessage(player, I18n.localize("commands.couple.propose.alreadySent",
                    "player", player.getName()));
            PromUtil.playSound(player, Sound.ANVIL_LAND);
        } else {
            for (UUID r : requests.keySet()) {
                UUID s = requests.get(r);

                if (s.equals(player.getUniqueId()) && r.equals(player2.player.getUniqueId())) {
                    CoupleManager.createCouple(player, player2.player);
                    requests.remove(player.getUniqueId());
                    return;
                }
            }

            requests.put(player.getUniqueId(), player2.player.getUniqueId());

            PromUtil.sendCenteredMessage(player, I18n.localize("commands.couple.propose.sentSelf",
                    "player", player2.player.getName()));
            PromUtil.playSound(player, Sound.LEVEL_UP);
            PromUtil.sendCenteredMessage(player2.player, I18n.localize("commands.couple.propose.sentOther",
                    "player", player.getName()));
            PromUtil.playSound(player2.player, Sound.LEVEL_UP);
        }
    }

    @Description("Break up with your partner.")
    @Subcommand("breakup|leave")
    public void breakup(Player player) {
        if (VoteCommand.getState() != VoteCommand.VoteState.NONE) {
            PromUtil.sendCenteredMessage(player, I18n.localize("commands.couple.voteError"));
            return;
        }
        Couple couple = CoupleManager.getCouple(player);

        if (couple != null) {
            CoupleManager.breakup(couple);
            PromUtil.playSound(player, Sound.GHAST_SCREAM);
        } else {
            PromUtil.sendCenteredMessage(player, I18n.localize("commands.couple.breakup.noPartner"));
            PromUtil.playSound(player, Sound.ANVIL_LAND);
        }
    }

    @Description("Kiss your partner <3")
    @Subcommand("kiss|mwah|k")
    public void kiss(Player player) {
        if (Cooldown.applyCooldownIfNotExists(player, "kiss")) {
            Couple couple = CoupleManager.getCouple(player);

            if (couple != null) {
                if (couple.getOtherPlayer(player).isOnline()) {
                    Player player2 = Bukkit.getPlayer(couple.getOtherUser(player.getUniqueId()));

                    PromUtil.sendCenteredMessage(player, I18n.localize("commands.couple.kiss.self"));
                    PromUtil.playSound(player, Sound.LEVEL_UP);
                    PromUtil.sendCenteredMessage(player2, I18n.localize("commands.couple.kiss.other"));
                    PromUtil.playSound(player2, Sound.LEVEL_UP);
                } else {
                    PromUtil.sendCenteredMessage(player, I18n.localize("commands.couple.kiss.offline"));
                    PromUtil.playSound(player, Sound.ANVIL_LAND);
                }
            } else {
                PromUtil.sendCenteredMessage(player, I18n.localize("commands.couple.kiss.noPartner"));
                PromUtil.playSound(player, Sound.ANVIL_LAND);
            }
        }
    }

    @HelpCommand
    public void help(CommandSender sender, CommandHelp help) {
        help.showHelp();
    }
}
