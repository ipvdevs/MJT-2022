package bg.sofia.uni.fmi.mjt.spotify.library;

import bg.sofia.uni.fmi.mjt.spotify.exceptions.EmptyLibraryException;
import bg.sofia.uni.fmi.mjt.spotify.exceptions.LibraryCapacityExceededException;
import bg.sofia.uni.fmi.mjt.spotify.exceptions.PlaylistNotFoundException;
import bg.sofia.uni.fmi.mjt.spotify.playlist.Playlist;
import bg.sofia.uni.fmi.mjt.spotify.playlist.UserPlaylist;

public class UserLibrary implements Library {

    private static final int LIBRARY_CAPACITY = 21;
    private static final String LIKED_SONGS_PLAYLIST_NAME = "Liked Content";

    private int libSize = 1;

    Playlist[] library;

    public UserLibrary() {
        library = new Playlist[LIBRARY_CAPACITY];
        library[0] = new UserPlaylist(LIKED_SONGS_PLAYLIST_NAME);
    }

    @Override
    public void add(Playlist playlist) throws LibraryCapacityExceededException {
        if (playlist == null) {
            throw new IllegalArgumentException("playlist cannot be null!");
        }

        if (library.length == libSize) {
            throw new LibraryCapacityExceededException("Library is full!");
        }

        library[libSize] = playlist;
        ++libSize;
    }

    @Override
    public void remove(String name) throws EmptyLibraryException, PlaylistNotFoundException {
        validateSongName(name);

        if (libSize == 0) {
            throw new EmptyLibraryException("Library is empty! Cannot remove from " + name);
        }

        int removeIndex = findPlaylist(name);

        if (removeIndex == -1) {
            throw new PlaylistNotFoundException("Could not find playlist " + name);
        }

        for (int i = removeIndex; i < libSize - 1; i++) {
            library[i] = library[i + 1];
        }

        library[libSize - 1] = null;
        --libSize;
    }

    private int findPlaylist(String name) {
        int removeIndex = -1;

        for (int i = 0; i < libSize; i++) {
            if (library[i].getName().equals(name)) {
                removeIndex = i;
                break;
            }
        }

        return removeIndex;
    }

    private void validateSongName(String name) {
        if (name == null) {
            throw new IllegalArgumentException("name cannot be null!");
        }

        if (name.isEmpty()) {
            throw new IllegalArgumentException("name is empty!");
        }

        if (name.equals("Liked Content")) {
            throw new IllegalArgumentException();
        }
    }

    @Override
    public Playlist getLiked() {
        return library[0];
    }
}
