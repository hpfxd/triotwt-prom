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

package nl.hpfxd.prom.util;

import lombok.Getter;
import nl.hpfxd.prom.I18n;
import nl.hpfxd.prom.Prom;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

public class Cooldown {
    @Getter private static List<Cooldown> cooldowns = new ArrayList<>();

    @Getter private final UUID uuid;
    @Getter private final String id;
    @Getter private final long expiry;

    public Cooldown(UUID uuid, String id, int seconds) {
        this.uuid = uuid;
        this.id = id;
        this.expiry = System.currentTimeMillis() + (seconds * 1000);
    }

    public long getRemainingSeconds() {
        return (expiry - System.currentTimeMillis()) / 1000;
    }

    public void apply() {
        cooldowns.add(this);
    }

    public static Cooldown getCooldown(UUID uuid, String id) {
        for (Cooldown cooldown : cooldowns) {
            if (cooldown.getUuid().equals(uuid) && cooldown.getId().equals(id)) {
                return cooldown;
            }
        }

        return null;
    }

    public static boolean applyCooldownIfNotExists(Player player, String id, int seconds) {
        if (player == null) return false;
        UUID uuid = player.getUniqueId();
        Cooldown cooldown = getCooldown(uuid, id);

        if (cooldown == null) {
            new Cooldown(uuid, id, seconds).apply();
            return true;
        } else if (System.currentTimeMillis() > cooldown.getExpiry()) {
            cooldowns.remove(cooldown);
            new Cooldown(uuid, id, seconds).apply();
            return true;
        } else {
            Prom.getProm().getLogger().info(player.getName() + " still on " + id + " cooldown for " + seconds + "s, sending cooldown message!");
            if (player.isOnline()) {
                PromUtil.sendCenteredMessage(player, I18n.localize("cooldown",
                        "cooldown", id,
                        "seconds", cooldown.getRemainingSeconds()));
                PromUtil.playSound(player, Sound.ANVIL_LAND);
            }
            return false;
        }
    }

    public static boolean applyCooldownIfNotExists(Player player, String id) {
        return applyCooldownIfNotExists(player, id, Prom.getProm().getConfig().getInt("cooldowns." + id));
    }

    public static void sweepCooldowns() { // called every 30 seconds
        PromUtil.debug("Sweeping cooldowns.");
        cooldowns.removeIf(cooldown -> System.currentTimeMillis() > cooldown.getExpiry());
    }
}