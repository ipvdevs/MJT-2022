package bg.sofia.uni.fmi.mjt.cocktail.server;

import java.util.Objects;

public record Ingredient(String name, String amount) {
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Ingredient that = (Ingredient) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    public static Ingredient of(String line) {
        String[] split = line.split("=");

        String name = split[0];
        String amount = split[1];

        return new Ingredient(name, amount);
    }
}
