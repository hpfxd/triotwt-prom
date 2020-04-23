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

package nl.hpfxd.prom.bungee;

import net.md_5.bungee.api.plugin.Plugin;
import nl.hpfxd.prom.bungee.listeners.KickHandler;
import nl.hpfxd.prom.bungee.listeners.QueueHandler;

import java.util.concurrent.TimeUnit;

public final class PromBungee extends Plugin {
    @Override
    public void onEnable() {
        this.getProxy().getPluginManager().registerListener(this, new QueueHandler());
        this.getProxy().getPluginManager().registerListener(this, new KickHandler());

        this.getProxy().getScheduler().schedule(this, QueueHandler::tick, 0, 1, TimeUnit.SECONDS);
    }

    @Override
    public void onDisable() {
    }
}
