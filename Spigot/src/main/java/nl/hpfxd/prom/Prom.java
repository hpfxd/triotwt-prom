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

package nl.hpfxd.prom;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.InvalidCommandArgument;
import co.aikar.commands.MessageKeys;
import co.aikar.commands.PaperCommandManager;
import lombok.Getter;
import nl.hpfxd.prom.commands.*;
import nl.hpfxd.prom.couples.CoupleManager;
import nl.hpfxd.prom.listeners.*;
import nl.hpfxd.prom.parties.Party;
import nl.hpfxd.prom.parties.PartyManager;
import nl.hpfxd.prom.util.PromUtil;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public final class Prom extends JavaPlugin {
    @Getter private static Prom prom;
    @Getter private PaperCommandManager commandManager;

    @Override
    public void onEnable() {
        prom = this;

        this.saveDefaultConfig();

        this.setupCommands();
        this.setupListeners();
        this.setupCooldowns();
        this.setupCouples();
        this.setupParties();
        NoteBlockHandler.setupNoteBlocks();
        WaldoHeadHandler.setup();
    }

    @Override
    public void onDisable() {
        prom = null;
    }

    private void setupCommands() {
        this.getLogger().info("Setting up commands.");
        this.commandManager = new PaperCommandManager(prom);
        commandManager.enableUnstableAPI("help");

        commandManager.getCommandContexts().registerIssuerOnlyContext(Party.class, c-> {
            if (c.getSender() instanceof Player) {
                Party party = PartyManager.getUserParty(c.getPlayer().getUniqueId());

                if (party == null) {
                    PromUtil.sendCenteredMessage(c.getPlayer(), I18n.localize("commands.party.notInParty"));
                    PromUtil.playSound(c.getPlayer(), Sound.ANVIL_LAND);
                    throw new InvalidCommandArgument("", false);
                } else {
                    if (c.hasFlag("leader")) {
                        if (!c.getPlayer().hasPermission("prom.partyadmin") && party.getLeader() != c.getPlayer().getUniqueId()) {
                            PromUtil.sendCenteredMessage(c.getPlayer(), I18n.localize("commands.party.notLeader"));
                            PromUtil.playSound(c.getPlayer(), Sound.ANVIL_LAND);
                            throw new InvalidCommandArgument("", false);
                        } else {
                            return party;
                        }
                    } else {
                        return party;
                    }
                }
            } else {
                throw new InvalidCommandArgument(MessageKeys.NOT_ALLOWED_ON_CONSOLE, false);
            }
        });

        for (BaseCommand command : new BaseCommand[] {
                new PromCommand(),
                new CoupleCommand(),
                new BroadcastCommand(),
                new VisibilityCommand(),
                new VoteCommand(),
                new RoseCommand(),
                new PartyCommand(),
                new HelpCommand(),
                new WaldoHeadCommand()
        }) {
            this.getLogger().info("Registering command " + command.getClass().getSimpleName());
            commandManager.registerCommand(command);
        }
    }

    private void setupListeners() {
        this.getLogger().info("Setting up listeners.");
        for (Listener listener : new Listener[] {
                new ChatHandler(),
                new TabHandler(),
                new PlayerVisibilityHandler(),
                new JoinHandler(),
                new PlayerLimitHandler(),
                new PartyManager(),
                new MobHandler(),
                new NoteBlockHandler(),
                new WaldoHeadHandler()
        }) {
            this.getLogger().info("Registering listener " + listener.getClass().getSimpleName());
            this.getServer().getPluginManager().registerEvents(listener, this);
        }
    }

    private void setupCouples() {
        this.getLogger().info("Setting up couples.");
        CoupleManager.loadCouples();
        this.getServer().getScheduler().runTaskTimer(this, CoupleManager::saveCouples, 0, 60 * 20);
    }

    private void setupParties() {
        this.getLogger().info("Setting up parties.");
        this.getServer().getScheduler().runTaskTimer(this, CoupleManager::saveCouples, 0, 10 * 20);
    }

    private void setupCooldowns() {
        this.getLogger().info("Setting up cooldowns.");
        this.getServer().getScheduler().runTaskTimer(this, PartyManager::tick, 0, 60 * 20);
    }
}
