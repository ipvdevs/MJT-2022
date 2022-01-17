package bg.sofia.uni.fmi.mjt.cocktail.server;

public class Respond {

    public static final String OK = "OK";
    public static final String CREATED = "CREATED";
    public static final String ERROR = "ERROR";

    private Respond() {

    }

    public static String createRespond(String status, String... args) {
        StringBuilder respond = new StringBuilder(String.format("{\"status\":\"%s\"", status));

        if (args.length == 2) {
            respond.append(",");
            if (status.equals(ERROR)) {
                respond.append(String.format("\"%s\":\"%s\"", args[0], args[1]));
            } else {
                respond.append(String.format("\"%s\":%s", args[0], args[1]));
            }
        }

        return respond.append("}").toString();
    }
}
