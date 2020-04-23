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

package nl.hpfxd.prom.acf;

import net.jodah.expiringmap.ExpirationPolicy;
import net.jodah.expiringmap.ExpiringMap;

import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

@SuppressWarnings("WeakerAccess")
final class ACFPatterns {
    public static final Pattern COMMA = Pattern.compile(",");
    public static final Pattern PERCENTAGE = Pattern.compile("%", Pattern.LITERAL);
    public static final Pattern NEWLINE = Pattern.compile("\n");
    public static final Pattern DASH = Pattern.compile("-");
    public static final Pattern UNDERSCORE = Pattern.compile("_");
    public static final Pattern SPACE = Pattern.compile(" ");
    public static final Pattern SEMICOLON = Pattern.compile(";");
    public static final Pattern COLON = Pattern.compile(":");
    public static final Pattern COLONEQUALS = Pattern.compile("([:=])");
    public static final Pattern PIPE = Pattern.compile("\\|");
    public static final Pattern NON_ALPHA_NUMERIC = Pattern.compile("[^a-zA-Z0-9]");
    public static final Pattern INTEGER = Pattern.compile("^[0-9]+$");
    public static final Pattern VALID_NAME_PATTERN = Pattern.compile("^[a-zA-Z0-9_$]{1,16}$");
    public static final Pattern NON_PRINTABLE_CHARACTERS = Pattern.compile("[^\\x20-\\x7F]");
    public static final Pattern EQUALS = Pattern.compile("=");
    public static final Pattern FORMATTER = Pattern.compile("<c(?<color>\\d+)>(?<msg>.*?)</c\\1>", Pattern.CASE_INSENSITIVE);
    public static final Pattern I18N_STRING = Pattern.compile("\\{@@(?<key>.+?)}", Pattern.CASE_INSENSITIVE);



    private ACFPatterns() {}
    @SuppressWarnings("Convert2MethodRef")
    static final Map<String, Pattern> patternCache = ExpiringMap.builder()
            .maxSize(200)
            .expiration(1, TimeUnit.HOURS)
            .expirationPolicy(ExpirationPolicy.ACCESSED)
            .build();

    /**
     * Gets a pattern and compiles it.
     * If the pattern is stored already in {@link #patternCache}, it will simply fetch it from there.
     * If it is not, it will store it there for further use.
     * <p>
     * The {@link #patternCache} does not contain the constant patterns defined in this class.
     *
     * @param pattern
     *         The raw pattern in a String.
     *
     * @return The pattern which has been cached.
     */
    public static Pattern getPattern(String pattern) {
        return patternCache.computeIfAbsent(pattern, s -> Pattern.compile(pattern));
    }
}
