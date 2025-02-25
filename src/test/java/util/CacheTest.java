package util;

import com.booksaw.betterTeams.util.Cache;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

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
		assertThat(cache.get("key1")).isEqualTo("value-key1");
		assertThat(loaderCallCount.get()).isEqualTo(1);

		// Second call should use cached value
		assertThat(cache.get("key1")).isEqualTo("value-key1");
		assertThat(loaderCallCount.get()).isEqualTo(1);

		// Different key should invoke loader again
		assertThat(cache.get("key2")).isEqualTo("value-key2");
		assertThat(loaderCallCount.get()).isEqualTo(2);
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
		assertThat(cache.get("key1")).isEqualTo("value-key1");
		assertThat(loaderCallCount.get()).isEqualTo(1);

		// Second call before expiration should use cached value
		assertThat(cache.get("key1")).isEqualTo("value-key1");
		assertThat(loaderCallCount.get()).isEqualTo(1);

		// Wait for expiration
		Thread.sleep(150);

		// Call after expiration should invoke loader again
		assertThat(cache.get("key1")).isEqualTo("value-key1");
		assertThat(loaderCallCount.get()).isEqualTo(2);
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
		assertThat(cache.get("key1")).isEqualTo("value-key1");
		assertThat(loaderCallCount.get()).isEqualTo(1);

		// Wait some time but not enough for expiration
		Thread.sleep(50);

		// Access again to reset timer
		assertThat(cache.get("key1")).isEqualTo("value-key1");
		assertThat(loaderCallCount.get()).isEqualTo(1);

		// Wait some more time but total since last access < expiration
		Thread.sleep(50);

		// Should still use cached value
		assertThat(cache.get("key1")).isEqualTo("value-key1");
		assertThat(loaderCallCount.get()).isEqualTo(1);

		// Wait for expiration since last access
		Thread.sleep(150);

		// Call after expiration should invoke loader again
		assertThat(cache.get("key1")).isEqualTo("value-key1");
		assertThat(loaderCallCount.get()).isEqualTo(2);
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
		assertThat(cache.get(1)).isEqualTo("value-1");
		assertThat(cache.get(2)).isEqualTo("value-2");
		assertThat(loaderCallCount.get()).isEqualTo(2);
		assertThat(cache.size()).isEqualTo(2);

		// Access an existing entry
		assertThat(cache.get(1)).isEqualTo("value-1");

		// Add a new entry that should evict the least recently used (key 2)
		assertThat(cache.get(3)).isEqualTo("value-3");
		assertThat(loaderCallCount.get()).isEqualTo(3);
		assertThat(cache.size()).isEqualTo(2); // Size should still be 2

		// Key 1 should be in cache (recently accessed)
		assertThat(cache.get(1)).isEqualTo("value-1");
		assertThat(loaderCallCount.get()).isEqualTo(3);

		// Key 2 should have been evicted
		assertThat(cache.get(2)).isEqualTo("value-2");
		assertThat(loaderCallCount.get()).isEqualTo(4);
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
		assertThat(cache.get("key1")).isEqualTo("value-key1");
		assertThat(cache.get("key2")).isEqualTo("value-key2");
		assertThat(loaderCallCount.get()).isEqualTo(2);

		// Invalidate one entry
		cache.invalidate("key1");

		// Next get should invoke loader again for invalidated key
		assertThat(cache.get("key1")).isEqualTo("value-key1");
		assertThat(loaderCallCount.get()).isEqualTo(3);

		// Non-invalidated key should still be cached
		assertThat(cache.get("key2")).isEqualTo("value-key2");
		assertThat(loaderCallCount.get()).isEqualTo(3);
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
		assertThat(cache.get("key1")).isEqualTo("value-key1");
		assertThat(cache.get("key2")).isEqualTo("value-key2");
		assertThat(loaderCallCount.get()).isEqualTo(2);

		// Invalidate all entries
		cache.invalidateAll();
		assertThat(cache.size()).isEqualTo(0);

		// Next gets should invoke loader again
		assertThat(cache.get("key1")).isEqualTo("value-key1");
		assertThat(cache.get("key2")).isEqualTo("value-key2");
		assertThat(loaderCallCount.get()).isEqualTo(4);
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
				assertThat(cache.get("shared")).isEqualTo("value-shared");
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
		assertThat(loaderCallCount.get()).isEqualTo(1);
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
		assertThat(cache.get("key1")).isEqualTo("value-key1");
		assertThat(loaderCallCount.get()).isEqualTo(1);

		assertThat(cache.get("key2")).isEqualTo("value-key2");
		assertThat(loaderCallCount.get()).isEqualTo(2);

		// Access within access expiration time
		Thread.sleep(50); // running total: 50ms
		assertThat(cache.get("key1")).isEqualTo("value-key1");
		assertThat(loaderCallCount.get()).isEqualTo(2);

		// Wait for access expiration but not write expiration
		Thread.sleep(100); // running total: 150ms
		// Should expire due to access expiration
		assertThat(cache.get("key1")).isEqualTo("value-key1");
		assertThat(loaderCallCount.get()).isEqualTo(3);

		assertThat(cache.get("key2")).isEqualTo("value-key2");
		assertThat(loaderCallCount.get()).isEqualTo(3);

		// Now the 2nd key expired due to write access
		Thread.sleep(70); // running total: 220ms
		assertThat(cache.get("key2")).isEqualTo("value-key2");
		// running total reset to 0
		assertThat(loaderCallCount.get()).isEqualTo(4);

		// Access frequently to prevent access expiration
		for (int i = 0; i < 5; i++) { // Here it should just reload once due to write expiration
			Thread.sleep(50);
			assertThat(cache.get("key2")).isEqualTo("value-key2");
		}
		assertThat(loaderCallCount.get()).isEqualTo(5);
	}

	@Test
	public void testNullValues() {
		AtomicInteger loaderCallCount = new AtomicInteger(0);

		Cache<String, String> cache = new Cache.Builder<String, String>()
			.loader(key -> {
				loaderCallCount.incrementAndGet();
				return key.equals("nullKey") ? null : key;
			})
			.build();

		// Test null value
		assertThat(cache.get("nullKey")).isNull();
		assertThat(loaderCallCount.get()).isEqualTo(1);

		// Second call should still invoke loader since null values aren't cached
		assertThat(cache.get("nullKey")).isNull();
		assertThat(loaderCallCount.get()).isEqualTo(2);

		// Non-null value should be cached
		assertThat(cache.get("key1")).isEqualTo("key1");
		assertThat(loaderCallCount.get()).isEqualTo(3);
		assertThat(cache.get("key1")).isEqualTo("key1");
		assertThat(loaderCallCount.get()).isEqualTo(3);
	}
}