package bg.sofia.uni.fmi.mjt.twitch.content;

import bg.sofia.uni.fmi.mjt.twitch.validations.CommonValidations;
import bg.sofia.uni.fmi.mjt.twitch.user.User;

public abstract class AbstractContent implements Content {
    private static final String USER_VAR_NAME = "user";

    private final ContentType type;
    private final Metadata metadata;
    protected int numberOfViews;

    public AbstractContent(ContentType type, Metadata metadata) {
        this.type = type;
        this.metadata = metadata;
    }

    @Override
    public void startWatching(User user) {
        CommonValidations.throwIfNull(user, USER_VAR_NAME);

        ++numberOfViews;
    }

    @Override
    public void stopWatching(User user) {
        if (numberOfViews > 0) {
            CommonValidations.throwIfNull(user, USER_VAR_NAME);
        }
    }

    @Override
    public Metadata getMetadata() {
        return metadata;
    }

    @Override
    public int getNumberOfViews() {
        return numberOfViews;
    }

}
