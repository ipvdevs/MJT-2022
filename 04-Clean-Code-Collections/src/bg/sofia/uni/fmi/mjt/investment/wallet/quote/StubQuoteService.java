package bg.sofia.uni.fmi.mjt.investment.wallet.quote;

import bg.sofia.uni.fmi.mjt.investment.wallet.asset.Asset;

import java.util.Map;

public class StubQuoteService implements QuoteService {
    private final Map<Asset, Quote> database;

    public StubQuoteService(Map<Asset, Quote> database) {
        this.database = database;
    }

    @Override
    public Quote getQuote(Asset asset) {
        if (asset == null) {
            throw new IllegalArgumentException("asset is null!");
        }

        return database.getOrDefault(asset, null);
    }
}
