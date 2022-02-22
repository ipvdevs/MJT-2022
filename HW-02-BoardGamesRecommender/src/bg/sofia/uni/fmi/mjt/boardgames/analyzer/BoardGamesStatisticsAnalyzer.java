package bg.sofia.uni.fmi.mjt.boardgames.analyzer;

import bg.sofia.uni.fmi.mjt.boardgames.BoardGame;

import java.util.*;
import java.util.stream.Collectors;

public class BoardGamesStatisticsAnalyzer implements StatisticsAnalyzer {
    private final double averageMinAge;

    private List<String> categoriesPopularityStats = new ArrayList<>();
    private Map<String, Double> averagePlayingTimeStats = new HashMap<>();

    public BoardGamesStatisticsAnalyzer(Collection<BoardGame> games) {
        this.averageMinAge = games.stream()
                .mapToDouble(BoardGame::minAge)
                .average()
                .orElse(0.0);

        precomputeCategoryStats(games);
    }

    @Override
    public List<String> getNMostPopularCategories(int n) {
        if (n < 0) {
            throw new IllegalArgumentException("N is a negative number");
        }

        n = Math.min(n, categoriesPopularityStats.size());

        return Collections.unmodifiableList(categoriesPopularityStats.subList(0, n));
    }

    @Override
    public double getAverageMinAge() {
        return averageMinAge;
    }

    @Override
    public double getAveragePlayingTimeByCategory(String category) {
        Objects.requireNonNull(category, "The provided category is null.");

        return averagePlayingTimeStats.getOrDefault(category, 0.0);
    }

    @Override
    public Map<String, Double> getAveragePlayingTimeByCategory() {
        return Collections.unmodifiableMap(averagePlayingTimeStats);
    }

    private void precomputeCategoryStats(Collection<BoardGame> games) {
        Map<String, Set<BoardGame>> categories = new HashMap<>();

        for (BoardGame game : games) {
            game.categories().forEach(
                    category -> {
                        categories.putIfAbsent(category, new HashSet<>());
                        categories.get(category).add(game);
                    }
            );
        }

        this.categoriesPopularityStats = new ArrayList<>(categories.keySet());
        this.categoriesPopularityStats.sort(Comparator.comparingInt(lhs -> categories.get(lhs).size()).reversed());

        this.averagePlayingTimeStats = categories.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().stream()
                                .mapToDouble(BoardGame::playingTimeMins)
                                .average()
                                .orElse(0.0)));
    }
}
