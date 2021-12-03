package bg.sofia.uni.fmi.mjt.spotify.playable;

public class Video extends Content {
    public Video(String title, String artist, int year, double duration) {
        super(title, artist, year, duration);
        setType(MediaType.VIDEO);
    }
}
