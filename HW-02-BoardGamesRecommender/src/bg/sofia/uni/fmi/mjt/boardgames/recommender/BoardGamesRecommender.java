package bg.sofia.uni.fmi.mjt.boardgames.recommender;

import bg.sofia.uni.fmi.mjt.boardgames.BoardGame;
import bg.sofia.uni.fmi.mjt.boardgames.exception.BoardGamesRecommenderException;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class BoardGamesRecommender implements Recommender {
    private final GameIndexer indexer;
    private Set<BoardGame> repository;

    /**
     * Constructs an instance using the provided file names.
     *
     * @param datasetZipFile  ZIP file containing the board games dataset file
     * @param datasetFileName the name of the dataset file (inside the ZIP archive)
     * @param stopwordsFile   the stopwords file
     */
    public BoardGamesRecommender(Path datasetZipFile, String datasetFileName, Path stopwordsFile) {
        try (ZipFile zipFile = new ZipFile(datasetZipFile.toString())) {
            ZipEntry zipEntry = zipFile.getEntry(datasetFileName);
            InputStream is = zipFile.getInputStream(zipEntry);

            initializeRepository(new InputStreamReader(is, StandardCharsets.UTF_8));
            indexer = new GameIndexer(stopwordsFile);
            indexer.importKeywords(repository);

        } catch (IOException e) {
            throw new BoardGamesRecommenderException("Could not load dataset.", e);
        }
    }

    /**
     * Constructs an instance using the provided Reader streams.
     *
     * @param dataset   Reader from which the dataset can be read
     * @param stopwords Reader from which the stopwords list can be read
     */
    public BoardGamesRecommender(Reader dataset, Reader stopwords) {
        initializeRepository(dataset);
        indexer = new GameIndexer(stopwords);
        indexer.importKeywords(repository);
    }

    @Override
    public Collection<BoardGame> getGames() {
        return Collections.unmodifiableCollection(repository);
    }

    @Override
    public List<BoardGame> getSimilarTo(BoardGame game, int n) {
        Objects.requireNonNull(game, "Game is null.");

        if (n < 0) {
            throw new IllegalArgumentException("N should be a non-negative number.");
        }

        List<BoardGame> filtered = repository
                .stream()
                .filter(other -> !game.equals(other))
                .filter(other ->
                        other.categories()
                                .stream()
                                .anyMatch(category -> game.categories().contains(category)))
                .toList();

        Map<BoardGame, Double> distances = new HashMap<>();

        for (BoardGame otherGame : filtered) {
            distances.put(otherGame, KthNearestNeighbors.getDistance(game, otherGame));
        }

        return filtered.stream()
                .sorted(Comparator.comparing(distances::get))
                .limit(n)
                .toList();
    }

    @Override
    public List<BoardGame> getByDescription(String... keywords) {
        return indexer.collect(keywords);
    }

    @Override
    public void storeGamesIndex(Writer writer) {
        Objects.requireNonNull(writer, "Writer is null.");

        indexer.write(writer);
    }

    /**
     * Reads data from specified reader and adds
     * all the data in {@link BoardGamesRecommender#repository}
     *
     * @param reader The reader from which the data is read.
     */
    private void initializeRepository(Reader reader) {
        Objects.requireNonNull(reader, "Reader is null.");

        try (var br = new BufferedReader(reader)) {
            this.repository = br.lines()
                    .skip(1)
                    .map(BoardGame::of)
                    .collect(Collectors.toSet());

        } catch (IOException e) {
            throw new BoardGamesRecommenderException("Could not initialize game repository.", e);
        }
    }

}
