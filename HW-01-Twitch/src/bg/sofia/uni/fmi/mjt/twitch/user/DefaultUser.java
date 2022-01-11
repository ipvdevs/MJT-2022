package bg.sofia.uni.fmi.mjt.twitch.user;

import java.util.Objects;

public class DefaultUser implements User {
    private final String name;
    private UserStatus status;

    public DefaultUser(String name) {
        this.name = name;
        this.status = UserStatus.OFFLINE;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DefaultUser that = (DefaultUser) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public UserStatus getStatus() {
        return status;
    }

    @Override
    public void setStatus(UserStatus status) {
        this.status = status;
    }
}
