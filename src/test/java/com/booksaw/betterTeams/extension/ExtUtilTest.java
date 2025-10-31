package com.booksaw.betterTeams.extension;

import com.booksaw.betterTeams.util.ExtUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.booksaw.betterTeams.extension.ExtensionTestUtil.createSortStub;
import static com.booksaw.betterTeams.extension.ExtensionTestUtil.createYml;
import static org.junit.jupiter.api.Assertions.*;


@DisplayName("ExtUtil Tests")
class ExtUtilTest {

	@TempDir
	Path tempDir;



	@Nested
	@DisplayName("scanExtensions() Tests")
	class ScanExtensionsTests {

		@Test
		@DisplayName("Should successfully load multiple valid extensions")
		void testMultipleValidJars() throws IOException {
			File dir = tempDir.toFile();
			ExtensionTestUtil.createFakeJar(
					"ext1.jar",
					createYml("ExtensionOne"),
					TestExtensionImpl.class,
					dir
			);
			ExtensionTestUtil.createFakeJar(
					"ext2.jar",
					createYml("ExtensionTwo"),
					TestExtensionImpl.class,
					dir
			);

			Set<ExtensionInfo> result = ExtUtil.scanExtensions(dir);
			assertEquals(2, result.size());

			Set<String> names = result.stream()
					.map(ExtensionInfo::getName)
					.collect(Collectors.toSet());

			assertTrue(names.contains("ExtensionOne"));
			assertTrue(names.contains("ExtensionTwo"));
		}

		@Test
		@DisplayName("Should ignore JARs with duplicate extension names")
		void testDuplicateNames() throws IOException {
			File dir = tempDir.toFile();
			// Both JARs declare the name "MyExtension"
			ExtensionTestUtil.createFakeJar(
					"ext1.jar",
					createYml("MyExtension"),
					TestExtensionImpl.class,
					dir
			);
			ExtensionTestUtil.createFakeJar(
					"ext2-dupe.jar",
					createYml("MyExtension"),
					TestExtensionImpl.class,
					dir
			);

			Set<ExtensionInfo> result = ExtUtil.scanExtensions(dir);

			// Only the first one found should be loaded
			assertEquals(1, result.size());
			assertEquals("MyExtension", result.iterator().next().getName());
		}

		@Test
		@DisplayName("Should ignore invalid JARs (missing yml) and still load valid ones")
		void testMixedValidAndInvalid() throws IOException {
			File dir = tempDir.toFile();

			// Valid JAR
			ExtensionTestUtil.createFakeJar(
					"valid.jar",
					createYml("ValidExt"),
					TestExtensionImpl.class,
					dir
			);

			// Invalid JAR (no extension.yml)
			ExtensionTestUtil.createFakeJar(
					"invalid.jar",
					null, // No YML content
					TestExtensionImpl.class,
					dir
			);

			String ymlContent = "name: InvalidMain\nversion: 1.0"; // No 'main' key
			ExtensionTestUtil.createFakeJar(
					"noMain.jar",
					ymlContent,
					TestExtensionImpl.class,
					dir
			);


			Set<ExtensionInfo> result = ExtUtil.scanExtensions(dir);

			// The exception from the invalid JAR should be caught, and the valid one loaded
			assertEquals(1, result.size());
			assertEquals("ValidExt", result.iterator().next().getName());
		}
	}

	@Nested
	@DisplayName("sort() Tests")
	class SortTests {

		@Test
		@DisplayName("Should handle extensions with no dependencies")
		void testNoDependencies() {
			ExtensionInfo extA = createSortStub("A", null, null);
			ExtensionInfo extB = createSortStub("B", null, null);
			Set<ExtensionInfo> input = Set.of(extA, extB);

			Set<ExtensionInfo> result = ExtUtil.sort(input);

			assertEquals(2, result.size());
			assertTrue(result.contains(extA));
			assertTrue(result.contains(extB));
		}

		@Test
		@DisplayName("Should correctly order transitive dependencies (A -> B -> C)")
		void testTransitiveDependency() {
			ExtensionInfo extA = createSortStub("A", List.of("B"), null);
			ExtensionInfo extB = createSortStub("B", List.of("C"), null);
			ExtensionInfo extC = createSortStub("C", null, null);
			Set<ExtensionInfo> input = Set.of(extA, extB, extC);

			Set<ExtensionInfo> result = ExtUtil.sort(input);
			List<ExtensionInfo> list = new ArrayList<>(result);

			assertEquals(3, list.size());
			assertEquals("C", list.get(0).getName());
			assertEquals("B", list.get(1).getName());
			assertEquals("A", list.get(2).getName());
		}

		@Test
		@DisplayName("Should correctly order a complex graph")
		void testComplexGraph() {
			// A -> B, C
			// B -> C
			// D -> C
			// E (no deps)
			ExtensionInfo extA = createSortStub("A", List.of("B", "C"), null);
			ExtensionInfo extB = createSortStub("B", List.of("C"), null);
			ExtensionInfo extD = createSortStub("D", List.of("C"), null);
			ExtensionInfo extC = createSortStub("C", null, null);
			ExtensionInfo extE = createSortStub("E", null, null);

			Set<ExtensionInfo> input = new HashSet<>(Set.of(extA, extB, extC, extD, extE));
			Set<ExtensionInfo> result = ExtUtil.sort(input);
			List<String> names = result.stream().map(ExtensionInfo::getName).collect(Collectors.toList());

			assertEquals(5, names.size());

			// Check relative order
			assertTrue(names.indexOf("C") < names.indexOf("A"), "C must be before A");
			assertTrue(names.indexOf("C") < names.indexOf("B"), "C must be before B");
			assertTrue(names.indexOf("C") < names.indexOf("D"), "C must be before D");
			assertTrue(names.indexOf("B") < names.indexOf("A"), "B must be before A");

			// E can be anywhere
			assertTrue(names.contains("E"));
		}

		@Test
		@DisplayName("Should detect hard circular dependency (A -> B, B -> A)")
		void testHardCircularDependency() {
			ExtensionInfo extA = createSortStub("A", List.of("B"), null);
			ExtensionInfo extB = createSortStub("B", List.of("A"), null);
			Set<ExtensionInfo> input = Set.of(extA, extB);

			Set<ExtensionInfo> result = ExtUtil.sort(input);
			assertNull(result);

		}
	}
}