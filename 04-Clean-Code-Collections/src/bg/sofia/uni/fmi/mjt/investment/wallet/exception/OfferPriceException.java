package bg.sofia.uni.fmi.mjt.investment.wallet.exception;

public class OfferPriceException extends WalletException {
    public OfferPriceException() {
    }

    public OfferPriceException(String message) {
        super(message);
    }

    public OfferPriceException(String message, Throwable cause) {
        super(message, cause);
    }

    public OfferPriceException(Throwable cause) {
        super(cause);
    }
}

