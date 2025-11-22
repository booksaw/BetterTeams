package com.booksaw.betterTeams.extension;

import com.booksaw.betterTeams.Main;
import org.bukkit.Server;
import org.bukkit.plugin.PluginManager;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.logging.Logger;

import static com.booksaw.betterTeams.extension.ExtensionTestUtil.createFakeJar;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("ExtensionManager Tests")
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ExtensionManagerTest {

	@Mock
	private Main mockPlugin;
	@Mock
	private Logger mockLogger;
	@Mock
	private ExtensionLoader mockLoader;
	@Mock
	private ExtensionStore mockStore;

	@Mock
	private File mockExtensionsDir;
	private ExtensionManager manager;


	@BeforeEach
	void setUp() {
		Server mockServer = mock(Server.class);
		PluginManager mockPluginManager = mock(PluginManager.class);
		when(mockPlugin.getLogger()).thenReturn(mockLogger);
		when(mockPlugin.getServer()).thenReturn(mockServer);
		when(mockServer.getPluginManager()).thenReturn(mockPluginManager);
		Main.plugin = mockPlugin;

		manager = new ExtensionManager(mockPlugin, mockExtensionsDir, mockLoader, mockStore);
	}

	@AfterEach
	void tearDown() {
		Main.plugin = null;
	}



	@Nested
	@DisplayName("Full Lifecycle Integration Test")
	class FullLifecycleIntegrationTest {
		@TempDir
		Path tempDir;

		private ExtensionManager manager;
		private ExtensionStore store;
		private File extensionsDir;

		@BeforeEach
		void setUp() {
			extensionsDir = tempDir.toFile();
			store = new ExtensionStore();
			ExtensionLoader loader = new ExtensionLoader(mockPlugin);
			manager = new ExtensionManager(mockPlugin, extensionsDir, loader, store);

			when(mockPlugin.getExtensionManager()).thenReturn(manager);
		}

		private String createYml(String name, String... deps) {
			StringBuilder yml = new StringBuilder();
			yml.append("name: ").append(name).append("\n");
			yml.append("main: ").append(TestExtensionImpl.class.getName()).append("\n");
			if (deps.length > 0) {
				yml.append("depend:\n");
				for (String dep : deps) {
					yml.append("  - ").append(dep).append("\n");
				}
			}
			return yml.toString();
		}

		@Test
		@DisplayName("Should run full lifecycle (init, enable, disable, unload) in correct order")
		void testFullLifecycle() throws IOException {
			createFakeJar(
					"ExtA.jar",
					createYml("ExtA"),
					TestExtensionImpl.class,
					extensionsDir
			);
			createFakeJar(
					"ExtB.jar",
					createYml("ExtB", "ExtA"), // ExtB depends on ExtA
					TestExtensionImpl.class,
					extensionsDir
			);

			// --- WHEN: Initialize ---
			manager.initializeExtensions();

			// --- THEN: Initialize ---
			verify(mockLogger).info("Initializing extensions...");
			verify(mockLogger).info("Loading extensions...");
			assertEquals(2, store.size());

			// Check load order (ExtA must be first)
			List<String> loadOrder = store.getLoadOrder();
			assertEquals("ExtA", loadOrder.get(0));
			assertEquals("ExtB", loadOrder.get(1));

			// Check that onLoad was called
			TestExtensionImpl instA = (TestExtensionImpl) store.get("ExtA").getInstance();
			TestExtensionImpl instB = (TestExtensionImpl) store.get("ExtB").getInstance();
			assertTrue(instA.onLoadCalled, "ExtA.onLoad() should be called");
			assertTrue(instB.onLoadCalled, "ExtB.onLoad() should be called");
			assertFalse(instA.onEnableCalled, "ExtA.onEnable() should not be called yet");
			assertFalse(instB.onEnableCalled, "ExtB.onEnable() should not be called yet");

			// --- WHEN: Enable ---
			manager.enableExtensions();

			// --- THEN: Enable ---
			verify(mockLogger).info("Enabling extensions...");
			assertTrue(instA.onEnableCalled, "ExtA.onEnable() should be called");
			assertTrue(instB.onEnableCalled, "ExtB.onEnable() should be called");
			assertTrue(store.get("ExtA").isEnabled(), "ExtA should be marked as enabled");
			assertTrue(store.get("ExtB").isEnabled(), "ExtB should be marked as enabled");

			// --- WHEN: Disable ---
			manager.disableExtensions();

			// --- THEN: Disable ---
			verify(mockLogger).info("Disabling extensions...");
			assertTrue(instA.onDisableCalled, "ExtA.onDisable() should be called");
			assertTrue(instB.onDisableCalled, "ExtB.onDisable() should be called");
			assertFalse(store.get("ExtA").isEnabled(), "ExtA should be marked as disabled");
			assertFalse(store.get("ExtB").isEnabled(), "ExtB should be marked as disabled");

			// --- WHEN: Unload ---
			manager.unloadExtensions();

			// --- THEN: Unload ---
			verify(mockLogger).info("Unloading extensions...");
			assertTrue(store.isEmpty(), "Store should be empty after unload");
		}
	}

}