package com.booksaw.betterTeams.extension;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

public class ExtensionClassLoader extends URLClassLoader {

	private final ExtensionManager manager;
	private final ExtensionInfo info;

	public ExtensionClassLoader(File jar, ClassLoader parent, ExtensionInfo info, ExtensionManager manager) throws MalformedURLException {
		super(new URL[]{jar.toURI().toURL()}, parent);
		this.info = info;
		this.manager = manager;
	}

	@Override
	protected Class<?> findClass(String name) throws ClassNotFoundException {
		// Try to find the class inside THIS extension's jar
		try {
			return super.findClass(name);
		} catch (ClassNotFoundException ignored) {
		}

		for (ExtensionWrapper wrapper : manager.getStore().getAll()) {
			// Don't check ourselves again
			if (wrapper.getInfo().getName().equals(info.getName())) {
				continue;
			}

			// Only check if the other extension has a ClassLoader ready
			ClassLoader otherLoader = wrapper.getClassLoader();
			if (otherLoader instanceof ExtensionClassLoader extLoader) {
				try {
					return extLoader.findClassInJar(name);
				} catch (ClassNotFoundException ignored) {
				}
			}
		}
		throw new ClassNotFoundException(name);
	}

	/**
	 * Helper method to expose the protected findClass from URLClassLoader
	 * strictly for looking inside that specific JAR.
	 */
	public Class<?> findClassInJar(String name) throws ClassNotFoundException {
		return super.findClass(name);
	}
}