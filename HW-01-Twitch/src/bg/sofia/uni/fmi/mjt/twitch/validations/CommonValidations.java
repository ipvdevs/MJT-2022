package bg.sofia.uni.fmi.mjt.twitch.validations;

public final class CommonValidations {

    public static void throwIfNullOrEmpty(String string, String varName) {
        throwIfNull(string, varName);

        if (string.isEmpty()) {
            throw new IllegalArgumentException(varName + " is empty!");
        }
    }

    public static void throwIfNull(Object obj, String varName) {
        if (obj == null) {
            throw new IllegalArgumentException(varName + " cannot be null!");
        }
    }
}
