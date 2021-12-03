package bg.sofia.uni.fmi.mjt.spotify;

import bg.sofia.uni.fmi.mjt.spotify.account.Account;
import bg.sofia.uni.fmi.mjt.spotify.exceptions.AccountNotFoundException;
import bg.sofia.uni.fmi.mjt.spotify.exceptions.PlayableNotFoundException;
import bg.sofia.uni.fmi.mjt.spotify.exceptions.PlaylistCapacityExceededException;
import bg.sofia.uni.fmi.mjt.spotify.exceptions.StreamingServiceException;
import bg.sofia.uni.fmi.mjt.spotify.playable.Playable;

public class Spotify implements StreamingService {

    private static final double AD_FEE = 0.10;
    private static final double SUBSCRIPTION_FEE = 25;

    private final Account[] accounts;
    private final Playable[] playableContent;

    public Spotify(Account[] accounts, Playable[] playableContent) {
        this.accounts = accounts;
        this.playableContent = playableContent;
    }

    private Account findAccount(Account targetAccount) throws AccountNotFoundException {
        throwIfNull(targetAccount, "targetAccount");

        for (Account account : accounts) {
            if (account.equals(targetAccount)) {
                return account;
            }
        }

        throw new AccountNotFoundException("Account is not found: " + targetAccount.getEmail());
    }

    @Override
    public void play(Account account, String title) throws AccountNotFoundException, PlayableNotFoundException {
        throwIfNull(account, "account");
        throwIfNullOrEmpty(title, "title");

        Account acc = findAccount(account);
        Playable content = findByTitle(title);

        acc.listen(content);
    }

    @Override
    public void like(Account account, String title) throws AccountNotFoundException, PlayableNotFoundException, StreamingServiceException {
        throwIfNull(account, "account");
        throwIfNullOrEmpty(title, "title");

        Account acc = findAccount(account);
        Playable liked = findByTitle(title);

        try {
            acc.getLibrary().getLiked().add(liked);
        } catch (PlaylistCapacityExceededException e) {
            throw new StreamingServiceException("Failed to like playable content with title " + title, e);
        }
    }

    @Override
    public Playable findByTitle(String title) throws PlayableNotFoundException {
        throwIfNullOrEmpty(title, "title");

        for (Playable content : playableContent) {
            if (content.getTitle().equals(title)) {
                return content;
            }
        }

        throw new PlayableNotFoundException("Failed to find playable content with title " + title);
    }

    @Override
    public Playable getMostPlayed() {
        Playable mostPlayable = null;
        int maxPlays = 0;

        for (Playable playable : playableContent) {
            if (playable.getTotalPlays() > maxPlays) {
                mostPlayable = playable;
                maxPlays = playable.getTotalPlays();
            }
        }

        return maxPlays == 0 ? null : mostPlayable;
    }

    @Override
    public double getTotalListenTime() {
        double totalListenTime = 0.0;

        for (Account account : accounts) {
            totalListenTime += account.getTotalListenTime();
        }

        return totalListenTime;
    }

    @Override
    public double getTotalPlatformRevenue() {
        double totalPlatformRevenue = 0.0;

        for (Account account : accounts) {
            switch (account.getType()) {
                case FREE -> totalPlatformRevenue += account.getAdsListenedTo() * AD_FEE;
                case PREMIUM -> totalPlatformRevenue += SUBSCRIPTION_FEE;
            }
        }

        return totalPlatformRevenue;
    }

    private void throwIfNullOrEmpty(String title, String name) {
        throwIfNull(title, name);

        if (title.isEmpty()) {
            throw new IllegalArgumentException(name + " is empty!");
        }
    }

    private void throwIfNull(Object object, String varName) {
        if (object == null) {
            throw new IllegalArgumentException(varName + " is null!");
        }
    }
}
