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
import nl.hpfxd.prom.parties.Party;
import nl.hpfxd.prom.parties.PartyManager;
import nl.hpfxd.prom.util.Cooldown;
import nl.hpfxd.prom.util.PromUtil;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

@CommandAlias("party|p")
public class PartyCommand extends BaseCommand {
    @Subcommand("create")
    @Description("Create a party.")
    public void create(Player player) {
        if (Cooldown.applyCooldownIfNotExists(player, "party create")) {
            if (PartyManager.getUserParty(player.getUniqueId()) == null) {
                Party party = new Party(player.getUniqueId());
                PartyManager.getParties().add(party);

                PromUtil.debug("PARTY: Created by {}", player.getName());
                PromUtil.sendCenteredMessage(player, I18n.localize("commands.party.create.success",
                        "player", player.getName()));
                PromUtil.playSound(player, Sound.LEVEL_UP);
            } else {
                PromUtil.sendCenteredMessage(player, I18n.localize("commands.party.create.inParty"));
                PromUtil.playSound(player, Sound.ANVIL_LAND);
            }
        }
    }

    @Subcommand("invite")
    @CommandCompletion("@players")
    @Description("Invite a player to your party.")
    public void invite(Player s, @Flags("leader") Party party, OnlinePlayer player) {
        if (party.getMembers().size() >= Prom.getProm().getConfig().getInt("parties.maxSize")) {
            PromUtil.sendCenteredMessage(s, I18n.localize("commands.party.full"));
        } else {
            if (Cooldown.applyCooldownIfNotExists(s, "party invite")) {
                if (PartyManager.getUserParty(player.player.getUniqueId()) == null) {
                    PromUtil.debug("PARTY: {} sent invite to {}", s.getName(), player.player.getName());
                    party.invite(player.player);
                } else {
                    PromUtil.sendCenteredMessage(s, I18n.localize("commands.party.invite.inParty",
                            "player", player.player.getName()));
                    PromUtil.playSound(s, Sound.ANVIL_LAND);
                }
            }
        }
    }

    @Subcommand("teleport|tp")
    @Description("Teleport all players in your party to you.")
    public void teleport(Player player, @Flags("leader") Party party) {
        if (Cooldown.applyCooldownIfNotExists(player, "party teleport")) {
            List<Player> players = party.getOnlinePlayers();

            if (players.size() > Prom.getProm().getConfig().getInt("parties.maxTeleportSize")) {
                PromUtil.sendCenteredMessage(player, I18n.localize("commands.party.teleport.tooBig"));
                PromUtil.playSound(player, Sound.ANVIL_LAND);
            } else {
                PromUtil.debug("PARTY: {} teleported {} players.", player.getName(), players.size());
                players.forEach(p -> {
                    p.teleport(player);
                    PromUtil.sendCenteredMessage(player, I18n.localize("commands.party.teleport.success",
                            "players", players.size()));
                    PromUtil.playSound(player, Sound.NOTE_SNARE_DRUM);
                });
            }
        }
    }

    @CommandAlias("pchat|pc")
    @Subcommand("chat|c")
    @Description("Send a message in party chat.")
    public void chat(Player player, Party party, String message) {
        if (!party.isMuted() || party.getLeader() == player.getUniqueId()) {
            if (player.hasPermission("prom.bypasschatcooldown") || Cooldown.applyCooldownIfNotExists(player, "party chat")) {
                Prom.getProm().getLogger().info("[PARTY] [" + party.getId() + "] " + player.getName() + ": " + message);
                party.getOnlinePlayers().forEach(p -> {
                    p.sendMessage(I18n.localize("commands.party.chat.format",
                            "player", player.getName(),
                            "msg", message));
                });
            }
        } else {
            PromUtil.sendCenteredMessage(player, I18n.localize("commands.party.chat.muted"));
            PromUtil.playSound(player, Sound.ANVIL_LAND);
        }
    }

    @Subcommand("mute")
    @Description("Mute party chat")
    public void mute(@Flags("leader") Party party) {
        party.setMuted(!party.isMuted());

        party.getOnlinePlayers().forEach(p -> {
            PromUtil.sendCenteredMessage(p, I18n.localize("commands.party.mute." + (party.isMuted() ? "" : "un") + "mute"));
            PromUtil.playSound(p, Sound.NOTE_SNARE_DRUM);
        });
    }

    @Subcommand("list|l")
    @CommandAlias("plist|pl")
    @Description("List the players in the party.")
    public void list(Player player, Party party) {
        List<String> s = new ArrayList<>();
        party.getOnlinePlayers().forEach(p -> s.add(ChatColor.YELLOW + "" + (party.getLeader() == p.getUniqueId() ? ChatColor.BOLD : "") + p.getName()));

        player.sendMessage(ChatColor.BLUE + "" + ChatColor.STRIKETHROUGH + PromUtil.HLINE);
        player.sendMessage(String.join(ChatColor.GRAY + ", ", s));
        player.sendMessage(ChatColor.BLUE + "" + ChatColor.STRIKETHROUGH + PromUtil.HLINE);
    }

    @Subcommand("promote")
    @Description("Promote a player to party leader.")
    public void promote(@Flags("leader") Party party, OnlinePlayer player) {
        party.getOnlinePlayers().forEach(p -> {
            PromUtil.sendCenteredMessage(p, I18n.localize("commands.party.promote.success",
                    "player", player.player.getName()));
            PromUtil.playSound(p, Sound.FIREWORK_BLAST);
        });
        party.setLeader(player.player.getUniqueId());
    }

    @Subcommand("leave")
    @Description("Leave your party.")
    public void leave(Player player, Party party) {
        if (player.getUniqueId() == party.getLeader()) {
            party.disband();
        } else {
            party.kick(player.getUniqueId());
        }
    }

    @Subcommand("join|accept")
    @Description("Accept a party invite.")
    public void join(Player player, OnlinePlayer p) {
        if (Cooldown.applyCooldownIfNotExists(player, "party join")) {
            Party party = PartyManager.getUserParty(p.player.getUniqueId());

            if (party != null) {
                if (party.getInvites().containsKey(player.getUniqueId())) {
                    party.join(player);
                    party.getInvites().remove(player.getUniqueId());
                } else {
                    PromUtil.sendCenteredMessage(player, I18n.localize("commands.party.join.notInvited"));
                    PromUtil.playSound(player, Sound.ANVIL_LAND);
                }
            } else {
                PromUtil.sendCenteredMessage(player, I18n.localize("commands.party.join.noParty"));
                PromUtil.playSound(player, Sound.ANVIL_LAND);
            }
        }
    }

    @Subcommand("kick|remove")
    @Description("Kick a player from the party.")
    public void kick(@Flags("leader") Party party, OnlinePlayer player) {
        if (party.getMembers().contains(player.player.getUniqueId())) {
            party.kick(player.player.getUniqueId());
        }
    }

    @HelpCommand
    public void help(CommandSender sender, CommandHelp help) {
        help.showHelp();
    }
}
