package util;

import com.booksaw.betterTeams.util.Cache;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

public class CacheTest {
	AtomicInteger loaderCallCount;

	@BeforeAll
	public static void beforeAll() {
		// Prime AssertJ
		assertThat(true).isTrue();
	}

	@BeforeEach
	void setUp() {
		loaderCallCount = new AtomicInteger(0);
	}

	private @Nullable String increment(@NotNull Object key) {
		loaderCallCount.incrementAndGet();
		return key.equals("nullKey") ? null : "value-" + key;
	}

	@Test
	public void testBasicCacheOperations() {
		Cache<String, String> cache = new Cache.Builder<String, String>()
				.maximumSize(100)
				.build(this::increment);

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
		Cache<String, String> cache = new Cache.Builder<String, String>()
				.expireAfterWrite(Duration.ofMillis(100))
				.build(this::increment);

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
		Cache<String, String> cache = new Cache.Builder<String, String>()
				.expireAfterAccess(Duration.ofMillis(100))
				.build(this::increment);


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
		Cache<Integer, String> cache = new Cache.Builder<Integer, String>()
				.maximumSize(2)
				.build(this::increment);


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
		Cache<String, String> cache = new Cache.Builder<String, String>()
				.build(this::increment);

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
		Cache<String, String> cache = new Cache.Builder<String, String>()
				.build(this::increment);


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
		Cache<String, String> cache = new Cache.Builder<String, String>()
				.build(key -> {
					// Add some delay to increase chance of concurrent issues
					try {
						Thread.sleep(10);
					} catch (InterruptedException e) {
						// Ignore
					}
					loaderCallCount.incrementAndGet();
					return "value-" + key;
				});

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
		Cache<String, String> cache = new Cache.Builder<String, String>()
				.expireAfterWrite(Duration.ofMillis(200))
				.expireAfterAccess(Duration.ofMillis(100))
				.build(this::increment);


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
		Thread.sleep(120); // running total: 170ms
		// Should expire due to access expiration
		assertThat(cache.get("key1")).isEqualTo("value-key1");
		assertThat(loaderCallCount.get()).isEqualTo(3);

		assertThat(cache.get("key2")).isEqualTo("value-key2");
		assertThat(loaderCallCount.get()).isEqualTo(3);

		// Now the 2nd key expired due to write access
		Thread.sleep(50); // running total: 220ms
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
		Cache<String, String> cache = new Cache.Builder<String, String>()

				.build(this::increment);


		// Test null value
		assertThat(cache.get("nullKey")).isNull();
		assertThat(loaderCallCount.get()).isEqualTo(1);

		// Second call should still invoke loader since null values aren't cached
		assertThat(cache.get("nullKey")).isNull();
		assertThat(loaderCallCount.get()).isEqualTo(2);

		// Non-null value should be cached
		assertThat(cache.get("key1")).isEqualTo("value-key1");
		assertThat(loaderCallCount.get()).isEqualTo(3);
		assertThat(cache.get("key1")).isEqualTo("value-key1");
		assertThat(loaderCallCount.get()).isEqualTo(3);
	}
}