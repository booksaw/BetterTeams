package com.booksaw.betterTeams.extension;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import static com.booksaw.betterTeams.extension.ExtensionTestUtil.createFakeJar;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ExtensionInfo Tests")
class ExtensionInfoTest {

	@TempDir
	Path tempDir;

	@Nested
	@DisplayName("fromYaml() Loading Tests")
	class FromYamlTests {

		@Test
		@DisplayName("Should successfully load a valid extension.yml")
		void testSuccessfulLoad() throws IOException {
			String ymlContent = """
                    name: TestExtension
                    main: com.booksaw.betterTeams.extension.TestExtensionImpl
                    version: 1.0.0
                    author: TestAuthor
                    description: A test extension
                    website: https://example.com
                    depend: [testExt]
                    softdepend: [TestExt2, TestExt3]
                    plugin-depend: [Vault, WorldEdit]
                    plugin-softdepend: [Essentials, LuckPerms]
                    """;

			File fakeJar = createFakeJar(
					"test-extension.jar",
					ymlContent,
					TestExtensionImpl.class,
					tempDir.toFile()
			);

			ExtensionInfo info = ExtensionInfo.fromYaml(fakeJar);

			// Assertions
			assertNotNull(info);
			assertEquals("TestExtension", info.getName());
			assertEquals("com.booksaw.betterTeams.extension.TestExtensionImpl", info.getMainClass());
			assertEquals("1.0.0", info.getVersion());
			assertEquals("TestAuthor", info.getAuthor());
			assertEquals("A test extension", info.getDescription());
			assertEquals("https://example.com", info.getWebsite());
			assertEquals(fakeJar, info.getJarFile());

			// Check lists
			assertEquals(List.of("testExt"), info.getExtensionDepend());
			assertEquals(List.of("TestExt2", "TestExt3"), info.getExtensionSoftDepend());
			assertEquals(List.of("Vault", "WorldEdit"), info.getPluginDepend());
			assertEquals(List.of("Essentials", "LuckPerms"), info.getPluginSoftDepend());
		}

		@Test
		@DisplayName("Should use default values for optional fields")
		void testDefaultValues() throws IOException {
			String ymlContent = """
                    name: MinimalExtension
                    main: com.booksaw.betterTeams.extension.TestExtensionImpl
                    """;

			File fakeJar = createFakeJar(
					"minimal.jar",
					ymlContent,
					TestExtensionImpl.class,
					tempDir.toFile()
			);

			ExtensionInfo info = ExtensionInfo.fromYaml(fakeJar);

			assertNotNull(info);
			assertEquals("MinimalExtension", info.getName());
			assertEquals("com.booksaw.betterTeams.extension.TestExtensionImpl", info.getMainClass());
			// Check defaults
			assertEquals("1.0", info.getVersion());
			assertEquals("", info.getAuthor());
			assertEquals("", info.getDescription());
			assertEquals("", info.getWebsite());
			assertTrue(info.getExtensionDepend().isEmpty());
			assertTrue(info.getExtensionSoftDepend().isEmpty());
			assertTrue(info.getPluginDepend().isEmpty());
			assertTrue(info.getPluginSoftDepend().isEmpty());
		}

		@Test
		@DisplayName("Should throw IOException if extension.yml is missing")
		void testMissingYml() throws IOException {
			// Create a JAR with no yml content
			File fakeJar = createFakeJar(
					"invalid.jar",
					null, // No YML
					TestExtensionImpl.class,
					tempDir.toFile()
			);

			IOException exception = assertThrows(IOException.class, () -> {
				ExtensionInfo.fromYaml(fakeJar);
			});

			assertTrue(exception.getMessage().contains("extension.yml not found"));
		}

		@Test
		@DisplayName("Should throw IllegalArgumentException if 'main' is missing in yml")
		void testMissingMainInYml() throws IOException {
			String ymlContent = """
                    name: InvalidExt
                    version: 1.0
                    """; // No 'main' key

			File fakeJar = createFakeJar(
					"invalid-main.jar",
					ymlContent,
					TestExtensionImpl.class,
					tempDir.toFile()
			);

			IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
				ExtensionInfo.fromYaml(fakeJar);
			});

			assertEquals("No 'main' specified in extension.yml", exception.getMessage());
		}

		@Test
		@DisplayName("Should throw IllegalArgumentException if main class is not in JAR")
		void testMissingMainClassInJar() throws IOException {
			String ymlContent = """
                    name: TestExtension
                    main: com.booksaw.betterTeams.extension.FakeTestExtension
                    version: 1.0
                    """;

			// Create a JAR without the class file
			File fakeJar = createFakeJar(
					"missing-class.jar",
					ymlContent,
					null, // No class added to JAR
					tempDir.toFile()
			);

			IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
				ExtensionInfo.fromYaml(fakeJar);
			});

			assertTrue(exception.getMessage().contains("Main class not found in JAR"));
		}

		@Test
		@DisplayName("Should throw IOException for a null file")
		void testNullFile() {
			IOException exception = assertThrows(IOException.class, () -> {
				ExtensionInfo.fromYaml(null);
			});

			assertTrue(exception.getMessage().contains("null"));
		}

		@Test
		@DisplayName("Should throw IOException for a non-existent file")
		void testNonExistentFile() {
			File nonExistent = new File(tempDir.toFile(), "not-real.jar");

			IOException exception = assertThrows(IOException.class, () -> {
				ExtensionInfo.fromYaml(nonExistent);
			});

			assertTrue(exception.getMessage().contains("not found or invalid"));
		}
	}
}