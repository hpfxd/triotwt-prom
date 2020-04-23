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
import lombok.Getter;
import nl.hpfxd.prom.I18n;
import nl.hpfxd.prom.Prom;
import nl.hpfxd.prom.couples.Couple;
import nl.hpfxd.prom.couples.CoupleManager;
import nl.hpfxd.prom.util.PromUtil;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

@CommandAlias("vote")
public class VoteCommand extends BaseCommand {
    private HashMap<UUID, UUID> votes = new HashMap<>();
    @Getter private static VoteState state = VoteState.NONE;

    @Subcommand("start")
    @CommandPermission("prom.command.vote.start")
    @Description("Initiate a vote.")
    public class Start extends BaseCommand {
        @Subcommand("single|solo")
        @Description("Initiate a vote with the type of SINGLE.")
        public void single(Player player) {
            PromUtil.broadcastCenteredMessage(I18n.localize("commands.vote.startSingle",
                    "player", player.getName()));
            PromUtil.playSound(Sound.NOTE_PIANO);
            state = VoteState.SINGLE;
            PromUtil.debug("Set vote state to {}.", state);
        }

        @Subcommand("couple|teams")
        @Description("Initiate a vote with the type of COUPLE.")
        public void couple(Player player) {
            PromUtil.broadcastCenteredMessage(I18n.localize("commands.vote.startCouple",
                    "player", player.getName()));
            PromUtil.playSound(Sound.NOTE_PIANO);
            state = VoteState.COUPLE;
            PromUtil.debug("Set vote state to {}.", state);
        }
    }

    @Subcommand("stop")
    @Description("Stop the current vote.")
    @CommandPermission("prom.command.vote.stop")
    public void stop(Player player) {
        if (state == VoteState.NONE) {
            PromUtil.sendCenteredMessage(player, I18n.localize("commands.vote.stopNoVote"));
            PromUtil.playSound(player, Sound.ANVIL_LAND);
            PromUtil.debug("{} tried to stop a vote, but vote state was {}!", player.getName(), state);
            return;
        } else if (state == VoteState.SINGLE) {
            LinkedHashMap<UUID, Integer> results = this.getResultsSingle();

            PromUtil.broadcastCenteredMessage(I18n.localize("commands.vote.stop"));

            int i = 0;
            for (UUID uuid : results.keySet()) {
                if (i > Prom.getProm().getConfig().getInt("voteTop")) break;
                OfflinePlayer op = Prom.getProm().getServer().getOfflinePlayer(uuid);
                if (op == null) continue;
                int v = results.get(uuid);

                PromUtil.debug("Broadcasting {}'s vote results. Votes: {}", op.getName(), v);
                Bukkit.broadcastMessage(I18n.localize("commands.vote.resultsSingle",
                        "place", i + 1,
                        "player", op.getName(),
                        "votes", v));
                i++;
            }

            PromUtil.debug("Sending individual vote places.");
            for (Player p : Prom.getProm().getServer().getOnlinePlayers()) {
                if (results.containsKey(p.getUniqueId())) {
                    p.sendMessage(I18n.localize("commands.vote.yourPlace",
                            "votes", results.get(p.getUniqueId())));
                } else {
                    p.sendMessage(I18n.localize("commands.vote.yourPlaceNoVotes"));
                }
            }
        } else if (state == VoteState.COUPLE) {
            LinkedHashMap<Couple, Integer> results = this.getResultsCouples();

            PromUtil.broadcastCenteredMessage(I18n.localize("commands.vote.stop"));

            int i = 0;
            for (Couple couple : results.keySet()) {
                if (i > Prom.getProm().getConfig().getInt("voteTop")) break;
                OfflinePlayer op1 = Prom.getProm().getServer().getOfflinePlayer(couple.getUser1());
                OfflinePlayer op2 = Prom.getProm().getServer().getOfflinePlayer(couple.getUser2());
                if (op1 == null || op2 == null) continue;
                int v = results.get(couple);

                PromUtil.debug("Broadcasting {} and {}'s vote results. Votes: {}", op1.getName(), op2.getName(), v);
                Bukkit.broadcastMessage(I18n.localize("commands.vote.resultsCouple",
                        "place", i + 1,
                        "player1", op1.getName(),
                        "player2", op2.getName(),
                        "votes", v));
                i++;
            }

            PromUtil.debug("Sending individual vote places.");
            for (Player p : Prom.getProm().getServer().getOnlinePlayers()) {
                Couple couple = CoupleManager.getCouple(p);
                if (results.containsKey(couple)) {
                    p.sendMessage(I18n.localize("commands.vote.yourPlace",
                            "votes", results.get(couple)));
                } else {
                    p.sendMessage(I18n.localize("commands.vote.yourPlaceNoVotes"));
                }
            }
        }

        PromUtil.playSound(Sound.NOTE_PIANO);
        state = VoteState.NONE;
        PromUtil.debug("Set vote state to {}.", state);
        votes.clear();
        PromUtil.debug("Cleared votes.");
    }

    @Description("Vote for a player or a couple.")
    @CommandCompletion("@players")
    @Default
    public void vote(Player player, OnlinePlayer p) {
        if (state == VoteState.NONE) {
            PromUtil.sendCenteredMessage(player, I18n.localize("commands.vote.noVote"));
            PromUtil.playSound(player, Sound.ANVIL_LAND);
        } else if (state == VoteState.SINGLE) {
            if (player.getUniqueId().equals(p.player.getUniqueId())) {
                PromUtil.sendCenteredMessage(player, I18n.localize("commands.vote.cannotVoteForSelf"));
                PromUtil.playSound(player, Sound.ANVIL_LAND);
                return;
            }

            votes.put(player.getUniqueId(), p.player.getUniqueId());
            PromUtil.sendCenteredMessage(player, I18n.localize("commands.vote.votedSingle",
                    "player", p.player.getName()));
            PromUtil.playSound(player, Sound.NOTE_SNARE_DRUM);
            PromUtil.debug("{} casted vote for {}", player.getName(), p.player.getName());
        } else if (state == VoteState.COUPLE) {
            Couple couple = CoupleManager.getCouple(p.player);

            if (couple == null) {
                PromUtil.sendCenteredMessage(player, I18n.localize("commands.vote.noCouple"));
                PromUtil.playSound(player, Sound.ANVIL_LAND);
                return;
            }

            if (CoupleManager.getCouple(player) == couple) {
                PromUtil.sendCenteredMessage(player, I18n.localize("commands.vote.cannotVoteForSelf"));
                PromUtil.playSound(player, Sound.ANVIL_LAND);
                return;
            }

            votes.put(player.getUniqueId(), p.player.getUniqueId());
            PromUtil.sendCenteredMessage(player, I18n.localize("commands.vote.votedCouple",
                    "player1", Prom.getProm().getServer().getOfflinePlayer(couple.getUser1()).getName(),
                    "player2", Prom.getProm().getServer().getOfflinePlayer(couple.getUser2()).getName()));
            PromUtil.playSound(player, Sound.NOTE_SNARE_DRUM);
            PromUtil.debug("{} casted vote for {}", player.getName(), p.player.getName());
        }
    }

    private LinkedHashMap<UUID, Integer> getResultsSingle() {
        PromUtil.debug("Starting result collection for vote type SINGLE, {} players voted.", votes.size());
        HashMap<UUID, Integer> results = new HashMap<>();
        for (UUID uuid : votes.values()) {
            results.put(uuid, results.getOrDefault(uuid, 0) + 1);
        }
        return results.entrySet().stream()
                .sorted(Map.Entry.<UUID, Integer>comparingByValue().reversed())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (e1, e2) -> e1, LinkedHashMap::new));
    }

    private LinkedHashMap<Couple, Integer> getResultsCouples() {
        PromUtil.debug("Starting result collection for vote type COUPLE, {} players voted.", votes.size());
        HashMap<Couple, Integer> results = new HashMap<>();
        for (UUID uuid : votes.values()) {
            Couple couple = CoupleManager.getCouple(uuid);
            results.put(couple, results.getOrDefault(couple, 0) + 1);
        }
        return results.entrySet().stream()
                .sorted(Map.Entry.<Couple, Integer>comparingByValue().reversed())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (e1, e2) -> e1, LinkedHashMap::new));
    }

    @HelpCommand
    public void help(CommandSender sender, CommandHelp help) {
        help.showHelp();
    }

    public enum VoteState {
        NONE,
        SINGLE,
        COUPLE
    }
}
