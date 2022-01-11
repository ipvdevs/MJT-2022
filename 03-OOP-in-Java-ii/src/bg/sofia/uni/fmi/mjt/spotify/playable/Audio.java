package bg.sofia.uni.fmi.mjt.spotify.playable;

public class Audio extends Content {

    public Audio(String title, String artist, int year, double duration) {
        super(title, artist, year, duration);
        setType(MediaType.AUDIO);
    }
}
