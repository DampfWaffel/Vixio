package me.iblitzkriegi.vixio.effects.audio;

import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import me.iblitzkriegi.vixio.Vixio;
import me.iblitzkriegi.vixio.util.Util;
import me.iblitzkriegi.vixio.util.audio.AudioHandlers;
import me.iblitzkriegi.vixio.util.audio.GuildMusicManager;
import me.iblitzkriegi.vixio.util.wrapper.Bot;
import net.dv8tion.jda.core.entities.Guild;
import org.bukkit.event.Event;

public class EffPlay extends Effect {
    static {
        Vixio.getInstance().registerEffect(EffPlay.class, "play %strings/tracks% [in %guild%] [with %bot/string%]")
                .setName("Play audio")
                .setDesc("Play a specific audio track or attempt to load something from a URL")
                .setExample("play \"https://www.youtube.com/watch?v=elwTgpHlty0\" in guild with id \"2199673352656165156\" with \"Jewel\"");
    }

    private Expression<Guild> guild;
    private Expression<Object> audio;
    private Expression<Object> bot;

    @Override
    protected void execute(Event e) {
        Guild guild = this.guild.getSingle(e);
        Bot bot = Util.botFrom(this.bot.getSingle(e));
        if (bot == null || guild == null) {
            return;
        }

        GuildMusicManager musicManager = AudioHandlers.getGuildAudioPlayer(guild, bot);
        for (Object track : this.audio.getAll(e)) {
            if (track instanceof AudioTrack) {
                AudioHandlers.play(musicManager, (AudioTrack) track);
            } else if (track instanceof String) {
                AudioHandlers.loadAndPlay(guild, (String) track, bot);
            }
        }

    }

    @Override
    public String toString(Event e, boolean debug) {
        return "play " + audio.toString(e, debug) + " in " + guild.toString(e, debug) + " with " + bot.toString(e, debug);
    }

    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        audio = (Expression<Object>) exprs[0];
        guild = (Expression<Guild>) exprs[1];
        bot = (Expression<Object>) exprs[2];
        return true;
    }
}
