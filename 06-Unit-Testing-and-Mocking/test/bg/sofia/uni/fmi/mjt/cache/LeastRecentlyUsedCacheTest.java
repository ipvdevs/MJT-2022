package bg.sofia.uni.fmi.mjt.cache;

import bg.sofia.uni.fmi.mjt.cache.exception.ItemNotFound;
import bg.sofia.uni.fmi.mjt.cache.factory.CacheFactory;
import bg.sofia.uni.fmi.mjt.cache.factory.EvictionPolicy;
import bg.sofia.uni.fmi.mjt.cache.storage.Storage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LeastRecentlyUsedCacheTest {
    private static final int ZERO = 0;
    private static final int ONE = 1;
    private static final int MIN_CAPACITY = 1;
    private static final int CAPACITY = 16;

    private Cache<Integer, Integer> lru;

    @Mock
    private Storage<Integer, Integer> mockStorage;

    void setUpLRU(int capacity) {
        lru = CacheFactory.getInstance(mockStorage, capacity, EvictionPolicy.LEAST_RECENTLY_USED);
    }

    @Test
    void testInitialSize() {
        setUpLRU(CAPACITY);

        assertEquals(0, lru.size());
    }

    @Test
    void testGetWithNullItem() {
        setUpLRU(CAPACITY);

        assertThrows(IllegalArgumentException.class, () -> lru.get(null));
    }

    @Test
    void testGetWithUnavailableItem() {
        setUpLRU(CAPACITY);

        when(mockStorage.retrieve(anyInt())).thenReturn(null);
        assertThrows(ItemNotFound.class, () -> lru.get(ZERO));
    }

    @Test
    void testGetFromPrimaryStorage() throws Exception {
        setUpLRU(CAPACITY);

        when(mockStorage.retrieve(ZERO)).thenReturn(ZERO);

        assertEquals(ZERO, lru.get(ZERO));
    }

    @Test
    void testGetFromCache() throws Exception {
        setUpLRU(CAPACITY);

        when(mockStorage.retrieve(ZERO)).thenReturn(ZERO);

        assertEquals(ZERO, lru.get(ZERO)); // From Primary storage
        assertEquals(1, lru.size());
        assertEquals(ZERO, lru.get(ZERO)); // From Cache
        assertEquals(0.5, lru.getHitRate());
        assertTrue(lru.values().contains(0));
    }

    @Test
    void testEvictFromCache() throws Exception {
        setUpLRU(MIN_CAPACITY);

        when(mockStorage.retrieve(ZERO)).thenReturn(ZERO);
        when(mockStorage.retrieve(ONE)).thenReturn(ONE);

        assertEquals(0, lru.size());
        assertTrue(lru.values().isEmpty());

        assertEquals(ZERO, lru.get(ZERO)); // From storage

        assertEquals(ZERO, lru.get(ZERO)); // From cache
        assertEquals(1, lru.size());       // Cache size
        assertEquals(ZERO, lru.get(ZERO)); // From Cache

        assertTrue(lru.values().contains(ZERO));
        assertEquals(ONE, lru.get(ONE));   // From storage and evict
        assertFalse(lru.values().contains(ZERO));
        assertTrue(lru.values().contains(ONE));
    }

    @Test
    void testGetHitRate() throws Exception {
        setUpLRU(MIN_CAPACITY);

        when(mockStorage.retrieve(ZERO)).thenReturn(ZERO);

        assertEquals(0, lru.getHitRate());

        assertEquals(ZERO, lru.get(ZERO));
        assertEquals(0, lru.getHitRate());

        assertEquals(ZERO, lru.get(ZERO));
        assertEquals(1.0 / 2.0, lru.getHitRate());

        assertEquals(ZERO, lru.get(ZERO));
        assertEquals(2.0 / 3.0, lru.getHitRate());

        lru.clear();
        assertTrue(lru.values().isEmpty());
        assertEquals(0, lru.getHitRate());
    }

    @Test
    void testLRU() throws Exception {
        setUpLRU(5);

        when(mockStorage.retrieve(1)).thenReturn(1);
        when(mockStorage.retrieve(2)).thenReturn(2);
        when(mockStorage.retrieve(3)).thenReturn(3);
        when(mockStorage.retrieve(4)).thenReturn(4);
        when(mockStorage.retrieve(5)).thenReturn(5);
        when(mockStorage.retrieve(7)).thenReturn(7);
        when(mockStorage.retrieve(8)).thenReturn(8);

        lru.get(1);
        lru.get(2);
        lru.get(3);
        lru.get(4);
        lru.get(5);

        assertIterableEquals(List.of(1, 2, 3, 4, 5), lru.values());

        lru.get(1);
        assertIterableEquals(List.of(2, 3, 4, 5, 1), lru.values());

        lru.get(7);
        assertIterableEquals(List.of(3, 4, 5, 1, 7), lru.values());

        lru.get(8);
        assertIterableEquals(List.of(4, 5, 1, 7, 8), lru.values());

        lru.get(4);
        assertIterableEquals(List.of(5, 1, 7, 8, 4), lru.values());

        assertEquals(2.0/ 9.0, lru.getHitRate());
    }
}