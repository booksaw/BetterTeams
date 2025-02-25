package util;

import com.booksaw.betterTeams.util.Cache;
import org.junit.Test;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class CacheTest {

	@Test
	public void testBasicCacheOperations() {
		AtomicInteger loaderCallCount = new AtomicInteger(0);

		Cache<String, String> cache = new Cache.Builder<String, String>()
			.loader(key -> {
				loaderCallCount.incrementAndGet();
				return "value-" + key;
			})
			.maximumSize(100)
			.build();

		// First call should invoke loader
		assertEquals("value-key1", cache.get("key1"));
		assertEquals(1, loaderCallCount.get());

		// Second call should use cached value
		assertEquals("value-key1", cache.get("key1"));
		assertEquals(1, loaderCallCount.get());

		// Different key should invoke loader again
		assertEquals("value-key2", cache.get("key2"));
		assertEquals(2, loaderCallCount.get());
	}

	@Test
	public void testExpireAfterWrite() throws InterruptedException {
		AtomicInteger loaderCallCount = new AtomicInteger(0);

		Cache<String, String> cache = new Cache.Builder<String, String>()
			.loader(key -> {
				loaderCallCount.incrementAndGet();
				return "value-" + key;
			})
			.expireAfterWrite(100, TimeUnit.MILLISECONDS)
			.build();

		// First call should invoke loader
		assertEquals("value-key1", cache.get("key1"));
		assertEquals(1, loaderCallCount.get());

		// Second call before expiration should use cached value
		assertEquals("value-key1", cache.get("key1"));
		assertEquals(1, loaderCallCount.get());

		// Wait for expiration
		Thread.sleep(150);

		// Call after expiration should invoke loader again
		assertEquals("value-key1", cache.get("key1"));
		assertEquals(2, loaderCallCount.get());
	}

	@Test
	public void testExpireAfterAccess() throws InterruptedException {
		AtomicInteger loaderCallCount = new AtomicInteger(0);

		Cache<String, String> cache = new Cache.Builder<String, String>()
			.loader(key -> {
				loaderCallCount.incrementAndGet();
				return "value-" + key;
			})
			.expireAfterAccess(100, TimeUnit.MILLISECONDS)
			.build();

		// First call should invoke loader
		assertEquals("value-key1", cache.get("key1"));
		assertEquals(1, loaderCallCount.get());

		// Wait some time but not enough for expiration
		Thread.sleep(50);

		// Access again to reset timer
		assertEquals("value-key1", cache.get("key1"));
		assertEquals(1, loaderCallCount.get());

		// Wait some more time but total since last access < expiration
		Thread.sleep(50);

		// Should still use cached value
		assertEquals("value-key1", cache.get("key1"));
		assertEquals(1, loaderCallCount.get());

		// Wait for expiration since last access
		Thread.sleep(150);

		// Call after expiration should invoke loader again
		assertEquals("value-key1", cache.get("key1"));
		assertEquals(2, loaderCallCount.get());
	}

	@Test
	public void testMaximumSize() {
		AtomicInteger loaderCallCount = new AtomicInteger(0);

		Cache<Integer, String> cache = new Cache.Builder<Integer, String>()
			.loader(key -> {
				loaderCallCount.incrementAndGet();
				return "value-" + key;
			})
			.maximumSize(2)
			.build();

		// Fill the cache
		assertEquals("value-1", cache.get(1));
		assertEquals("value-2", cache.get(2));
		assertEquals(2, loaderCallCount.get());
		assertEquals(2, cache.size());

		// Access an existing entry
		assertEquals("value-1", cache.get(1));

		// Add a new entry that should evict the least recently used (key 2)
		assertEquals("value-3", cache.get(3));
		assertEquals(3, loaderCallCount.get());
		assertEquals(2, cache.size()); // Size should still be 2

		// Key 1 should be in cache (recently accessed)
		assertEquals("value-1", cache.get(1));
		assertEquals(3, loaderCallCount.get());

		// Key 2 should have been evicted
		assertEquals("value-2", cache.get(2));
		assertEquals(4, loaderCallCount.get());
	}

	@Test
	public void testInvalidate() {
		AtomicInteger loaderCallCount = new AtomicInteger(0);

		Cache<String, String> cache = new Cache.Builder<String, String>()
			.loader(key -> {
				loaderCallCount.incrementAndGet();
				return "value-" + key;
			})
			.build();

		// Add entries
		assertEquals("value-key1", cache.get("key1"));
		assertEquals("value-key2", cache.get("key2"));
		assertEquals(2, loaderCallCount.get());

		// Invalidate one entry
		cache.invalidate("key1");

		// Next get should invoke loader again for invalidated key
		assertEquals("value-key1", cache.get("key1"));
		assertEquals(3, loaderCallCount.get());

		// Non-invalidated key should still be cached
		assertEquals("value-key2", cache.get("key2"));
		assertEquals(3, loaderCallCount.get());
	}

	@Test
	public void testInvalidateAll() {
		AtomicInteger loaderCallCount = new AtomicInteger(0);

		Cache<String, String> cache = new Cache.Builder<String, String>()
			.loader(key -> {
				loaderCallCount.incrementAndGet();
				return "value-" + key;
			})
			.build();

		// Add entries
		assertEquals("value-key1", cache.get("key1"));
		assertEquals("value-key2", cache.get("key2"));
		assertEquals(2, loaderCallCount.get());

		// Invalidate all entries
		cache.invalidateAll();
		assertEquals(0, cache.size());

		// Next gets should invoke loader again
		assertEquals("value-key1", cache.get("key1"));
		assertEquals("value-key2", cache.get("key2"));
		assertEquals(4, loaderCallCount.get());
	}

	@Test
	public void testConcurrentAccess() throws InterruptedException {
		AtomicInteger loaderCallCount = new AtomicInteger(0);

		Cache<String, String> cache = new Cache.Builder<String, String>()
			.loader(key -> {
				// Add some delay to increase chance of concurrent issues
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					// Ignore
				}
				loaderCallCount.incrementAndGet();
				return "value-" + key;
			})
			.build();

		// Create multiple threads that access the same key
		int threadCount = 10;
		Thread[] threads = new Thread[threadCount];

		for (int i = 0; i < threadCount; i++) {
			threads[i] = new Thread(() -> {
				assertEquals("value-shared", cache.get("shared"));
			});
		}

		// Start all threads
		for (Thread thread : threads) {
			thread.start();
		}

		// Wait for all threads to complete
		for (Thread thread : threads) {
			thread.join();
		}

		// Should only call the loader once despite multiple threads
		assertEquals(1, loaderCallCount.get());
	}

	@Test
	public void testBothExpirationStrategies() throws InterruptedException {
		AtomicInteger loaderCallCount = new AtomicInteger(0);

		Cache<String, String> cache = new Cache.Builder<String, String>()
			.loader(key -> {
				loaderCallCount.incrementAndGet();
				return "value-" + key;
			})
			.expireAfterWrite(200, TimeUnit.MILLISECONDS)
			.expireAfterAccess(100, TimeUnit.MILLISECONDS)
			.build();

		// First call should invoke loader
		assertEquals("value-key1", cache.get("key1"));
		assertEquals(1, loaderCallCount.get());

		// Access within access expiration time
		Thread.sleep(50);
		assertEquals("value-key1", cache.get("key1"));
		assertEquals(1, loaderCallCount.get());

		// Wait for access expiration but not write expiration
		Thread.sleep(150);

		// Should expire due to access expiration
		assertEquals("value-key1", cache.get("key1"));
		assertEquals(2, loaderCallCount.get());

		// Test write expiration with frequent access
		assertEquals("value-key2", cache.get("key2"));
		assertEquals(3, loaderCallCount.get());

		// Access frequently to prevent access expiration
		for (int i = 0; i < 5; i++) {
			Thread.sleep(50);
			assertEquals("value-key2", cache.get("key2"));
		}
		assertEquals(3, loaderCallCount.get());

		// But wait for write expiration
		Thread.sleep(100);
		assertEquals("value-key2", cache.get("key2"));
		assertEquals(4, loaderCallCount.get());
	}

	@Test
	public void testNullValues() {
		AtomicInteger loaderCallCount = new AtomicInteger(0);

		Cache<String, String> cache = new Cache.Builder<String, String>()
			.loader(key -> {
				loaderCallCount.incrementAndGet();
				return key.equals("nullKey") ? null : "value-" + key;
			})
			.build();

		// Test null value
		assertNull(cache.get("nullKey"));
		assertEquals(1, loaderCallCount.get());

		// Second call should still invoke loader since null values aren't cached
		assertNull(cache.get("nullKey"));
		assertEquals(2, loaderCallCount.get());

		// Non-null value should be cached
		assertEquals("value-key1", cache.get("key1"));
		assertEquals("value-key1", cache.get("key1"));
		assertEquals(3, loaderCallCount.get());
	}
}