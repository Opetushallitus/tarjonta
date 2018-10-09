package fi.vm.sade.tarjonta.service.impl.resources.v1.util;

import com.google.common.base.Ticker;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AutoRefreshableCacheTest {

    private AutoRefreshableCache<String> cache;
    private Ticker tickerMock;
    private final String KEY1 = "key1";
    private final String KEY2 = "key2";
    private final int REFRESH_PERIOD_IN_MINUTES = AutoRefreshableCache.REFRESH_PERIOD_IN_MINUTES;

    @Before
    public void setUp() throws Exception {
        tickerMock = mock(Ticker.class);
        setTimeToMinutes(0);
        cache = new AutoRefreshableCache<>();
        cache.initializeCache(tickerMock);
    }

    @Test
    public void cachesTheValue() throws ExecutionException {
        final String value = "my value";

        cache.get(KEY1, () -> value);

        String storedValue = cache.getCache().getIfPresent(KEY1);
        assertNotNull(storedValue);
        assertEquals(value, storedValue);
    }

    @Test
    public void refreshesAutomaticallyExactlyWhenRefreshPeriodForEntryExpires() throws ExecutionException {
        final long startTimeInMinutes = 3;
        setTimeToMinutes(startTimeInMinutes);
        final String firstBackendValue = "starting value";
        String firstValue = getValue(KEY1, firstBackendValue);
        setTimeToMinutes(REFRESH_PERIOD_IN_MINUTES + startTimeInMinutes - 1);
        final String newValue = "new value";
        String stillStartingValue = getValue(KEY1, newValue); // newValue will be ignored because cache is not yet refreshed
        setTimeToMinutes(REFRESH_PERIOD_IN_MINUTES + startTimeInMinutes + 1); // at this point there should be already a refresh

        String automaticallyRefreshedValue = cache.getIfPresent(KEY1); // now it should be newValue because refresh was done at 5 minutes

        assertEquals(firstBackendValue, firstValue);
        assertEquals(firstBackendValue, stillStartingValue);
        assertNotNull(automaticallyRefreshedValue);
        assertEquals(newValue, automaticallyRefreshedValue);
    }

    @Test
    public void invalidateRemovesAllIfThereAreNoKeepers() throws ExecutionException {
        getValue(KEY1, "value");
        assertNotNull(cache.getIfPresent(KEY1));

        cache.invalidateAll();

        assertNull(cache.getIfPresent(KEY1));
        assertEquals(0, cache.getCache().size());
    }

    @Test
    public void keepersWillBeKeptAndRefreshedAfterInvalidate() throws ExecutionException, InterruptedException {
        final String keepersNewValue = "new_value";
        getValue(KEY1, "Keeper's old value");
        getValue(KEY2, "value2");
        cache.markAsKeeper(KEY1);
        getValue(KEY1, keepersNewValue);
        assertEquals(2, cache.getCache().size());

        cache.invalidateAll();
        Thread.sleep(100);

        assertEquals(1, cache.getCache().size());
        assertNull("Non-keeper is deleted", cache.getIfPresent(KEY2));
        assertNotNull("Keeper is there (again)", cache.getIfPresent(KEY1));
        assertEquals("Keeper has the most recent value", keepersNewValue, cache.getIfPresent(KEY1));
    }

    @Test
    public void markAsKeeperDirectlyInGetMethod() throws InterruptedException {
        cache.get(KEY1, () -> "value", true);
        cache.get(KEY2, () -> "value", false);
        assertEquals(2, cache.getCache().size());

        cache.invalidateAll();
        Thread.sleep(100);

        assertEquals(1, cache.getCache().size());
    }

    @Test
    public void markAsKeeperDirectlyInGetMethodStaysKeeperForever() throws InterruptedException {
        cache.get(KEY1, () -> "value", true);
        cache.get(KEY1, () -> "value but no keeper flag");

        cache.invalidateAll();
        Thread.sleep(100);

        assertEquals("The entry should stay, even though the second call did not contain flag", 1, cache.getCache().size());
    }

    private String getValue(String key, String backendValue) throws ExecutionException {
        Callable<String> loader = () -> backendValue;
        return cache.get(key, loader);
    }

    private void setTimeToMinutes(long minutes) {
        when(tickerMock.read()).thenReturn(TimeUnit.MINUTES.toNanos(minutes));
    }
}