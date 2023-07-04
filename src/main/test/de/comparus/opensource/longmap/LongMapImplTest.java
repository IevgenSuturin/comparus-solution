package de.comparus.opensource.longmap;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


public class LongMapImplTest {

    private final LongMap<Long> map = new LongMapImpl<>(16, 0.75);

    @Before
    public void setup() {
        map.clear();
        map.put(10L, 100L);
        map.put(11L, 110L);
        map.put(12L, 120L);
        map.put(13L, 130L);
        map.put(14L, 140L);
    }

    @Test
    public void testLongMapImplGetMethod() {
        Long expected = 110L;
        Long actual = map.get(11L);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testLongMapImplNotFoundByKey() {
        long notInsertedKey = 210L;
        Assert.assertThrows(IllegalArgumentException.class, () -> map.get(notInsertedKey));
    }

    @Test
    public void testNotEmptyLongMapImpl() {
        Assert.assertFalse(map.isEmpty());
    }

    @Test
    public void testEmptyLongMapImpl() {
        LongMap<Long> testMap = new LongMapImpl<>(16, 0.75);
        Assert.assertTrue(testMap.isEmpty());
    }

    @Test
    public void testNotContainsKeyLongMapImpl() {
        Assert.assertFalse(map.containsKey(210L));
    }

    @Test
    public void testContainsKeyLongMapImpl() {
        Assert.assertTrue(map.containsKey(11L));
    }

    @Test
    public void testNotContainsValueLongMapImpl() {
        Assert.assertFalse(map.containsValue(210L));
    }

    @Test
    public void testContainsValueLongMapImpl() {
        Assert.assertTrue(map.containsValue(110L));
    }

    @Test
    public void testGetKeysLongMapImpl() {
        long[] expectedKeys = new long[] {10L, 11L, 12L, 13L, 14L};
        long[] actualKeys = map.keys();

        Assert.assertArrayEquals(expectedKeys, actualKeys);
    }

    @Test
    public void testGetValuesLongMapImpl() {
        Long[] expectedValues = new Long[] {100L, 110L, 120L, 130L, 140L};
        Long[] actualValues = map.values().toArray(new Long[0]);

        Assert.assertArrayEquals(expectedValues, actualValues);
    }


    @Test
    public void testGetSizeLongMapImpl() {
        long expectedSize = 5;
        long actualSize = map.size();

        Assert.assertEquals(expectedSize, actualSize);
    }

    @Test
    public void testClearLongMapImpl() {
        map.clear();
        long expectedSize = 0;
        long actualSize = map.size();

        Assert.assertEquals(expectedSize, actualSize);
    }
}
