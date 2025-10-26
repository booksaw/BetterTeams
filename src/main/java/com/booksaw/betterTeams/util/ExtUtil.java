package com.booksaw.betterTeams.util;

import com.booksaw.betterTeams.Main;
import com.booksaw.betterTeams.extension.ExtensionInfo;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ExtUtil {

	public static boolean missingExtDep(ExtensionInfo info) {
		// TODO
		return false;
	}

	public static boolean missingPluginDep(ExtensionInfo info) {
		return info.getPluginDepend().stream()
				.anyMatch(dep -> {
					Plugin p = Main.plugin.getServer().getPluginManager().getPlugin(dep);
					return p == null || !p.isEnabled();
				});
	}

	public static Set<ExtensionInfo> scanExtensions(File extensionsDir) {
		Set<ExtensionInfo> result = new HashSet<>();
		if (!extensionsDir.exists() && !extensionsDir.mkdirs()) {
			return result;
		}

		File[] files = extensionsDir.listFiles((dir, name) -> name.toLowerCase().endsWith(".jar"));
		if (files == null || files.length == 0) {
			return result;
		}

		Set<String> seenNames = new HashSet<>();
		for (File jar : files) {
			try {
				ExtensionInfo info = ExtensionInfo.fromYaml(jar);
				if (seenNames.contains(info.getName())) {
					continue;
				}
				result.add(info);
				seenNames.add(info.getName());

			} catch (Exception ignored) {}
		}
		return result;
	}

	public static Set<ExtensionInfo> sort(Set<ExtensionInfo> input) {
		if (input.isEmpty()) return Set.of();

		// Build graph
		Map<String, ExtensionInfo> extMap = input.stream()
				.collect(Collectors.toMap(ExtensionInfo::getName, ext -> ext));
		Map<String, Set<String>> dependencies = new HashMap<>();
		Map<String, Integer> inDegree = new HashMap<>();

		// Initialize
		for (String name : extMap.keySet()) {
			dependencies.put(name, new HashSet<>());
			inDegree.put(name, 0);
		}

		// Build edges (hard + soft deps)
		for (ExtensionInfo ext : input) {
			String name = ext.getName();
			List<String> allDeps = new ArrayList<>(ext.getExtensionDepend());
			allDeps.addAll(ext.getExtensionSoftDepend());

			for (String dep : allDeps) {
				if (extMap.containsKey(dep)) {
					dependencies.get(dep).add(name);
					inDegree.put(name, inDegree.get(name) + 1);
				}
			}
		}

		// Kahn's algorithm
		Queue<String> queue = new LinkedList<>();
		for (Map.Entry<String, Integer> entry : inDegree.entrySet()) {
			if (entry.getValue() == 0) {
				queue.offer(entry.getKey());
			}
		}

		List<ExtensionInfo> sortedList = new ArrayList<>();
		while (!queue.isEmpty()) {
			String current = queue.poll();
			sortedList.add(extMap.get(current));

			for (String dependent : dependencies.getOrDefault(current, Set.of())) {
				inDegree.put(dependent, inDegree.get(dependent) - 1);
				if (inDegree.get(dependent) == 0) {
					queue.offer(dependent);
				}
			}
		}

		// Cycle detection
		if (sortedList.size() != input.size()) {
			return null;
		}

		// Preserve topological order in LinkedHashSet
		return new LinkedHashSet<>(sortedList);
	}
}
