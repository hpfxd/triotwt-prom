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

package nl.hpfxd.prom.listeners;

import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.xxmicloxx.NoteBlockAPI.event.SongNextEvent;
import com.xxmicloxx.NoteBlockAPI.model.Playlist;
import com.xxmicloxx.NoteBlockAPI.model.RepeatMode;
import com.xxmicloxx.NoteBlockAPI.model.Song;
import com.xxmicloxx.NoteBlockAPI.songplayer.RadioSongPlayer;
import com.xxmicloxx.NoteBlockAPI.utils.NBSDecoder;
import lombok.Getter;
import nl.hpfxd.prom.I18n;
import nl.hpfxd.prom.Prom;
import nl.hpfxd.prom.util.PromUtil;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class NoteBlockHandler implements Listener {
    @Getter private static RadioSongPlayer sp;
    public static void setupNoteBlocks() {
        Prom.getProm().getLogger().info("Setting up note blocks.");

        File dir = new File(Prom.getProm().getDataFolder(), "songs/");
        dir.mkdirs();

        Prom.getProm().getLogger().info("Creating playlist.");

        List<Song> songs = new ArrayList<>();
        for (File file : dir.listFiles()) {
            Prom.getProm().getLogger().info("Registering song " + file.getName());
            Song song = NBSDecoder.parse(file);
            songs.add(song);
        }

        Playlist playlist = new Playlist(songs.toArray(new Song[0]));

        sp = new RadioSongPlayer(playlist);
        sp.setPlaying(true);
        sp.setRandom(true);
        sp.setRepeatMode(RepeatMode.ALL);
        sp.setStereo(true);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        sp.addPlayer(event.getPlayer());
    }

    @EventHandler
    public void onNextSong(SongNextEvent event) {
        PromUtil.debug("Playing next note block song: {}", sp.getSong().getTitle());
        final WrappedChatComponent text = WrappedChatComponent.fromText(I18n.localize("music.nowPlaying",
                "title", sp.getSong().getTitle(),
                "author", sp.getSong().getAuthor()));
        final int[] i = {0};
        new BukkitRunnable() {
            @Override
            public void run() {
                Prom.getProm().getServer().getOnlinePlayers().forEach(player -> PromUtil.sendActionBar(player, text));
                i[0]++;

                if (i[0] == 10) this.cancel();
            }
        }.runTaskTimer(Prom.getProm(), 0, 20);
    }
}
