package bg.sofia.uni.fmi.mjt.investment.wallet.acquisition;

import bg.sofia.uni.fmi.mjt.investment.wallet.asset.Asset;

import java.time.LocalDateTime;

public class AcquisitionDefault implements Acquisition {

    private final Asset asset;
    private final double price;
    private final int quantity;
    private final LocalDateTime createdOn;

    public AcquisitionDefault(double price, int quantity, Asset asset) {
        this.price = price;
        this.quantity = quantity;
        this.asset = asset;
        createdOn = LocalDateTime.now();
    }

    @Override
    public double getPrice() {
        return price;
    }

    @Override
    public int getQuantity() {
        return quantity;
    }

    @Override
    public Asset getAsset() {
        return asset;
    }

    @Override
    public LocalDateTime getTimestamp() {
        return createdOn;
    }
}
