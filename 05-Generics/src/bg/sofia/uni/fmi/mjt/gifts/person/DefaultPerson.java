package bg.sofia.uni.fmi.mjt.gifts.person;

import bg.sofia.uni.fmi.mjt.gifts.exception.WrongReceiverException;
import bg.sofia.uni.fmi.mjt.gifts.gift.Gift;

import java.util.*;

public class DefaultPerson<I> implements Person<I> {
    private final I id;
    private final List<Gift<?>> receivedGifts;

    public DefaultPerson(I id) {
        this.id = id;
        receivedGifts = new ArrayList<>();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DefaultPerson<?> that = (DefaultPerson<?>) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public Collection<Gift<?>> getNMostExpensiveReceivedGifts(int n) {
        if (n < 0) {
            throw new IllegalArgumentException("N must be a non-negative integer!");
        }

        receivedGifts.sort(Comparator.comparingDouble(Gift::getPrice));

        if (n >= receivedGifts.size()) {
            return List.copyOf(receivedGifts);
        }

        return List.copyOf(receivedGifts.subList(receivedGifts.size() - n, receivedGifts.size()));
    }

    @Override
    public Collection<Gift<?>> getGiftsBy(Person<?> person) {
        if (person == null) {
            throw new IllegalArgumentException("Person cannot be null!");
        }

        List<Gift<?>> giftsFromPerson = new ArrayList<>();

        for (Gift<?> receivedGift : receivedGifts) {
            if (receivedGift.getSender().equals(person)) {
                giftsFromPerson.add(receivedGift);
            }
        }

        return List.copyOf(giftsFromPerson);
    }

    @Override
    public I getId() {
        return id;
    }

    @Override
    public void receiveGift(Gift<?> gift) {
        if (gift == null) {
            throw new IllegalArgumentException("The received gift cannot be null!");
        }

        if (!gift.getReceiver().getId().equals(id)) {
            throw new WrongReceiverException("The received gift has different receiver!");
        }

        receivedGifts.add(gift);
    }
}
