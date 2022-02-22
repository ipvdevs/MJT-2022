package bg.sofia.uni.fmi.mjt.boardgames.recommender;

import bg.sofia.uni.fmi.mjt.boardgames.BoardGame;
import bg.sofia.uni.fmi.mjt.boardgames.exception.GameIndexerException;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

public class GameIndexer {
    private final static String PATTERN = "[\\p{IsPunctuation}\\p{IsWhite_Space}]+";

    private final Map<String, Set<BoardGame>> index = new HashMap<>();

    private Set<String> stopwords = new HashSet<>();

    public GameIndexer(Path stopwordsFile) {
        Objects.requireNonNull(stopwordsFile, "Stop words file path is null.");

        try (var br = Files.newBufferedReader(stopwordsFile)) {
            readStopwords(br);
        } catch (IOException e) {
            throw new GameIndexerException("Could not read stopwords.", e);
        }
    }

    public GameIndexer(Reader stopwords) {
        Objects.requireNonNull(stopwords, "Stop words reader is null.");

        readStopwords(stopwords);
    }

    void readStopwords(Reader reader) {
        Objects.requireNonNull(reader, "Reader is null.");

        try (var br = new BufferedReader(reader)) {
            this.stopwords = br.lines().collect(Collectors.toSet());
        } catch (IOException e) {
            throw new GameIndexerException("Could not read stopwords.", e);
        }
    }

    public void importKeywords(Collection<BoardGame> games) {
        Objects.requireNonNull(games, "The collection of boardgames is null.");

        for (BoardGame game : games) {
            List<String> keywords =
                    Arrays.stream(game.description().split(PATTERN))
                            .map(String::toLowerCase)
                            .filter(word -> !(stopwords.contains(word) || word.isBlank()))
                            .toList();

            keywords.forEach(keyword -> {
                index.putIfAbsent(keyword, new HashSet<>());
                index.get(keyword).add(game);
            });
        }
    }

    public void write(Writer writer) {
        Objects.requireNonNull(writer, "Writer is null");

        try (var bw = new BufferedWriter(writer)) {

            bw.write(index.entrySet().stream()
                    .map(entry -> {
                        String keyword = entry.getKey();
                        String gameIndices = entry.getValue()
                                .stream()
                                .map((game) -> String.valueOf(game.id()))
                                .collect(Collectors.joining(", "));

                        return keyword + ": " + gameIndices;
                    }).collect(Collectors.joining(System.lineSeparator())));
            bw.flush();

        } catch (IOException e) {
            throw new GameIndexerException("Could not write the index.", e);
        }
    }

    public List<BoardGame> collect(String[] keywords) {
        Objects.requireNonNull(keywords, "The listed keywords are invalid.");

        Set<BoardGame> allMatching = new HashSet<>();
        Map<BoardGame, Integer> matchingCount = new HashMap<>();

        for (String keyword : keywords) {
            Set<BoardGame> matching = index.get(keyword.toLowerCase());

            if (matching != null) {
                matching.forEach((game) -> {
                    matchingCount.putIfAbsent(game, 0);
                    matchingCount.put(game, matchingCount.get(game) + 1);
                });

                allMatching.addAll(matching);
            }
        }

        Comparator<BoardGame> byIncreasingOccurrences = Comparator.comparing(matchingCount::get);

        return allMatching.stream()
                .sorted(byIncreasingOccurrences.reversed())
                .toList();
    }
}
