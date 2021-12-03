package bg.sofia.uni.fmi.mjt.spotify.playlist;

import bg.sofia.uni.fmi.mjt.spotify.exceptions.PlaylistCapacityExceededException;
import bg.sofia.uni.fmi.mjt.spotify.playable.Playable;

public class UserPlaylist implements Playlist {

    private static final int PLAYLIST_CAPACITY = 20;

    private final String name;

    private static int mediaCount = 0;

    Playable[] playlist;

    public UserPlaylist(String name) {
        this.name = name;
        playlist = new Playable[PLAYLIST_CAPACITY];
    }

    @Override
    public void add(Playable playable) throws PlaylistCapacityExceededException {
        if (mediaCount == PLAYLIST_CAPACITY) {
            throw new PlaylistCapacityExceededException("Playlist is full! Could not add playable " + playable);
        }

        playlist[mediaCount] = playable;
        ++mediaCount;
    }

    @Override
    public String getName() {
        return name;
    }
}
