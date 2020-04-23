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
import nl.hpfxd.prom.I18n;
import nl.hpfxd.prom.Prom;
import nl.hpfxd.prom.util.PromUtil;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CoupleManager {
    private static File file = new File(Prom.getProm().getDataFolder(), "src/main/resources/couples.yml");
    private static FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);
    @Getter private static List<Couple> couples = new ArrayList<>();

    public static void createCouple(Player player1, Player player2) {
        Couple couple = new Couple(player1.getUniqueId(), player2.getUniqueId());
        PromUtil.sendCenteredMessage(player1, I18n.localize("couples.create",
                "player", player2.getName()));
        PromUtil.sendCenteredMessage(player2, I18n.localize("couples.create",
                "player", player1.getName()));

        PromUtil.playSound(player1, Sound.FIREWORK_LAUNCH);
        PromUtil.playSound(player2, Sound.FIREWORK_LAUNCH);

        couples.add(couple);
        PromUtil.debug("New couple: {}", couple);
    }

    public static void breakup(Couple couple) {
        PromUtil.debug("Couple breakup: {}", couple);
        couples.remove(couple);

        if (couple.getPlayer1() != null) {
            PromUtil.playSound(couple.getPlayer1(), Sound.ANVIL_LAND);
            PromUtil.sendCenteredMessage(couple.getPlayer1(), I18n.localize("couples.breakupExecutor"));
        }

        if (couple.getPlayer2() != null) {
            PromUtil.playSound(couple.getPlayer2(), Sound.ANVIL_LAND);
            PromUtil.sendCenteredMessage(couple.getPlayer2(), I18n.localize("couples.breakupExecutor"));
        }
    }

    public static Couple getCouple(Player player) {
        return getCouple(player.getUniqueId());
    }

    public static Couple getCouple(UUID uuid) {
        for (Couple couple : couples) {
            if (couple.getUser1() == uuid || couple.getUser2() == uuid) {
                return couple;
            }
        }

        return null;
    }

    public static void loadCouples() {
        try {
            cfg.load(file);
            couples.clear();

            for (String str : cfg.getStringList("couples")) {
                String[] s = str.split("\\|");

                couples.add(new Couple(UUID.fromString(s[0]), UUID.fromString(s[1])));
            }

            PromUtil.debug("Loaded {} couples.", couples.size());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void saveCouples() {
        try {
            List<String> l = new ArrayList<>();
            for (Couple couple : couples) {
                l.add(couple.getUser1() + "|" + couple.getUser2());
            }

            cfg.set("couples", l);
            cfg.save(file);
            PromUtil.debug("Saved couples.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
