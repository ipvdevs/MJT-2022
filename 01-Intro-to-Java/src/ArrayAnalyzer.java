public class ArrayAnalyzer {
    public static boolean isMountainArray(int[] array) {
        if (array.length < 3) return false;

        boolean incr = true;

        for (int i = 0; i < array.length - 1; i++) {
            if (incr) {
                if (array[i] > array[i + 1]) {
                    incr = false;
                }
            } else if (array[i] < array[i + 1]) {
                return false;
            }

            // The array must be strictly increasing
            if (array[i] == array[i + 1])
                return false;
        }

        return true;
    }
}
