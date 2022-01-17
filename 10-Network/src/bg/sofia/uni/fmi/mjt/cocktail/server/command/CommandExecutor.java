package bg.sofia.uni.fmi.mjt.cocktail.server.command;

import bg.sofia.uni.fmi.mjt.cocktail.server.Cocktail;
import bg.sofia.uni.fmi.mjt.cocktail.server.Ingredient;
import bg.sofia.uni.fmi.mjt.cocktail.server.Respond;
import bg.sofia.uni.fmi.mjt.cocktail.server.storage.CocktailStorage;
import bg.sofia.uni.fmi.mjt.cocktail.server.storage.DefaultCocktailStorage;
import bg.sofia.uni.fmi.mjt.cocktail.server.storage.exceptions.CocktailAlreadyExistsException;
import bg.sofia.uni.fmi.mjt.cocktail.server.storage.exceptions.CocktailNotFoundException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public class CommandExecutor {
    private static final Gson gson = new Gson();
    private static final CocktailStorage storage = new DefaultCocktailStorage();

    private CommandExecutor() {

    }

    public static String execute(Command command) {
        return switch (command.type()) {
            case CREATE -> create(command.args());
            case GET_ALL -> getAll();
            case GET_BY_NAME -> getByName(command.args());
            case GET_BY_INGREDIENT -> getByIngredient(command.args());
            case DISCONNECT -> disconnect();
            default -> unknown();
        };
    }

    private static String getByIngredient(String... args) {
        if (args.length != 1) {
            return unknown();
        }

        synchronized (CommandExecutor.class) {
            String ingredient = args[0];

            return Respond.createRespond(
                    Respond.OK,
                    "cocktails",
                    gson.toJson(storage.getCocktailsWithIngredient(ingredient)));
        }
    }

    private static String getByName(String... args) {
        if (args.length != 1) {
            return unknown();
        }

        synchronized (CommandExecutor.class) {
            try {
                Cocktail cocktail = storage.getCocktail(args[0]);

                return Respond.createRespond(Respond.OK, "cocktail", gson.toJson(cocktail));
            } catch (CocktailNotFoundException e) {
                return Respond.createRespond(Respond.ERROR, "errorMessage", e.getMessage());
            }
        }
    }

    private static String getAll() {
        synchronized (CommandExecutor.class) {
            return Respond.createRespond(Respond.OK, "cocktails", gson.toJson(storage.getCocktails()));
        }
    }

    private static String create(String... args) {
        if (args == null || args.length < 2) {
            throw new IllegalArgumentException("Invalid state of arguments.");
        }

        String cocktailName = args[0];
        Set<Ingredient> ingredients = Arrays.stream(args)
                .skip(1) // cocktail name
                .map(Ingredient::of)
                .collect(Collectors.toSet());

        Cocktail cocktail = new Cocktail(cocktailName, ingredients);

        synchronized (CommandExecutor.class) {
            try {
                storage.createCocktail(cocktail);

                return Respond.createRespond(Respond.CREATED);
            } catch (CocktailAlreadyExistsException e) {
                return Respond.createRespond(Respond.ERROR, "errorMessage", e.getMessage());
            }
        }
    }

    private static String unknown() {
        return "Unknown command";
    }

    private static String disconnect() {
        return "Disconnected from the server";
    }

}
