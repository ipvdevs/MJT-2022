package bg.sofia.uni.fmi.mjt.game.recommender;

import bg.sofia.uni.fmi.mjt.game.recommender.util.GamesStreamInitializer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class GameRecommenderTest {
    private static List<Game> games;
    private static GameRecommender gameRecommender;

    @BeforeAll
    private static void setUp() throws IOException {
        InputStream dataStream = GamesStreamInitializer.getGameStream();

        try (var isr = new InputStreamReader(dataStream);
             var bufferedReader = new BufferedReader(isr)) {

            games = bufferedReader
                    .lines()
                    .map(Game::of)
                    .collect(Collectors.toList());
        }

        var isr = new InputStreamReader(GamesStreamInitializer.getGameStream());
        gameRecommender = new GameRecommender(isr);
    }

    @Test
    void gameRecommenderWithNullReader() {
        assertThrows(NullPointerException.class, () -> new GameRecommender(null));
    }

    @Test
    void getAllGamesNoData() {
        String message = "Number of all games should be 0. The data set is empty.";
        byte[] emptyStr = "".getBytes(StandardCharsets.UTF_8);
        var reader = new InputStreamReader(new ByteArrayInputStream(emptyStr));

        assertTrue(new GameRecommender(reader).getAllGames().isEmpty(), message);
    }

    @Test
    void getAllGamesIsListModifiable() {
        String message = "The collection returned should be unmodifiable.";
        Game game = new Game("", "", LocalDate.now(), "", 0, 0.0f);

        List<Game> unmodifiable = gameRecommender.getAllGames();

        assertThrows(UnsupportedOperationException.class, () -> unmodifiable.add(0, game), message);
    }

    @Test
    void getAllGames() {
        List<Game> actual = gameRecommender.getAllGames();

        assertEquals(games.size(), actual.size());
        assertIterableEquals(games, actual);
    }

    @Test
    void getGamesReleasedAfterWithNullDate() {
        assertThrows(NullPointerException.class, () -> gameRecommender.getGamesReleasedAfter(null));
    }

    @Test
    void getGamesReleasedAfterDateWithNoData() {
        byte[] emptyStr = "".getBytes(StandardCharsets.UTF_8);
        var reader = new InputStreamReader(new ByteArrayInputStream(emptyStr));
        LocalDate date = LocalDate.of(2005, 1, 1);

        assertTrue(new GameRecommender(reader).getGamesReleasedAfter(date).isEmpty());
    }

    @Test
    void getGamesReleasedAfterUnavailableDate() {
        String message = "A date after the given does not exist in the data set";
        LocalDate farFuture = LocalDate.of(2200, 1, 1);

        assertTrue(gameRecommender.getGamesReleasedAfter(farFuture).isEmpty(), message);
    }

    @Test
    void getGamesReleasedAfter() {
        LocalDate date = LocalDate.of(2007, 1, 1);
        List<Game> gamesAfter2007 = new ArrayList<>();

        for (Game game : games) {
            if (game.releaseDate().isAfter(date)) {
                gamesAfter2007.add(game);
            }
        }

        List<Game> actual = gameRecommender.getGamesReleasedAfter(date);

        gamesAfter2007.sort(Comparator.comparing(Game::releaseDate));
        actual.sort(Comparator.comparing(Game::releaseDate));

        assertEquals(gamesAfter2007.size(), actual.size());
        assertIterableEquals(gamesAfter2007, actual);
    }

    @Test
    void getTopNUserRatedGamesWithNegativeN() {
        assertThrows(IllegalArgumentException.class, () -> gameRecommender.getTopNUserRatedGames(-1));
        assertThrows(IllegalArgumentException.class, () -> gameRecommender.getTopNUserRatedGames(-5));
        assertThrows(IllegalArgumentException.class, () -> gameRecommender.getTopNUserRatedGames(Integer.MIN_VALUE));
    }


    @Test
    void getTop3UserRatedGames() {
        List<Game> top3Games = new ArrayList<>(games);

        top3Games.sort(Comparator.comparingDouble(Game::userReview).reversed());
        top3Games = top3Games.subList(0, 3);

        List<Game> actual = gameRecommender.getTopNUserRatedGames(3);

        assertEquals(top3Games.size(), actual.size());
        assertIterableEquals(top3Games, actual);
    }

    @Test
    void getTop1UserRatedGame() {
        List<Game> top1Game = new ArrayList<>(games);
        top1Game.sort(Comparator.comparingDouble(Game::userReview).reversed());
        Game expected = top1Game.get(0);

        List<Game> actual = gameRecommender.getTopNUserRatedGames(1);

        assertEquals(1, actual.size());
        assertEquals(expected, actual.get(0));
    }

    @Test
    void getTopNUserRatedGamesWithLargeN() {
        String message = "N is larger number than the size of the available games. Should return all of them.";
        List<Game> allTopGames = new ArrayList<>(games);

        allTopGames.sort(Comparator.comparingDouble(Game::userReview).reversed());

        assertEquals(allTopGames.size(), gameRecommender.getTopNUserRatedGames(100).size(), message);
        assertIterableEquals(allTopGames, gameRecommender.getTopNUserRatedGames(100), message);
    }

    @Test
    void getTopNUserRatedGamesWithExactN() {
        String message = "N is matches exactly the size of the available games. Should return all of them.";

        List<Game> expected = new ArrayList<>(games);
        expected.sort(Comparator.comparingDouble(Game::userReview).reversed());

        assertEquals(expected.size(), gameRecommender.getTopNUserRatedGames(19).size(), message);
        assertIterableEquals(expected, gameRecommender.getTopNUserRatedGames(19), message);
    }

    @Test
    void getTopNUserRatedGamesIsUnmodifiable() {
        String message = "The collection returned should be unmodifiable.";
        Game game = new Game("", "", LocalDate.now(), "", 0, 0.0f);

        List<Game> unmodifiable = gameRecommender.getTopNUserRatedGames(5);

        assertThrows(UnsupportedOperationException.class, () -> unmodifiable.add(0, game), message);
    }

    @Test
    void getYearsWithTopScoringGamesWithUnavailableMetaScore() {
        int minMetaScore = Integer.MAX_VALUE;

        List<Integer> actual = gameRecommender.getYearsWithTopScoringGames(minMetaScore);

        assertTrue(actual.isEmpty());
    }

    @Test
    void getYearsWithTopScoringGames() {
        HashSet<Integer> yearsWithTopScoringGames = new HashSet<>();
        int minMetaScore = 50;

        for (Game game : games) {
            if (game.metaScore() > minMetaScore) {
                yearsWithTopScoringGames.add(game.releaseDate().getYear());
            }
        }

        List<Integer> actual = gameRecommender.getYearsWithTopScoringGames(minMetaScore);

        assertEquals(yearsWithTopScoringGames.size(), actual.size());

        for (Integer year : actual) {
            assertTrue(yearsWithTopScoringGames.contains(year));
            yearsWithTopScoringGames.remove(year);
        }
    }

    @Test
    void getAllNamesOfGamesReleasedInUnavailableYear() {
        int farFuture = 2200;

        String actual = gameRecommender.getAllNamesOfGamesReleasedIn(farFuture);

        assertTrue(actual.isEmpty());
    }

    @Test
    void getAllNamesOfGamesReleasedIn() {
        int year = 2000;
        List<String> names = new ArrayList<>();

        for (Game game : games) {
            if (game.releaseDate().getYear() == year) {
                names.add(game.name());
            }
        }

        String expected = String.join(", ", names);
        String actual = gameRecommender.getAllNamesOfGamesReleasedIn(year);

        assertEquals(expected, actual);
    }

    @Test
    void getHighestUserRatedGameByPlatformWithUnavailablePlatform() {
        String platform = "Unavailable-Platform";

        assertThrows(NoSuchElementException.class, () -> gameRecommender.getHighestUserRatedGameByPlatform(platform));
    }

    @Test
    void getHighestUserRatedGameByPlatform() {
        String platform = "Xbox 360";
        Game expected = null;
        Game actual = null;

        for (Game game : games) {
            if (game.platform().equals(platform)) {

                if (expected == null) {
                    expected = game;
                    continue;
                }

                if (Double.compare(game.userReview(), expected.userReview()) > 0) {
                    expected = game;
                }
            }
        }

        actual = gameRecommender.getHighestUserRatedGameByPlatform(platform);

        assertEquals(expected, actual);
    }


    @Test
    void getAllGamesByPlatform() {
        Map<String, Set<Game>> expected = new HashMap<>();
        Map<String, Set<Game>> actual = gameRecommender.getAllGamesByPlatform();

        for (Game game : games) {
            expected.putIfAbsent(game.platform(), new HashSet<>());
            expected.get(game.platform()).add(game);
        }

        assertEquals(expected.size(), actual.size());
        assertEquals(actual, expected);
    }

    @Test
    void getYearsActive() {
        String platform = "Xbox 360";
        int actual = gameRecommender.getYearsActive(platform);

        int minYear = Integer.MAX_VALUE, maxYear = 0;
        for (Game game : games) {
            if (game.platform().equals(platform)) {
                minYear = Math.min(game.releaseDate().getYear(), minYear);
                maxYear = Math.max(game.releaseDate().getYear(), maxYear);
            }
        }

        int expected = Math.abs(maxYear - minYear) + 1;

        assertEquals(expected, actual);
    }

    @Test
    void getYearsActiveWithUnavailablePlatform() {
        String platform = "Unavailable Platform";

        int actual = gameRecommender.getYearsActive(platform);

        assertEquals(0, actual);
    }

    @Test
    void getYearsActiveWithPlatformOccurringOnce() {
        String message = "Since the platform is occurring only once," +
                " the platform platform have been active for a single year";
        String platform = "PC";

        int actual = gameRecommender.getYearsActive(platform);

        assertEquals(1, actual, message);
    }

    @Test
    void getGamesSimilarTo() {
        String keyword = "is";
        List<Game> expected = new ArrayList<>();

        for (Game game : games) {
            if (game.summary().contains(keyword)) {
                expected.add(game);
            }
        }

        List<Game> actual = gameRecommender.getGamesSimilarTo(keyword);

        assertEquals(expected.size(), actual.size());
        assertEquals(expected, actual);
    }

    @Test
    void getGamesSimilarToIsUnmodifiable() {
        String keyword = "is";
        Game game = new Game("", "", LocalDate.now(), "", 0, 0);

        List<Game> unmodifiable = gameRecommender.getGamesSimilarTo(keyword);

        assertThrows(UnsupportedOperationException.class, () -> unmodifiable.add(game));
    }

    @Test
    void getGamesSimilarToWithUnavailableKeyword() {
        String keyword = "lorem ipsum";

        List<Game> actual = gameRecommender.getGamesSimilarTo(keyword);

        assertTrue(actual.isEmpty());
    }

    @Test
    void getGamesSimilarToWithNoKeywords() {
        List<Game> actual = gameRecommender.getGamesSimilarTo();

        assertTrue(actual.isEmpty());
    }

    @Test
    void getGamesSimilarToWithMoreThanOneKeyword() {
        String[] keywords = {"Super", "Grand"};
        List<Game> expected = new ArrayList<>();


        for (Game game : games) {
            if (game.summary().contains(keywords[0]) || game.summary().contains(keywords[1])) {
                expected.add(game);
            }
        }

        List<Game> actual = gameRecommender.getGamesSimilarTo(keywords);

        assertEquals(expected.size(), actual.size());
        assertEquals(expected, actual);
    }
}