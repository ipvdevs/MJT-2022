package bg.sofia.uni.fmi.mjt.investment.wallet;

import bg.sofia.uni.fmi.mjt.investment.wallet.acquisition.Acquisition;
import bg.sofia.uni.fmi.mjt.investment.wallet.acquisition.AcquisitionDefault;
import bg.sofia.uni.fmi.mjt.investment.wallet.asset.Asset;
import bg.sofia.uni.fmi.mjt.investment.wallet.exception.InsufficientResourcesException;
import bg.sofia.uni.fmi.mjt.investment.wallet.exception.OfferPriceException;
import bg.sofia.uni.fmi.mjt.investment.wallet.exception.UnknownAssetException;
import bg.sofia.uni.fmi.mjt.investment.wallet.exception.WalletException;
import bg.sofia.uni.fmi.mjt.investment.wallet.quote.Quote;
import bg.sofia.uni.fmi.mjt.investment.wallet.quote.QuoteService;

import java.util.*;

public class InvestmentWallet implements Wallet {

    private final List<Acquisition> acquisitions;
    private final Map<Asset, Integer> assetQuantities;
    private final QuoteService quoteService;

    private double balance;

    public InvestmentWallet(QuoteService quoteService) {
        this.quoteService = quoteService;
        this.acquisitions = new ArrayList<>();
        this.assetQuantities = new HashMap<>();
        this.balance = 0;
    }

    @Override
    public double deposit(double cash) {
        throwIfNegative(cash, "Cash");

        balance += cash;
        return balance;
    }

    @Override
    public double withdraw(double cash) throws InsufficientResourcesException {
        throwIfNegative(cash, "Cash");

        if (balance < cash) {
            throw new InsufficientResourcesException("Cannot withdraw" + cash + "Insufficient balance: " + balance);
        }

        balance -= cash;
        return balance;
    }

    @Override
    public Acquisition buy(Asset asset, int quantity, double maxPrice) throws WalletException {
        throwIfNegative(quantity, "Quantity");
        throwIfNegative(maxPrice, "Max price");
        throwIfNull(asset, "Asset");

        Quote quote = quoteService.getQuote(asset);

        if (quote == null) {
            throw new UnknownAssetException("There is no defined quote for " + asset.getName());
        }

        if (quote.askPrice() > maxPrice) {
            throw new OfferPriceException("The ask price is higher than the maximum price.");
        }

        double bill = quote.askPrice() * quantity;

        if (balance < bill) {
            throw new InsufficientResourcesException("There is not enough balance for the transaction.");
        }

        balance -= bill;
        Acquisition acquisition = new AcquisitionDefault(quote.askPrice(), quantity, asset);
        acquisitions.add(acquisition);

        int ownedQuantityOfAsset = assetQuantities.getOrDefault(asset, 0);
        assetQuantities.put(asset, ownedQuantityOfAsset + quantity);

        return acquisition;
    }

    @Override
    public double sell(Asset asset, int quantity, double minPrice) throws WalletException {
        throwIfNull(asset, "Asset");
        throwIfNegative(quantity, "Quantity");
        throwIfNegative(minPrice, "Min price");

        String assetName = asset.getName();
        if (!assetQuantities.containsKey(asset) || assetQuantities.get(asset) < quantity) {
            throw new InsufficientResourcesException("Not enough quantity of " + assetName);
        }

        Quote quote = quoteService.getQuote(asset);

        if (quote == null) {
            throw new UnknownAssetException("There is no defined quote for " + assetName);
        }

        double bidPrice = quote.bidPrice();
        if (minPrice > bidPrice) {
            throw new OfferPriceException("The bid price is lower than the minimum price.");
        }

        double profit = bidPrice * quantity;
        balance += profit;

        int ownedQuantityOfAsset = assetQuantities.get(asset);

        if (ownedQuantityOfAsset == 0) {
            assetQuantities.remove(asset);
        } else {
            assetQuantities.put(asset, ownedQuantityOfAsset - quantity);
        }

        return profit;
    }

    @Override
    public double getValuation() {
        double valuation = 0.0;

        for (Asset asset : assetQuantities.keySet()) {
            try {
                valuation += getValuation(asset);
            } catch (UnknownAssetException e) {
                valuation = 0.0;
            }
        }

        return valuation;
    }

    @Override
    public double getValuation(Asset asset) throws UnknownAssetException {
        throwIfNull(asset, "Asset");

        if (!assetQuantities.containsKey(asset)) {
            throw new UnknownAssetException(asset.getName() + " is not available in the wallet.");
        }

        Quote quote = quoteService.getQuote(asset);
        if (quote == null) {
            throw new UnknownAssetException("There is no defined quote for " + asset.getName());
        }

        double bidPrice = quote.bidPrice();

        return bidPrice * assetQuantities.get(asset);
    }

    @Override
    public Asset getMostValuableAsset() {
        Asset mostValuableAsset = null;

        for (Asset asset : assetQuantities.keySet()) {
            if (mostValuableAsset == null) {
                mostValuableAsset = asset;
                continue;
            }

            try {
                if (getValuation(asset) > getValuation(mostValuableAsset)) {
                    mostValuableAsset = asset;
                }
            } catch (UnknownAssetException e) {
                mostValuableAsset = null;
                break;
            }
        }

        return mostValuableAsset;
    }

    @Override
    public Collection<Acquisition> getAllAcquisitions() {
        return List.copyOf(acquisitions);
    }

    @Override
    public Set<Acquisition> getLastNAcquisitions(int n) {
        throwIfNegative(n, "N");

        if (n > acquisitions.size()) {
            return Set.copyOf(acquisitions);
        }

        // Get last n elements
        int end = acquisitions.size();
        int beg = end - n;
        return Set.copyOf(acquisitions.subList(beg, end));
    }

    private void throwIfNull(Object obj, String varName) {
        if (obj == null) {
            throw new IllegalArgumentException(varName + "is null!");
        }
    }

    private void throwIfNegative(double param, String varName) {
        if (param < 0.0) {
            throw new IllegalArgumentException(varName + " should not be negative!");
        }
    }

}
