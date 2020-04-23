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

package nl.hpfxd.prom.couples;

import lombok.Getter;
import nl.hpfxd.prom.Prom;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.UUID;

public class Couple {
    @Getter private UUID user1;
    @Getter private UUID user2;

    public Couple(UUID user1, UUID user2) {
        this.user1 = user1;
        this.user2 = user2;
    }

    public UUID getOtherUser(UUID user) {
        return user1 == user ? user2 : user1;
    }

    public OfflinePlayer getOtherPlayer(OfflinePlayer player) {
        return Prom.getProm().getServer().getOfflinePlayer(this.getOtherUser(player.getUniqueId()));
    }

    public Player getPlayer1() {
        return Prom.getProm().getServer().getPlayer(this.user1);
    }

    public Player getPlayer2() {
        return Prom.getProm().getServer().getPlayer(this.user2);
    }

    @Override
    public String toString() {
        return "Couple{user1=" + user1 + ",user2=" + user2 + "}";
    }
}
