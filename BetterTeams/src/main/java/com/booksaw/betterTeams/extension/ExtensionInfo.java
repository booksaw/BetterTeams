package com.booksaw.betterTeams.extension;
import lombok.*;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Holds all the metadata for a BetterTeams extension, loaded from its
 * {@code extension.yml} file.
 */
@Getter
@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class ExtensionInfo {
	/**
	 * The unique name of the extension.
	 */
	private final String name;

	/**
	 * The fully qualified class path to the extension's main class.
	 */
	private final String mainClass;

	/**
	 * The version of the extension.
	 */
	private final String version;

	/**
	 * The author(s) of the extension.
	 */
	private final String author;

	/**
	 * A brief description of what the extension does.
	 */
	private final String description;

	/**
	 * The official website for the extension.
	 */
	private final String website;

	/**
	 * A list of other BetterTeams extensions that this extension
	 * *requires* to load (hard dependency).
	 */
	private final List<String> extensionDepend;

	/**
	 * A list of other BetterTeams extensions that this extension
	 * can optionally hook into (soft dependency).
	 */
	private final List<String> extensionSoftDepend;

	/**
	 * A list of Bukkit plugins that this extension
	 * *requires* to load (hard dependency).
	 */
	private final List<String> pluginDepend;

	/**
	 * A list of Bukkit plugins that this extension
	 * can optionally hook into (soft dependency).
	 */
	private final List<String> pluginSoftDepend;

	/**
	 * The file handle for the extension's JAR file.
	 */
	private final File jarFile;

	/**
	 * Provides a user-friendly string representation of the extension.
	 * @return A formatted string
 	*/
	public String getDisplayName() {
		String trimmedAuthor = (author != null) ? author.trim() : "";
		return name.trim() + " v" + version.trim() +
				(trimmedAuthor.isEmpty() ? "" : " (author: " + trimmedAuthor + ")");
	}

	/**
	 * Loads and parses an {@code extension.yml} file from a given JAR file
	 * and constructs a new {@link ExtensionInfo} object.
	 *
	 * @param file The extension's JAR file.
	 * @return A fully populated {@link ExtensionInfo} object.
	 */
	public static ExtensionInfo fromYaml(File file) throws IOException {
		if (file == null || !file.exists()) {
			throw new IOException("JAR file not found or invalid: " + (file != null ? file.getAbsolutePath() : "null"));
		}

		try (JarFile jarFile = new JarFile(file)) {
			JarEntry entry = jarFile.getJarEntry("extension.yml");
			if (entry == null) {
				throw new IOException("extension.yml not found in JAR: " + file.getName());
			}

			try (InputStream yamlStream = jarFile.getInputStream(entry);
				 InputStreamReader reader = new InputStreamReader(yamlStream, StandardCharsets.UTF_8)) {
				YamlConfiguration yml = YamlConfiguration.loadConfiguration(reader);

				String name = yml.getString("name", "").trim();
				String main = yml.getString("main", "").trim();
				String ver = yml.getString("version", "1.0").trim();
				String author = yml.getString("author", "").trim();
				String desc = yml.getString("description", "").trim();
				String site = yml.getString("website", "").trim();

				List<String> eHard = yml.getStringList("depend");
				List<String> eSoft = yml.getStringList("softdepend");
				List<String> pHard = yml.getStringList("plugin-depend");
				List<String> pSoft = yml.getStringList("plugin-softdepend");

				if (main.isEmpty()) {
					throw new IllegalArgumentException("No 'main' specified in extension.yml");
				}
				if (name.isEmpty()) {
					throw new IllegalArgumentException("No 'name' specified in extension.yml");
				}
				if (jarFile.getJarEntry(main.replace('.', '/') + ".class") == null) {
					throw new IllegalArgumentException("Main class not found in JAR: " + main);
				}
				return new ExtensionInfo(name, main, ver, author, desc, site, eHard, eSoft, pHard, pSoft, file);
			}
		}
	}
}
