package bg.sofia.uni.fmi.mjt.gifts.service;

import bg.sofia.uni.fmi.mjt.gifts.gift.BirthdayGift;
import bg.sofia.uni.fmi.mjt.gifts.gift.Gift;
import bg.sofia.uni.fmi.mjt.gifts.gift.Priceable;
import bg.sofia.uni.fmi.mjt.gifts.person.Person;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class DefaultPackingService<T extends Priceable> implements PackingService<T> {
    @Override
    public Gift<T> pack(Person<?> sender, Person<?> receiver, T item) {
        if (sender == null) {
            throw new IllegalArgumentException("Sender cannot be null!");
        }

        if (receiver == null) {
            throw new IllegalArgumentException("Receiver cannot be null!");
        }

        if (item == null) {
            throw new IllegalArgumentException("Item cannot be null!");
        }

        return new BirthdayGift<>(sender, receiver, List.of(item));
    }

    @Override
    public Gift<T> pack(Person<?> sender, Person<?> receiver, T... items) {
        if (sender == null) {
            throw new IllegalArgumentException("Sender cannot be null!");
        }

        if (receiver == null) {
            throw new IllegalArgumentException("Receiver cannot be null!");
        }

        for (T gift : items) {
            if (gift == null) {
                throw new IllegalArgumentException("Item cannot be null!");
            }
        }

        return new BirthdayGift<>(sender, receiver, List.of(items));
    }

    @Override
    public Collection<T> unpack(Gift<T> gift) {
        if (gift == null) {
            throw new IllegalArgumentException("Gift cannot be null!");
        }

        return gift.getItems();
    }
}
