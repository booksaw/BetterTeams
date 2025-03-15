package com.booksaw.betterTeams.util;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * A simple cache implementation with expiration and size-based eviction.
 *
 * @param <K> The type of keys maintained by this cache
 * @param <V> The type of values maintained by this cache
 */
public class Cache<K, V> {
	private final Map<K, CachedValue<V>> cache;
	private final Function<K, V> loader;
	private final long expireAfterWriteMillis;
	private final long expireAfterAccessMillis;
	private final int maximumSize;

	private Cache(@NotNull Builder<K, V> builder) {
		this.loader = builder.loader;
		this.expireAfterWriteMillis = builder.expireAfterWriteMillis;
		this.expireAfterAccessMillis = builder.expireAfterAccessMillis;
		this.maximumSize = builder.maximumSize;

		this.cache = Collections.synchronizedMap(new LinkedHashMap<K, CachedValue<V>>(
				builder.initialCapacity, 0.75f, true) {
			@Override
			protected boolean removeEldestEntry(Map.Entry<K, CachedValue<V>> eldest) {
				return size() > maximumSize;
			}
		});
	}

	/**
	 * Gets a value from the cache, loading it if necessary.
	 *
	 * @param key the key to look up
	 * @return the value associated with the key
	 */
	public V get(K key) {
		return cache.compute(key, (k, cached) -> {
			if (cached == null || cached.isExpired(expireAfterAccessMillis, expireAfterWriteMillis)) {
				V value = loader.apply(k);
				return (value != null) ? new CachedValue<>(value) : null;
			}

			if (expireAfterAccessMillis > 0) {
				cached.updateAccessTime();
			}

			return cached;
		}) == null ? null : cache.get(key).getValue();
	}

	/**
	 * Returns the approximate number of entries in this cache.
	 */
	public int size() {
		return cache.size();
	}

	/**
	 * Removes all entries from the cache.
	 */
	public void invalidateAll() {
		cache.clear();
	}

	/**
	 * Removes an entry from the cache.
	 *
	 * @param key the key to remove
	 */
	public void invalidate(K key) {
		cache.remove(key);
	}

	/**
	 * A builder for creating Cache instances.
	 */
	public static class Builder<K, V> {
		private Function<K, V> loader;
		private long expireAfterWriteMillis = -1;
		private long expireAfterAccessMillis = -1;
		private int maximumSize = Integer.MAX_VALUE;
		private int initialCapacity = 16;

		/**
		 * Sets the expiration time after writing an entry.
		 */
		public Builder<K, V> expireAfterWrite(@NotNull Duration duration) {
			this.expireAfterWriteMillis = duration.toMillis();
			return this;
		}

		/**
		 * Sets the expiration time after accessing an entry.
		 */
		public Builder<K, V> expireAfterAccess(@NotNull Duration duration) {
			this.expireAfterAccessMillis = duration.toMillis();
			return this;
		}

		/**
		 * Sets the maximum size of the cache.
		 */
		public Builder<K, V> maximumSize(int maximumSize) {
			this.maximumSize = maximumSize;
			return this;
		}

		/**
		 * Sets the initial capacity of the cache.
		 */
		public Builder<K, V> initialCapacity(int initialCapacity) {
			this.initialCapacity = initialCapacity;
			return this;
		}

		/**
		 * Builds a new Cache instance.
		 */
		public Cache<K, V> build(Function<K, V> loader) {
			this.loader = loader;
			if (loader == null) {
				throw new IllegalStateException("Loader function must be set");
			}
			return new Cache<>(this);
		}
	}

	/**
	 * A value in the cache with expiration metadata.
	 */
	private static class CachedValue<V> {
		@Getter
		private final V value;
		private final long creationTime;
		private long accessTime;

		public CachedValue(V value) {
			this.value = value;
			this.creationTime = System.currentTimeMillis();
			this.accessTime = 0;
		}

		public boolean isExpired(long expireAfterAccessMillis, long expireAfterWriteMillis) {
			long now = System.currentTimeMillis();

			if (expireAfterWriteMillis > 0 && now - creationTime > expireAfterWriteMillis) {
				return true;
			}

			return accessTime > 0 && expireAfterAccessMillis > 0 && now - accessTime > expireAfterAccessMillis;
		}

		public void updateAccessTime() {
			this.accessTime = System.currentTimeMillis();
		}
	}
}