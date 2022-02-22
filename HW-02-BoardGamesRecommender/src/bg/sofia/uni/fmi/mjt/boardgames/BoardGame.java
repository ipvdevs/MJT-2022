package bg.sofia.uni.fmi.mjt.boardgames;

import java.util.Arrays;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

public record BoardGame(int id,
                        String name,
                        String description,
                        int maxPlayers,
                        int minAge,
                        int minPlayers,
                        int playingTimeMins,
                        Collection<String> categories,
                        Collection<String> mechanics) {

    private static final int ID = 0;
    private static final int MAX_PLAYERS = 1;
    private static final int MIN_AGE = 2;
    private static final int MIN_PLAYERS = 3;
    private static final int NAME = 4;
    private static final int PLAYING_TIME = 5;
    private static final int CATEGORIES = 6;
    private static final int MECHANICS = 7;
    private static final int DESCRIPTION = 8;

    public static BoardGame of(String line) {
        String[] tokens = line.split(";");

        String name = tokens[NAME];
        String description = tokens[DESCRIPTION];

        int id = Integer.parseInt(tokens[ID]);
        int maxPlayers = Integer.parseInt(tokens[MAX_PLAYERS]);
        int minAge = Integer.parseInt(tokens[MIN_AGE]);
        int minPlayers = Integer.parseInt(tokens[MIN_PLAYERS]);
        int playingTime = Integer.parseInt(tokens[PLAYING_TIME]);

        String categoriesToken = tokens[CATEGORIES];
        String mechanicsToken = tokens[MECHANICS];

        Set<String> categories =
                Arrays.stream(categoriesToken.split(","))
                        .collect(Collectors.toSet());

        Set<String> mechanics =
                Arrays.stream(mechanicsToken.split(","))
                        .collect(Collectors.toSet());

        return new BoardGame(
                id,
                name,
                description,
                maxPlayers,
                minAge,
                minPlayers,
                playingTime,
                categories,
                mechanics);
    }
}
