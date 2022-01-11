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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LeastFrequentlyUsedCacheTest {
    private static final int ZERO = 0;
    private static final int ONE = 1;
    private static final int MIN_CAPACITY = 1;
    private static final int CAPACITY = 16;

    private Cache<Integer, Integer> lfu;

    @Mock
    private Storage<Integer, Integer> mockStorage;

    void setUpLFU(int capacity) {
        lfu = CacheFactory.getInstance(mockStorage, capacity, EvictionPolicy.LEAST_FREQUENTLY_USED);
    }

    @Test
    void testInitialSize() {
        setUpLFU(CAPACITY);

        assertEquals(0, lfu.size());
    }

    @Test
    void testGetWithNullItem() {
        setUpLFU(CAPACITY);

        assertThrows(IllegalArgumentException.class, () -> lfu.get(null));
    }

    @Test
    void testGetWithUnavailableItem() {
        setUpLFU(CAPACITY);

        when(mockStorage.retrieve(anyInt())).thenReturn(null);
        assertThrows(ItemNotFound.class, () -> lfu.get(0));
    }

    @Test
    void testGetFromPrimaryStorage() throws Exception {
        setUpLFU(CAPACITY);

        when(mockStorage.retrieve(ZERO)).thenReturn(ZERO);

        assertEquals(ZERO, lfu.get(ZERO));
    }


    @Test
    void testGetFromCache() throws Exception {
        setUpLFU(CAPACITY);

        when(mockStorage.retrieve(ZERO)).thenReturn(ZERO);

        assertEquals(ZERO, lfu.get(ZERO)); // From Primary storage
        assertEquals(1, lfu.size());
        assertEquals(ZERO, lfu.get(ZERO)); // From Cache
        assertEquals(0.5, lfu.getHitRate());
        assertTrue(lfu.values().contains(0));
    }

    @Test
    void testEvictFromCache() throws Exception {
        setUpLFU(MIN_CAPACITY);

        when(mockStorage.retrieve(ZERO)).thenReturn(ZERO);
        when(mockStorage.retrieve(ONE)).thenReturn(ONE);

        assertEquals(0, lfu.size());
        assertTrue(lfu.values().isEmpty());

        assertEquals(ZERO, lfu.get(ZERO)); // From storage

        assertEquals(ZERO, lfu.get(ZERO)); // From cache
        assertEquals(1, lfu.size()); // Cache size
        assertTrue(lfu.values().contains(ZERO));

        assertEquals(ONE, lfu.get(ONE));   // From storage + evict
        assertFalse(lfu.values().contains(ZERO));
        assertTrue(lfu.values().contains(ONE));
    }

    @Test
    void testGetHitRate() throws Exception {
        setUpLFU(MIN_CAPACITY);

        when(mockStorage.retrieve(ZERO)).thenReturn(ZERO);

        assertEquals(0, lfu.getHitRate());

        assertEquals(ZERO, lfu.get(ZERO));
        assertEquals(0, lfu.getHitRate());

        assertEquals(ZERO, lfu.get(ZERO));
        assertEquals(1.0 / 2.0, lfu.getHitRate());

        assertEquals(ZERO, lfu.get(ZERO));
        assertEquals(2.0 / 3.0, lfu.getHitRate());

        lfu.clear();
        assertTrue(lfu.values().isEmpty());
        assertEquals(0, lfu.getHitRate());
    }

    @Test
    void testLFU() throws Exception {
        setUpLFU(5);

        when(mockStorage.retrieve(1)).thenReturn(1);
        when(mockStorage.retrieve(2)).thenReturn(2);
        when(mockStorage.retrieve(3)).thenReturn(3);
        when(mockStorage.retrieve(4)).thenReturn(4);
        when(mockStorage.retrieve(5)).thenReturn(5);
        when(mockStorage.retrieve(7)).thenReturn(7);
        when(mockStorage.retrieve(8)).thenReturn(8);

        // Load 1...5 into LFU cache
        lfu.get(1);
        lfu.get(2);
        lfu.get(3);
        lfu.get(4);
        lfu.get(5);

        verify(mockStorage, times(5)).retrieve(anyInt());
        assertEquals(0, lfu.getHitRate());

        assertIterableEquals(List.of(1, 2, 3, 4, 5), lfu.values());

        lfu.get(7);
        verify(mockStorage).retrieve(7);
        assertEquals(0, lfu.getHitRate());
        assertTrue(lfu.values().contains(7));
        assertFalse(lfu.values().contains(5));

        lfu.get(1);
        lfu.get(2);
        lfu.get(3);
        lfu.get(4);

        assertEquals(lfu.getHitRate(), 4.0 / 10.0);

        lfu.get(8);

        assertTrue(lfu.values().contains(8));
        assertFalse(lfu.values().contains(7));

        assertEquals(lfu.getHitRate(), 4.0 / 11.0);
        lfu.get(8);
        assertEquals(lfu.getHitRate(), 5.0 / 12.0);
    }
}