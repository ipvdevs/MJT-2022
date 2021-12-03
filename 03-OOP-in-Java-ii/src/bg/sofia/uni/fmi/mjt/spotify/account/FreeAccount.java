package bg.sofia.uni.fmi.mjt.spotify.account;

import bg.sofia.uni.fmi.mjt.spotify.library.Library;

public class FreeAccount extends Account {
    private static final int MEDIA_PLAYED_PER_ADD = 5;

    public FreeAccount(String email, Library library) {
        super(email, library);
    }

    @Override
    public int getAdsListenedTo() {
        return getTotalMediaPlayed() / MEDIA_PLAYED_PER_ADD;
    }

    @Override
    public AccountType getType() {
        return AccountType.FREE;
    }
}
