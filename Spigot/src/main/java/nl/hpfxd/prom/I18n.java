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

import nl.hpfxd.prom.util.PromUtil;
import org.bukkit.ChatColor;

import java.util.HashMap;

public class I18n {
    public static String localize(String key, Object... replacements) {
        if (Prom.getProm().getConfig().contains("localization." + key)) {
            HashMap<String, String> replacementMap = new HashMap<>();

            String k = "";
            for (Object r : replacements) {
                if (k.equals("")) {
                    k = r.toString();
                } else {
                    replacementMap.put(k, r.toString());
                    k = "";
                }
            }

            String message = Prom.getProm().getConfig().getString("localization." + key);
            for (String replacementKey : replacementMap.keySet()) {
                message = message.replace("{" + replacementKey + "}", replacementMap.get(replacementKey));
            }

            return ChatColor.translateAlternateColorCodes('&', message);
        } else {
            PromUtil.debug("Translation key {} not found!", key);
            return ChatColor.RED + "This translation key could not be found! Please send this error to @hpfkys / Nate#7105";
        }
    }
}

