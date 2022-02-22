package bg.sofia.uni.fmi.mjt.boardgames.recommender;

import bg.sofia.uni.fmi.mjt.boardgames.BoardGame;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class KthNearestNeighbors {

    public static double getDistance(BoardGame lhs, BoardGame rhs) {
        long[] deltaList = {
                lhs.playingTimeMins() - rhs.playingTimeMins(),
                lhs.maxPlayers() - rhs.maxPlayers(),
                lhs.minAge() - rhs.minAge(),
                lhs.minPlayers() - rhs.minPlayers()
        };

        double distanceEuclid = euclideanDistance(deltaList);
        int powerA = setsCommonPower(lhs.categories(), rhs.categories());
        int powerB = setsCommonPower(lhs.mechanics(), rhs.mechanics());

        return distanceEuclid + powerA + powerB;
    }

    /**
     * Calculates the power difference between union(lhs, rhs) and intersection(lhs, rhs)
     *
     * @param lhs The set A
     * @param rhs The set B
     * @return |A ∪ B| - |A ∩ B|
     */
    private static int setsCommonPower(Collection<String> lhs, Collection<String> rhs) {
        Set<String> union = new HashSet<>(lhs);
        union.addAll(rhs);

        Set<String> intersection = new HashSet<>(lhs);
        intersection.retainAll(rhs);

        return union.size() - intersection.size();
    }

    /**
     * Calculates the Euclidean distance by provided deltas using the generalized formula
     * for n-dimensional Euclidean space.
     *
     * @param deltaList The list of deltas used for the calculation
     * @return the Euclidean distance of the given deltas
     */
    private static double euclideanDistance(long[] deltaList) {
        Objects.requireNonNull(deltaList, "Listed deltas are null.");

        long powSum = 0;
        for (long delta : deltaList) {
            powSum += delta * delta;
        }

        return Math.sqrt(powSum);
    }

}
