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
import nl.hpfxd.prom.I18n;
import nl.hpfxd.prom.Prom;
import nl.hpfxd.prom.couples.Couple;
import nl.hpfxd.prom.couples.CoupleManager;
import nl.hpfxd.prom.util.Cooldown;
import nl.hpfxd.prom.util.PromUtil;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@CommandAlias("rose|flower")
public class RoseCommand extends BaseCommand {
    private static HashMap<UUID, Integer> roses = new HashMap<>();

    @Description("Give a rose to a player.")
    @CommandCompletion("@players")
    @Default
    public void rose(Player player, OnlinePlayer p) {
        if (player.getUniqueId() == p.player.getUniqueId()) {
            PromUtil.sendCenteredMessage(player, I18n.localize("commands.rose.cannotGiveSelf"));
            PromUtil.playSound(player, Sound.ANVIL_LAND);
        } else {
            if (Cooldown.applyCooldownIfNotExists(player, "rose")) {
                Couple couple = CoupleManager.getCouple(player);
                if (couple != null && couple == CoupleManager.getCouple(p.player)) {
                    PromUtil.sendCenteredMessage(player, I18n.localize("commands.rose.givePartner"));
                    PromUtil.sendCenteredMessage(p.player, I18n.localize("commands.rose.givenPartner"));
                    PromUtil.playSound(p.player, Sound.LEVEL_UP);
                } else {
                    PromUtil.sendCenteredMessage(player, I18n.localize("commands.rose.give",
                            "player", p.player.getName()));

                    if (!p.player.hasPermission("prom.norosemsg")) {
                        PromUtil.sendCenteredMessage(p.player, I18n.localize("commands.rose.given"));
                        PromUtil.playSound(p.player, Sound.LEVEL_UP);
                    }
                }

                PromUtil.debug("{} gave a rose to {}", player.getName(), p.player.getName());
                PromUtil.playSound(player, Sound.LEVEL_UP);

                ItemStack rose = new ItemStack(Material.RED_ROSE);
                ItemMeta meta = rose.getItemMeta();
                meta.setDisplayName(I18n.localize("commands.rose.name"));
                rose.setItemMeta(meta);
                p.player.getInventory().addItem(rose);

                roses.put(p.player.getUniqueId(), roses.containsKey(p.player.getUniqueId()) ? roses.get(p.player.getUniqueId()) + 1 : 1);
            }
        }
    }

    @Subcommand("top")
    @Description("View the players with most roses received.")
    public void top(Player player) {
        if (Cooldown.applyCooldownIfNotExists(player, "rose top")) {
            LinkedHashMap<UUID, Integer> results = this.getTopRoses();

            PromUtil.sendCenteredMessage(player, I18n.localize("commands.rose.top.header"));
            PromUtil.playSound(player, Sound.NOTE_SNARE_DRUM);

            int i = 0;
            for (UUID uuid : results.keySet()) {
                OfflinePlayer p = Prom.getProm().getServer().getOfflinePlayer(uuid);

                player.sendMessage(I18n.localize("commands.rose.top.result",
                        "place", i + 1,
                        "player", p.getName(),
                        "roses", results.get(uuid)));

                i++;
                if (i >= 5) break;
            }
        }
    }

    private LinkedHashMap<UUID, Integer> getTopRoses() {
        return roses.entrySet().stream()
                .sorted(Map.Entry.<UUID, Integer>comparingByValue().reversed())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (e1, e2) -> e1, LinkedHashMap::new));
    }

    @HelpCommand
    public void help(CommandSender sender, CommandHelp help) {
        help.showHelp();
    }
}
