package bg.sofia.uni.fmi.mjt.gifts.gift;


import bg.sofia.uni.fmi.mjt.gifts.person.Person;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class BirthdayGift<T extends Priceable> implements Gift<T> {
    private final Person<?> sender;
    private final Person<?> receiver;
    private final Set<T> items;

    public BirthdayGift(Person<?> sender, Person<?> receiver, Collection<T> items) {
        this.sender = sender;
        this.receiver = receiver;
        this.items = new HashSet<>(items);
    }

    @Override
    public Person<?> getSender() {
        return sender;
    }

    @Override
    public Person<?> getReceiver() {
        return receiver;
    }

    @Override
    public double getPrice() {
        double totalPrice = 0.0;

        for (T gift : items) {
            totalPrice += gift.getPrice();
        }

        return totalPrice;
    }

    @Override
    public void addItem(T t) {
        if (t == null) {
            throw new IllegalArgumentException("The item cannot be null!");
        }

        items.add(t);
    }

    @Override
    public boolean removeItem(T t) {
        if (t == null) {
            // The item cannot be null!
            return false;
        }

        return items.remove(t);
    }

    @Override
    public Collection<T> getItems() {
        return Set.copyOf(items);
    }

    @Override
    public T getMostExpensiveItem() {
        T mostExpensiveGift = null;

        for (T gift : items) {
            if (mostExpensiveGift == null || gift.getPrice() > mostExpensiveGift.getPrice()) {
                mostExpensiveGift = gift;
            }
        }

        return mostExpensiveGift;
    }
}
