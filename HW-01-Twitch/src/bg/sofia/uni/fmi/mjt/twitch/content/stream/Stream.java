package bg.sofia.uni.fmi.mjt.twitch.content.stream;

import bg.sofia.uni.fmi.mjt.twitch.content.AbstractContent;
import bg.sofia.uni.fmi.mjt.twitch.content.Category;
import bg.sofia.uni.fmi.mjt.twitch.content.ContentType;
import bg.sofia.uni.fmi.mjt.twitch.content.Metadata;
import bg.sofia.uni.fmi.mjt.twitch.user.User;

import java.time.Duration;
import java.time.Instant;

public class Stream extends AbstractContent {
    private final Instant started;
    private Instant ended;

    public Stream(Metadata metadata) {
        super(ContentType.STREAM, metadata);
        this.started = Instant.now();
    }

    public void end() {
        this.ended = Instant.now();
    }

    @Override
    public Duration getDuration() {
        if (ended != null) {
            return Duration.between(started, ended);
        }

        return Duration.between(started, Instant.now());
    }

    @Override
    public void stopWatching(User user) {
        super.stopWatching(user);
        --numberOfViews;
    }
}
