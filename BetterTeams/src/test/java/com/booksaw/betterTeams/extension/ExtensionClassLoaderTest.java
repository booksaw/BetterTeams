package com.booksaw.betterTeams.extension;

import com.booksaw.betterTeams.Main;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.File;
import java.nio.file.Path;
import java.util.List;

import static com.booksaw.betterTeams.extension.ExtensionTestUtil.createFakeJar;
import static com.booksaw.betterTeams.extension.ExtensionTestUtil.createYml;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@DisplayName("ExtensionClassLoader Global Access Tests")
class ExtensionClassLoaderTest {

	@TempDir
	Path tempDir;

	@Mock
	private Main mockPlugin;
	@Mock
	private ExtensionManager mockManager;
	@Mock
	private ExtensionStore mockStore;

	// A custom parent loader that pretends it CANNOT find our specific test classes.
	// This forces the ExtensionClassLoader to look inside the JARs instead of delegating to the system.
	private ClassLoader isolatingParent;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		when(mockPlugin.getExtensionManager()).thenReturn(mockManager);
		when(mockManager.getStore()).thenReturn(mockStore);

		// Create the filter
		isolatingParent = new ClassLoader(getClass().getClassLoader()) {
			@Override
			protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
				// Block the parent from loading our test classes, forcing the child to do it
				if (name.equals(UniqueClassInExtA.class.getName()) ||
						name.equals(TestExtensionImpl.class.getName())) {
					throw new ClassNotFoundException(name);
				}
				return super.loadClass(name, resolve);
			}
		};
	}

	// --- Dummy Classes for Testing ---
	public static class UniqueClassInExtA {
		public static String greet() { return "Hello from A"; }
	}

	@Test
	@DisplayName("Should load a class from another extension (Global Access)")
	void testLoadClassFromOtherExtension() throws Exception {
		// 1. SETUP EXTENSION A (The Provider)
		File jarA = createFakeJar("ExtA.jar", createYml("ExtA"), tempDir.toFile(),
				TestExtensionImpl.class, UniqueClassInExtA.class);

		ExtensionInfo infoA = ExtensionInfo.fromYaml(jarA);

		try (ExtensionClassLoader loaderA = new ExtensionClassLoader(jarA, isolatingParent, infoA, mockManager)) {

			ExtensionWrapper wrapperA = mock(ExtensionWrapper.class);
			when(wrapperA.getClassLoader()).thenReturn(loaderA);
			when(wrapperA.getInfo()).thenReturn(infoA);

			// Mock the store to contain A
			when(mockStore.getAll()).thenReturn(List.of(wrapperA));

			// 2. SETUP EXTENSION B (The Consumer)
			File jarB = createFakeJar("ExtB.jar", createYml("ExtB"), tempDir.toFile(), TestExtensionImpl.class);
			ExtensionInfo infoB = ExtensionInfo.fromYaml(jarB);

			try (ExtensionClassLoader loaderB = new ExtensionClassLoader(jarB, isolatingParent, infoB, mockManager)) {
				// 3. THE TEST
				// Loader B doesn't have UniqueClassInExtA, Parent blocked it.
				// It MUST ask Loader A.
				Class<?> loadedClass = assertDoesNotThrow(() -> {
					return loaderB.loadClass(UniqueClassInExtA.class.getName());
				});

				// VERIFY
				assertEquals(UniqueClassInExtA.class.getName(), loadedClass.getName());
				// Verify it was loaded by Loader A
				assertEquals(loaderA, loadedClass.getClassLoader());
			}
		}
	}

	@Test
	@DisplayName("Should prefer its own class over others")
	void testSelfPriority() throws Exception {
		// 1. Setup ExtA
		File jarA = createFakeJar("ExtA.jar", createYml("ExtA"), tempDir.toFile(), TestExtensionImpl.class);
		ExtensionInfo infoA = ExtensionInfo.fromYaml(jarA);

		try (ExtensionClassLoader loaderA = new ExtensionClassLoader(jarA, isolatingParent, infoA, mockManager)) {
			ExtensionWrapper wrapperA = mock(ExtensionWrapper.class);
			when(wrapperA.getClassLoader()).thenReturn(loaderA);
			when(wrapperA.getInfo()).thenReturn(infoA);
			when(mockStore.getAll()).thenReturn(List.of(wrapperA));

			// 2. Setup ExtB
			File jarB = createFakeJar("ExtB.jar", createYml("ExtB"), tempDir.toFile(), TestExtensionImpl.class);
			ExtensionInfo infoB = ExtensionInfo.fromYaml(jarB);

			try (ExtensionClassLoader loaderB = new ExtensionClassLoader(jarB, isolatingParent, infoB, mockManager)) {
				// 3. Test: Load TestExtensionImpl (exists in A, B, and Parent)
				// Parent is blocked. It should find it in B (Self) before checking A.
				Class<?> loadedClass = loaderB.loadClass(TestExtensionImpl.class.getName());

				// 4. Verify: It should be loaded by Loader B
				assertEquals(loaderB, loadedClass.getClassLoader());
				assertNotEquals(loaderA, loadedClass.getClassLoader());
			}
		}
	}

	@Test
	@DisplayName("Should fail if class does not exist anywhere")
	void testClassNotFound() throws Exception {
		File jarB = createFakeJar("ExtB.jar", createYml("ExtB"), tempDir.toFile(), TestExtensionImpl.class);
		ExtensionInfo infoB = ExtensionInfo.fromYaml(jarB);

		try (ExtensionClassLoader loaderB = new ExtensionClassLoader(jarB, isolatingParent, infoB, mockManager)) {
			when(mockStore.getAll()).thenReturn(List.of());

			assertThrows(ClassNotFoundException.class, () -> {
				loaderB.loadClass("com.booksaw.nonexistent.Clazz");
			});
		}
	}
}