package com.booksaw.betterTeams.extension;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;

public class ExtensionTestUtil {

	public static File createFakeJar(String jarName, String ymlContent, Class<?> mainClass, File Dir) throws IOException {
		return createFakeJar(jarName, ymlContent, Dir, mainClass);
	}

	public static File createFakeJar(String jarName, String ymlContent, File destDir, Class<?>... classes) throws IOException {
		File jarFile = destDir.toPath().resolve(jarName).toFile();
		try (FileOutputStream fos = new FileOutputStream(jarFile);
			 JarOutputStream jos = new JarOutputStream(fos)) {

			if (ymlContent != null) {
				// Add extension.yml entry
				JarEntry entry = new JarEntry("extension.yml");
				jos.putNextEntry(entry);
				jos.write(ymlContent.getBytes(StandardCharsets.UTF_8));
				jos.closeEntry();
			}

			// Add all requested classes
			for (Class<?> clazz : classes) {
				if (clazz == null) continue;
				String classPath = clazz.getName().replace('.', '/') + ".class";
				try (InputStream classStream = clazz.getClassLoader().getResourceAsStream(classPath)) {
					if (classStream == null) {
						throw new IOException("Could not find class resource: " + classPath);
					}

					JarEntry classEntry = new JarEntry(classPath);
					jos.putNextEntry(classEntry);
					jos.write(classStream.readAllBytes());
					jos.closeEntry();
				}
			}
		}
		return jarFile;
	}

	static String createYml(String name) {
		return "name: " + name + "\nmain: " + TestExtensionImpl.class.getName();
	}

	static ExtensionInfo createSortStub(String name, List<String> deps, List<String> softDeps) {
		return new ExtensionInfo(
				name,
				"com.example.Main",
				"1.0",
				"Test",
				"",
				"",
				deps != null ? deps : List.of(),
				softDeps != null ? softDeps : List.of(),
				List.of(),
				List.of(),
				null
		);
	}
}