package bg.sofia.uni.fmi.mjt.boardgames.recommender;

import bg.sofia.uni.fmi.mjt.boardgames.BoardGame;
import bg.sofia.uni.fmi.mjt.boardgames.utils.BoardGameInitializer;
import bg.sofia.uni.fmi.mjt.boardgames.utils.StopwordsInitializer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BoardGamesRecommenderTest {
    private static final int MONOPOLY_INDEX = 0;
    private static final int CATAN_INDEX = 1;
    private static final int CARCASSONNE_INDEX = 2;
    private static final int STAY_AWAY_INDEX = 3;
    private static final int BELOTE_INDEX = 4;

    private static List<BoardGame> boardGames;
    private static BoardGamesRecommender recommender;

    @BeforeAll
    static void setUp() {
        boardGames = new BufferedReader(
                BoardGameInitializer.initBoardGamesStream())
                .lines()
                .skip(1)
                .map(BoardGame::of)
                .toList();

        recommender = new BoardGamesRecommender(
                BoardGameInitializer.initBoardGamesStream(),
                StopwordsInitializer.initStopwords());
    }

    @Test
    void loadZipFile() {
        Path zip = Path.of("data.zip");
        String file = "data.csv";
        Path stopwords = Path.of("stopwords.txt");

        var recommender = new BoardGamesRecommender(zip, file, stopwords);

        assertTrue(recommender.getGames().isEmpty());
    }

    @Test
    void getGames() {
        String message = "Count of loaded games do not match.";

        int expected = boardGames.size();
        int actual = recommender.getGames().size();

        assertEquals(expected, actual, message);
    }

    @Test
    void getGamesIsUnmodifiable() {
        String message = "The returned collection should be unmodifiable.";

        assertThrows(UnsupportedOperationException.class, () -> recommender.getGames().clear(), message);
    }

    @Test
    void getSimilarToNegativeN() {
        String message = "A negative N is not allowed.";

        assertThrows(
                IllegalArgumentException.class,
                () -> recommender.getSimilarTo(boardGames.get(MONOPOLY_INDEX), -10),
                message
        );
    }

    @Test
    void getSimilarToGreaterN() {
        String message = "The provided N is greater than existing elements. Only one game should be included.";

        String game = "1;1;1;1;M;1;A;A;game";
        String exclude = "2;2;2;2;M;1;B;B;gameExclude";
        String other = "3;300;300;300;M;1;A, B;A;gameOther";

        int expectedSize = 1;
        BoardGame expectedGame = BoardGame.of(other);

        BoardGamesRecommender temp = new BoardGamesRecommender(
                new StringReader(String.format("skipline%n%s%n%s%n%s%n", game, exclude, other)),
                StopwordsInitializer.initStopwords());

        List<BoardGame> actual = temp.getSimilarTo(BoardGame.of(game), 100);

        assertEquals(expectedSize, actual.size(), message);
        assertEquals(expectedGame, actual.get(0), message);
    }

    @Test
    void getSimilarToExclude() {
        String message = "The two games do not share common categories.";

        String game = "1;1;1;1;M;1;A;A;game";
        String exclude = "3;100;100;100;M;1;B;B;gameExclude";

        BoardGamesRecommender temp = new BoardGamesRecommender(
                new StringReader(String.format("skipline%n%s%n%s", game, exclude)),
                StopwordsInitializer.initStopwords());

        int actual = temp.getSimilarTo(BoardGame.of(game), 1).size();
        int expected = 0;

        assertEquals(expected, actual, message);
    }

    @Test
    void getSimilarToOrder() {
        String game = "1;1;1;1;M;1;A;A;game";

        String close = "2;2;2;2;M;1;A;A;gameClose";
        String far = "3;100;100;100;M;1;A;A;gameFar";
        String farAway = "3;300;300;300;M;1;A;A;gameFarAway";

        BoardGame expectedFirst = BoardGame.of(close);
        BoardGame expectedSecond = BoardGame.of(far);
        BoardGame expectedThird = BoardGame.of(farAway);

        BoardGamesRecommender temp = new BoardGamesRecommender(
                new StringReader(String.format("skipline%n%s%n%s%n%s%n%s%n", game, close, far, farAway)),
                StopwordsInitializer.initStopwords());

        List<BoardGame> similar = temp.getSimilarTo(BoardGame.of(game), 3);

        BoardGame actualFirst = similar.get(0);
        BoardGame actualSecond = similar.get(1);
        BoardGame actualThird = similar.get(2);

        assertEquals(3, similar.size(), "There are only 3 other games");
        assertEquals(expectedFirst, actualFirst, "Invalid sort order");
        assertEquals(expectedSecond, actualSecond, "Invalid sort order");
        assertEquals(expectedThird, actualThird, "Invalid sort order");
    }


    @Test
    void getByDescriptionWithSingleKeyword() {
        String message = "Only one game (id = 3) has expedition in its description.";

        List<BoardGame> actual = recommender.getByDescription("expedition");

        BoardGame expectedGame = boardGames.get(STAY_AWAY_INDEX);
        BoardGame actualGame = actual.get(0);

        int expectedSize = 1;
        int actualSize = actual.size();

        assertEquals(expectedSize, actualSize, message);
        assertEquals(expectedGame, actualGame, message);
    }


    @Test
    void getByDescriptionWithSingleKeywordLetterCase() {
        String message = "Letter case should be ignored";

        List<BoardGame> actual1 = recommender.getByDescription("expedition");
        List<BoardGame> actual2 = recommender.getByDescription("Expedition");
        List<BoardGame> actual3 = recommender.getByDescription("ExPeDiTiON");

        BoardGame expectedGame = boardGames.get(STAY_AWAY_INDEX);

        int expectedSize = 1;

        assertEquals(expectedSize, actual1.size(), message);
        assertEquals(expectedSize, actual2.size(), message);
        assertEquals(expectedSize, actual3.size(), message);

        assertEquals(expectedGame, actual1.get(0), message);
        assertEquals(expectedGame, actual2.get(0), message);
        assertEquals(expectedGame, actual3.get(0), message);
    }

    @Test
    void getByDescriptionWithMultipleKeywords() {
        List<BoardGame> actual = recommender.getByDescription("France", "Monopoly");
        List<BoardGame> expected = List.of(boardGames.get(BELOTE_INDEX), boardGames.get(MONOPOLY_INDEX));

        assertEquals(expected.size(), actual.size(), "Count of games searched by \"France\" and \"Monopoly\" does not match.");
        assertTrue(expected.contains(boardGames.get(BELOTE_INDEX)), "Game with id 5 has \"France\" in its description");
        assertTrue(expected.contains(boardGames.get(MONOPOLY_INDEX)), "Game with id 1 has \"Monopoly\" in its description");
    }

    @Test
    void getByDescriptionOrder() {
        String message = "Games with id {1, 2, 3, 5} contain \"game\" and game with id {1} contains \"classic\"";

        String[] keywords = {"game", "classic"};

        List<BoardGame> actual = recommender.getByDescription(keywords);

        int expectedSize = boardGames.size() - 1;
        BoardGame actualFirstGame = actual.get(0);
        BoardGame expectedFirstGame = boardGames.get(MONOPOLY_INDEX);

        assertEquals(expectedSize, actual.size(), message);
        assertEquals(expectedFirstGame, actualFirstGame, message);
        assertFalse(actual.contains(boardGames.get(STAY_AWAY_INDEX)), message);
    }

    @Test
    void getByDescriptionWithInvalidKeyword() {
        String message = "Games with \"LoremIpsum\" in description should not exist.";

        int actual = recommender.getByDescription("LoremIpsum").size();

        assertEquals(0, actual, message);
    }

    @Test
    void storeGamesIndex() {
        String stopwordLine = "all a the of an";
        String game1 = "1;1;1;1;M;1;A;A;game1 " + stopwordLine;
        String game2 = "2;1;1;1;M;1;A;A;game2 " + stopwordLine;

        StringReader games = new StringReader(String.format("skipline%n%s%n%s%n", game1, game2));
        StringReader stopwords = StopwordsInitializer.initStopwords();

        BoardGamesRecommender temp = new BoardGamesRecommender(games, stopwords);
        StringWriter stringWriter = new StringWriter();
        temp.storeGamesIndex(stringWriter);

        String actual = stringWriter.toString();
        String expected = String.format("game1: 1%ngame2: 2");

        assertEquals(actual, expected);
    }

}