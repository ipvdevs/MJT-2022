package bg.sofia.uni.fmi.mjt.cocktail.server.command;

import java.util.Arrays;

public class CommandParser {
    private static final String WHITESPACE_PATTERN = "\s+";

    private static final String DISCONNECT = "disconnect";
    private static final String CREATE = "create";
    private static final String GET = "get";

    public static Command of(String line) {
        String[] tokens = line.split(WHITESPACE_PATTERN);

        if (tokens.length == 0) {
            return new Command(CommandType.UNKNOWN);
        }

        String command = tokens[0];

        if (command.equals(DISCONNECT)) {
            return new Command(CommandType.DISCONNECT);
        }

        if (command.equals(CREATE) && tokens.length >= 3) {
            String[] args = Arrays.copyOfRange(tokens, 1, tokens.length);

            boolean isFormatValid =
                    Arrays.stream(args)
                            .skip(1) // Skips Cocktail name
                            .allMatch(CommandParser::validateIngredientFormat);

            if (isFormatValid) {
                return new Command(CommandType.CREATE, args);
            }

        } else if (command.equals(GET) && tokens.length >= 2) {
            String getType = tokens[1];

            if (getType.equals("all") && tokens.length == 2) {
                return new Command(CommandType.GET_ALL);
            }

            if (getType.equals("by-name") && tokens.length == 3) {
                String cocktailName = tokens[2];
                return new Command(CommandType.GET_BY_NAME, cocktailName);
            }

            if (getType.equals("by-ingredient") && tokens.length == 3) {
                String ingredient = tokens[2];
                return new Command(CommandType.GET_BY_INGREDIENT, ingredient);
            }
        }

        return new Command(CommandType.UNKNOWN);
    }

    private static boolean validateIngredientFormat(String str) {
        return str.split("=").length == 2;
    }
}
