package bg.sofia.uni.fmi.mjt.investment.wallet.asset;

import java.util.Objects;

public abstract class AbstractAsset implements Asset {

    private final String id;
    private final String name;
    private final AssetType type;

    AbstractAsset(String id, String name, AssetType type) {
        this.id = id;
        this.name = name;
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AbstractAsset that = (AbstractAsset) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public AssetType getType() {
        return type;
    }

}
