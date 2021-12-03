package bg.sofia.uni.fmi.mjt.spotify.playable;

public abstract class Content implements Playable {
    private MediaType type = MediaType.UNDEFINED;

    private final String title;
    private final String artist;
    private final int year;
    private final double duration;

    private int totalPlays;

    public Content(String title, String artist, int year, double duration) {
        this.title = title;
        this.artist = artist;
        this.year = year;
        this.duration = duration;
    }

    public void setType(MediaType type) {
        this.type = type;
    }

    @Override
    public String play() {
        ++totalPlays;
        return String.format("Currently playing %s content: %s", type.toString(), title);
    }

    @Override
    public int getTotalPlays() {
        return totalPlays;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getArtist() {
        return artist;
    }

    @Override
    public int getYear() {
        return year;
    }

    @Override
    public double getDuration() {
        return duration;
    }
}
