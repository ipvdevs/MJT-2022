package bg.sofia.uni.fmi.mjt.game.recommender;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public record Game(String name,
                   String platform,
                   LocalDate releaseDate,
                   String summary,
                   int metaScore,
                   double userReview) {

    public static Game of(String string) {
        final int nameToken = 0;
        final int platformToken = 1;
        final int releaseDateToken = 2;
        final int summaryToken = 3;
        final int metaScoreToken = 4;
        final int userReviewToken = 5;

        String[] tokens = string.split(",");

        return new Game(
                tokens[nameToken],
                tokens[platformToken],
                LocalDate.parse(tokens[releaseDateToken], DateTimeFormatter.ofPattern("dd-LLL-yyyy")),
                tokens[summaryToken],
                Integer.parseInt(tokens[metaScoreToken]),
                Double.parseDouble(tokens[userReviewToken]));
    }

}
