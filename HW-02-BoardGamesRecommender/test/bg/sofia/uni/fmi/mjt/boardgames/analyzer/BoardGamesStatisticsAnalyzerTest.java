package bg.sofia.uni.fmi.mjt.boardgames.analyzer;

import bg.sofia.uni.fmi.mjt.boardgames.BoardGame;
import bg.sofia.uni.fmi.mjt.boardgames.utils.BoardGameInitializer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;


class BoardGamesStatisticsAnalyzerTest {
    private static final int COUNT_OF_ALL_CATEGORIES = 11;

    private static List<BoardGame> games;
    private static BoardGamesStatisticsAnalyzer analyzer;

    @BeforeAll
    static void setUp() {
        games = new BufferedReader(
                BoardGameInitializer.initBoardGamesStream())
                .lines()
                .skip(1)
                .map(BoardGame::of)
                .toList();

        analyzer = new BoardGamesStatisticsAnalyzer(games);
    }

    @Test
    void getTheMostPopularCategory() {
        String message = "Only the top 1 category is requested.";

        List<String> actual = analyzer.getNMostPopularCategories(1);

        String expected = "Negotiation";

        assertEquals(1, actual.size(), message);
        assertEquals(expected, actual.get(0), message);
    }

    @Test
    void getNMostPopularCategoriesWithGreaterN() {
        String message = "N is greater than the count of all available categories. All of them should be returned.";

        List<String> actual = analyzer.getNMostPopularCategories(100).stream().distinct().toList();

        assertEquals(COUNT_OF_ALL_CATEGORIES, actual.size(), message);
    }

    @Test
    void getNMostPopularCategoriesWithExactN() {
        String message = "The requested size of top categories is matching exactly the count of all categories.";

        List<String> actual = analyzer.getNMostPopularCategories(COUNT_OF_ALL_CATEGORIES).stream().distinct().toList();

        assertEquals(COUNT_OF_ALL_CATEGORIES, actual.size(), message);
    }

    @Test
    void getNMostPopularCategoriesWithNegativeN() {
        String message = "N is a negative number.";

        assertThrows(IllegalArgumentException.class, () -> analyzer.getNMostPopularCategories(-10), message);
        assertThrows(IllegalArgumentException.class, () -> analyzer.getNMostPopularCategories(Integer.MIN_VALUE), message);
    }

    @Test
    void getThe3MostPopularCategories() {
        String message = "There are only two categories";

        BoardGame game1 = BoardGame.of("1;1;1;1;M;1;Card Game;A;game1" + System.lineSeparator());
        BoardGame game2 = BoardGame.of("2;1;1;1;M;1;Card Game;A;game1" + System.lineSeparator());
        BoardGame game3 = BoardGame.of("3;1;1;1;M;1;Strategy;A;game1" + System.lineSeparator());

        BoardGamesStatisticsAnalyzer temp = new BoardGamesStatisticsAnalyzer(List.of(game1, game2, game3));

        List<String> actual = temp.getNMostPopularCategories(2);

        assertEquals(2, actual.size(), message);
        assertEquals("Card Game", actual.get(0));
        assertEquals("Strategy", actual.get(1));
    }

    @Test
    void getMostPopularCategoriesWithNoGames() {
        String message = "There are no games available.";

        BoardGamesStatisticsAnalyzer temp = new BoardGamesStatisticsAnalyzer(Collections.emptyList());

        List<String> actual = temp.getNMostPopularCategories(5);

        assertTrue(actual.isEmpty(), message);
    }

    @Test
    void getAverageMinAgeWithNoGames() {
        String message = "There are no available games. Average should be 0.0";
        BoardGamesStatisticsAnalyzer temp = new BoardGamesStatisticsAnalyzer(Collections.emptyList());

        double actual = temp.getAverageMinAge();

        assertEquals(0.0, actual, message);
    }

    @Test
    void getAverageMinAge() {
        double expected = 11.0;

        double actual = analyzer.getAverageMinAge();

        assertEquals(expected, actual);
    }

    @Test
    void getAveragePlayingTimeByCategory() {
        String category = "Card Game";
        double expected = 40.0;

        double actual = analyzer.getAveragePlayingTimeByCategory(category);

        assertEquals(expected, actual);
    }

    @Test
    void getAveragePlayingTimeByInvalidCategory() {
        String category = "Lorem Ipsum";
        double expected = 0.0;

        double actual = analyzer.getAveragePlayingTimeByCategory(category);

        assertEquals(expected, actual);
    }


    @Test
    void getAveragePlayingTimeByCategoryAll() {
        BoardGame game1 = BoardGame.of("1;1;1;1;M;1;Card Game;A;game1" + System.lineSeparator());
        BoardGame game2 = BoardGame.of("2;1;1;1;M;2;Card Game;A;game1" + System.lineSeparator());
        BoardGame game3 = BoardGame.of("3;1;1;1;M;3;Strategy;A;game1" + System.lineSeparator());

        BoardGamesStatisticsAnalyzer temp = new BoardGamesStatisticsAnalyzer(List.of(game1, game2, game3));

        Map<String, Double> actual = temp.getAveragePlayingTimeByCategory();

        assertEquals(2, actual.size());
        assertEquals(1.5, actual.get("Card Game"));
        assertEquals(3, actual.get("Strategy"));
    }
}