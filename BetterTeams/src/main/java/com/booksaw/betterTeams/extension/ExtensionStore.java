package com.booksaw.betterTeams.extension;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

public class ExtensionStore {
	private final Map<String, ExtensionWrapper> extensions = new ConcurrentHashMap<>();
	private final List<String> loadOrder = new CopyOnWriteArrayList<>();

	void add(ExtensionWrapper ext) {
		synchronized (this) {
			String name = ext.getInfo().getName();
			extensions.put(name, ext);
			if (!loadOrder.contains(name)) {
				loadOrder.add(name);
			}
		}
	}

	void remove(String name) {
		synchronized (this) {
			extensions.remove(name);
			loadOrder.remove(name);
		}
	}

	boolean contains(String name) {
		return extensions.containsKey(name);
	}

	Set<String> getNames() {
		return Set.copyOf(extensions.keySet());
	}

	ExtensionWrapper get(String name) {
		return extensions.get(name);
	}

	public BetterTeamsExtension get(String name, boolean enabled) {
		ExtensionWrapper loaded = extensions.get(name);
		if (loaded == null) {
			return null;
		}
		return (loaded.isEnabled() == enabled) ? loaded.getInstance() : null;
	}

	ExtensionWrapper get(BetterTeamsExtension extension) {
		return this.extensions.values().stream()
				.filter(wrapper -> wrapper.getInstance() == extension)
				.findFirst()
				.orElse(null);
	}

	List<BetterTeamsExtension> getEnabledExtensions() {
		return extensions.values().stream()
				.filter(ExtensionWrapper::isEnabled)
				.map(ExtensionWrapper::getInstance)
				.collect(Collectors.toList());
	}

	public List<String> getLoadOrder() {
		return new ArrayList<>(loadOrder);
	}

	List<String> getLoadOrderReversed() {
		List<String> reversed = new ArrayList<>(loadOrder);
		Collections.reverse(reversed);
		return reversed;
	}

	public Collection<ExtensionWrapper> getAll() {
		return List.copyOf(extensions.values());
	}

	void clear() {
		synchronized (this) {
			extensions.clear();
			loadOrder.clear();
		}
	}

	public int size() {
		return extensions.size();
	}

	public boolean isEmpty() {
		return extensions.isEmpty();
	}

	/**
	 * Gets a list of extension instances based on their enabled state.
	 * @param enabled true for enabled extensions, false for disabled (or not-yet-enabled) extensions.
	 * @return A read-only list of matching extension instances.
	 */
	public List<BetterTeamsExtension> getByState(boolean enabled) {
		return extensions.values().stream()
				.filter(loaded -> loaded.isEnabled() == enabled)
				.map(ExtensionWrapper::getInstance)
				.toList();
	}

	/**
	 * Gets a list of extension wrappers based on their enabled state.
	 * @param enabled true for enabled wrappers, false for disabled (or not-yet-enabled) wrappers.
	 * @return A read-only list of matching extension wrappers.
	 */
	public List<ExtensionWrapper> getWrappersByState(boolean enabled) {
		return extensions.values().stream()
				.filter(loaded -> loaded.isEnabled() == enabled)
				.toList();
	}
}
