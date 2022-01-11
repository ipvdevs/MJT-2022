package bg.sofia.uni.fmi.mjt.game.recommender;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GameRecommender {
    private List<Game> games;

    /**
     * Loads the dataset from the given {@code dataInput} stream.
     *
     * @param dataInput java.io.Reader input stream from which the dataset can be read
     */
    public GameRecommender(Reader dataInput) {
        Objects.requireNonNull(dataInput, "DataInput should not be null");

        try (var bufferedReader = new BufferedReader(dataInput)) {

            games = bufferedReader
                    .lines()
                    .map(Game::of)
                    .collect(Collectors.toList());

        } catch (IOException e) {
            games = Collections.emptyList();
        }
    }

    /**
     * @return All games from the dataset as an unmodifiable copy.
     * If the dataset is empty, return an empty collection
     */
    public List<Game> getAllGames() {
        return List.copyOf(games);
    }

    /**
     * Returns all games in the dataset released after the specified {@code date} as an unmodifiable list.
     * If no games have been released after the given date, returns an empty list.
     *
     * @param date
     * @return a list of all games released after {@code date}, in an undefined order.
     */
    public List<Game> getGamesReleasedAfter(LocalDate date) {
        Objects.requireNonNull(date, "Date cannot be null!");

        return games
                .stream()
                .filter((game) -> game.releaseDate().isAfter(date))
                .collect(Collectors.toList());
    }

    /**
     * Returns the top {@code n} games by user review score.
     *
     * @param n maximum number of games to return
     *          If {@code n} exceeds the total number of games in the dataset, return all games.
     * @return unmodifiable list of the games sorted by user review score in descending order
     * @throws IllegalArgumentException in case {@code n} is a negative number.
     */
    public List<Game> getTopNUserRatedGames(int n) {
        if (n < 0) {
            throw new IllegalArgumentException("N should be a non-negative number!");
        }

        return games
                .stream()
                .sorted(Comparator.comparingDouble(Game::userReview).reversed())
                .limit(n)
                .toList();
    }

    /**
     * Returns a list (without repetitions) of all years in which at least one game with meta score
     * {@code minimalScore} or higher has been released. The order of the years in the result is undefined.
     * If there are no such years, return an empty list.
     *
     * @param minimalScore
     * @return the years when a game with at least {@code minimalScore} meta score has been released
     */
    public List<Integer> getYearsWithTopScoringGames(int minimalScore) {
        return games
                .stream()
                .filter(game -> game.metaScore() >= minimalScore)
                .map(game -> game.releaseDate().getYear())
                .distinct()
                .toList();
    }

    /**
     * Returns the names of all games in the dataset released in {@code year} as a comma-separated String.
     * Each comma in the result must be followed by a space. The order of the game names in the result is undefined.
     * If no games have been released in the given year, returns an empty String.
     *
     * @param year
     * @return a comma-separated String containing all game names released in {@code year}
     */
    public String getAllNamesOfGamesReleasedIn(int year) {
        return games
                .stream()
                .filter((game) -> game.releaseDate().getYear() == year)
                .map((Game::name))
                .collect(Collectors.joining(", "));
    }

    /**
     * Returns the game for the specified {@code platform} with the highest user review score.
     *
     * @param platform the name of the platform
     * @return the game for the specified {@code platform} with the highest review score
     * @throws NoSuchElementException if there is no game at all released for the specified platform,
     *                                or if {@code platform} is null or an empty String.
     */
    public Game getHighestUserRatedGameByPlatform(String platform) {
        return games
                .stream()
                .filter(game -> game.platform().equals(platform))
                .max(Comparator.comparingDouble(Game::userReview))
                .orElseThrow(NoSuchElementException::new);
    }

    /**
     * Returns all games by platform. The result should map a platform name to the set of all games
     * released for this platform.
     *
     * @return all games by platform
     */
    public Map<String, Set<Game>> getAllGamesByPlatform() {
        return games
                .stream()
                .collect(Collectors.groupingBy(Game::platform, Collectors.toSet()));
    }

    /**
     * Returns the number of years a game platform has been live.
     * The lifecycle of a platform is assumed to start and end with the release year of the oldest and newest game
     * released for this platform (the exact date is not significant).
     * In case all games for a given platform have been released in a single year, return 1.
     * In case {@code platform} is null, blank or unknown in the dataset, return 0.
     *
     * @return the number of years a game platform has been live
     */
    public int getYearsActive(String platform) {
        int minYear = games
                .stream()
                .filter(game -> game.platform().equals(platform))
                .map(game -> game.releaseDate().getYear())
                .min(Integer::compare)
                .orElse(0);

        int maxYear = games
                .stream()
                .filter(game -> game.platform().equals(platform))
                .map(game -> game.releaseDate().getYear())
                .max(Integer::compare)
                .orElse(0);


        return (minYear != 0 && maxYear != 0) ? Math.abs(maxYear - minYear) + 1 : 0;
    }

    /**
     * Returns the games whose summary contains all {@code keywords} specified, as an unmodifiable list.
     * <p>
     * If there are no such games, return an empty list.
     * In case no keywords are specified, or any of the keywords is null or blank, the result is undefined.
     *
     * @param keywords the keywords to search for in the game summary
     * @return the games whose summary contains the specified keywords
     */
    public List<Game> getGamesSimilarTo(String... keywords) {
        Objects.requireNonNull(keywords, "A keyword cannot be null.");

        if (keywords.length == 0) {
            return Collections.emptyList();
        }

        List<Game> matchingGames = new ArrayList<>();

        for (String keyword : keywords) {
            matchingGames.addAll(games
                    .stream()
                    .filter(game -> game.summary().contains(keyword))
                    .toList());
        }

        return List.copyOf(matchingGames);
    }
}
