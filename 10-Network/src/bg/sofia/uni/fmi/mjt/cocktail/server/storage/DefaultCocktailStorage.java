package bg.sofia.uni.fmi.mjt.cocktail.server.storage;

import bg.sofia.uni.fmi.mjt.cocktail.server.Cocktail;
import bg.sofia.uni.fmi.mjt.cocktail.server.Ingredient;
import bg.sofia.uni.fmi.mjt.cocktail.server.storage.exceptions.CocktailAlreadyExistsException;
import bg.sofia.uni.fmi.mjt.cocktail.server.storage.exceptions.CocktailNotFoundException;

import java.util.*;

public class DefaultCocktailStorage implements CocktailStorage {

    private final Set<Cocktail> cocktails;

    public DefaultCocktailStorage() {
        cocktails = new HashSet<>();
    }

    @Override
    public void createCocktail(Cocktail cocktail) throws CocktailAlreadyExistsException {
        Objects.requireNonNull(cocktail, "The cocktail cannot be null.");

        if (cocktails.contains(cocktail)) {
            throw new CocktailAlreadyExistsException("The cocktail already present in the storage.");
        }

        cocktails.add(cocktail);
    }

    @Override
    public Collection<Cocktail> getCocktails() {
        return Collections.unmodifiableSet(cocktails);
    }

    @Override
    public Collection<Cocktail> getCocktailsWithIngredient(String ingredientName) {
        Objects.requireNonNull(ingredientName, "The ingredient name cannot be null");

        return cocktails.stream()
                .filter(cocktail -> cocktail.ingredients().stream()
                        .map(Ingredient::name)
                        .anyMatch(ingredientName::equalsIgnoreCase))
                .toList();
    }

    @Override
    public Cocktail getCocktail(String name) throws CocktailNotFoundException {
        Objects.requireNonNull(name, "The cocktail name cannot be null.");

        for (Cocktail cocktail : cocktails) {
            if (cocktail.name().equalsIgnoreCase(name)) {
                return cocktail;
            }
        }

        throw new CocktailNotFoundException("Could not find the given cocktail in the storage.");
    }
}
