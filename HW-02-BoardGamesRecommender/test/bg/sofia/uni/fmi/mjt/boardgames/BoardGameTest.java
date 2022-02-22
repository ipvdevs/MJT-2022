package bg.sofia.uni.fmi.mjt.boardgames;

import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BoardGameTest {

    @Test
    void of() {
        String message = "Line was not parsed as expected.";
        String line = "8;5;12;2;Lords of Creation;120;Civilization,Fantasy;Modular Board;DESCRIPTION";
        BoardGame expected = new BoardGame(8,
                "Lords of Creation",
                "DESCRIPTION",
                5,
                12,
                2,
                120,
                Set.of("Civilization", "Fantasy"),
                Set.of("Modular Board"));

        BoardGame actual = BoardGame.of(line);

        assertEquals(expected, actual, message);
    }
}