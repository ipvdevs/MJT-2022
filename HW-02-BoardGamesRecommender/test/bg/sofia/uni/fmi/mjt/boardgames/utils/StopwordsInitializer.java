package bg.sofia.uni.fmi.mjt.boardgames.utils;

import java.io.StringReader;
import java.util.Arrays;
import java.util.stream.Collectors;

public class StopwordsInitializer {
    private static final String[] stopwords = {"a", "the", "of", "an", "or", "can", "all"};

    public static StringReader initStopwords() {
        return new StringReader(
                Arrays.stream(stopwords).collect(Collectors.joining(System.lineSeparator()))
        );
    }
}
