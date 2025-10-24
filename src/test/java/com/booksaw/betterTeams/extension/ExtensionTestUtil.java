package com.booksaw.betterTeams.extension;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;

public class ExtensionTestUtil {

	public static File createFakeJar(String jarName, String ymlContent, Class<?> mainClass, File extensionsDir) throws IOException {
		File jarFile = extensionsDir.toPath().resolve(jarName).toFile();
		try (FileOutputStream fos = new FileOutputStream(jarFile);
			 JarOutputStream jos = new JarOutputStream(fos)) {

			if (ymlContent != null) {
				// Add extension.yml entry
				JarEntry entry = new JarEntry("extension.yml");
				jos.putNextEntry(entry);
				jos.write(ymlContent.getBytes(StandardCharsets.UTF_8));
				jos.closeEntry();
			}

			if (mainClass != null) {
				String classPath = mainClass.getName().replace('.', '/') + ".class";
				try (InputStream classStream = mainClass.getClassLoader().getResourceAsStream(classPath)) {
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
}
