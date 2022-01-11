package bg.sofia.uni.fmi.mjt.twitch.content.video;

import bg.sofia.uni.fmi.mjt.twitch.content.AbstractContent;
import bg.sofia.uni.fmi.mjt.twitch.content.ContentType;
import bg.sofia.uni.fmi.mjt.twitch.content.Metadata;

import java.time.Duration;

public class Video extends AbstractContent {
    private final Duration duration;

    public Video(Metadata metadata, Duration duration) {
        super(ContentType.VIDEO, metadata);
        this.duration = duration;
    }

    @Override
    public Duration getDuration() {
        return duration;
    }

}
