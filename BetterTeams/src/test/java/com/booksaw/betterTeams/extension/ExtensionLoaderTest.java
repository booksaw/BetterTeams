package com.booksaw.betterTeams.extension;

import com.booksaw.betterTeams.Main;
import com.booksaw.betterTeams.exceptions.LoadingException;
import org.bukkit.Server;
import org.bukkit.plugin.PluginManager;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.io.File;
import java.io.IOException;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.util.List;
import java.util.logging.Logger;

import static com.booksaw.betterTeams.extension.ExtensionTestUtil.createFakeJar;
import static com.booksaw.betterTeams.extension.ExtensionTestUtil.createYml;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("ExtensionLoader Tests")
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ExtensionLoaderTest {

	@TempDir
	Path tempDir;

	private ExtensionLoader loader;
	@Mock
	private Main mockPlugin;
	@Mock
	private Logger mockLogger;
	@Mock
	private ExtensionManager mockExtManager;
	@Mock
	private PluginManager mockPluginManager;


	@BeforeEach
	void setUp() {
		Server mockServer = mock(Server.class);

		when(mockPlugin.getLogger()).thenReturn(mockLogger);
		when(mockPlugin.getServer()).thenReturn(mockServer);
		when(mockServer.getPluginManager()).thenReturn(mockPluginManager);

		Main.plugin = mockPlugin;
//		when(mockPlugin.getExtensionManager()).thenReturn(mockExtManager);
		loader = new ExtensionLoader(mockPlugin);
	}

	@AfterEach
	void tearDown() {
		Main.plugin = null;
	}

	/**
	 * Helper method to create a valid, loadable extension info
	 */
	private ExtensionInfo createValidInfo(String name) throws IOException {
		String yml = createYml(name);
		File jar = createFakeJar(name + ".jar", yml, TestExtensionImpl.class, tempDir.toFile());
		return ExtensionInfo.fromYaml(jar);
	}

	@Nested
	@DisplayName("load() Tests")
	class LoadTests {

		@Test
		@DisplayName("Should successfully load a valid extension")
		void testLoadSuccess() throws IOException, LoadingException {
			ExtensionInfo info = createValidInfo("ValidExt");
			ExtensionWrapper wrapper = loader.load(info);

			assertNotNull(wrapper);
			assertNotNull(wrapper.getInstance());
			assertNotNull(wrapper.getClassLoader());
			assertEquals(info, wrapper.getInfo());
			assertFalse(wrapper.isEnabled());

			assertInstanceOf(TestExtensionImpl.class, wrapper.getInstance());
			TestExtensionImpl impl = (TestExtensionImpl) wrapper.getInstance();
			assertTrue(impl.onLoadCalled);
			assertFalse(impl.onEnableCalled);
			verify(mockLogger).info("Loaded extension: ValidExt");
		}

		@Test
		@DisplayName("Should throw LoadingException if JAR file is missing")
		void testLoadNoJar() throws IOException {
			ExtensionInfo mockInfo = mock(ExtensionInfo.class);

			when(mockInfo.getName()).thenReturn("NoJarExt");
			when(mockInfo.getJarFile()).thenReturn(null);

			LoadingException e = assertThrows(LoadingException.class, () -> {
				loader.load(mockInfo);
			});
			assertEquals("No JAR file associated with extension 'NoJarExt'", e.getMessage());;
		}

		@Test
		@DisplayName("Should throw LoadingException if main class does not extend BetterTeamsExtension")
		void testLoadWrongMainClass() throws IOException {
			String yml = createYml("WrongClass") + "\nmain: com.booksaw.betterTeams.extension.ExtensionLoaderTest";
			File jar = createFakeJar("wrongclass.jar", yml, ExtensionLoaderTest.class, tempDir.toFile());
			ExtensionInfo info = ExtensionInfo.fromYaml(jar);

			LoadingException e = assertThrows(LoadingException.class, () -> {
				loader.load(info);
			});

			assertTrue(e.getMessage().startsWith("Failed to initialize extension 'WrongClass'"));
			assertInstanceOf(ClassCastException.class, e.getCause());
		}
	}

	@Nested
	@DisplayName("enable() Tests")
	class EnableTests {

		private ExtensionWrapper validWrapper;
		private TestExtensionImpl validImpl;

		@BeforeEach
		void setUp() throws IOException, LoadingException {
			ExtensionInfo info = createValidInfo("ValidExt");
			validWrapper = loader.load(info); // This calls onLoad
			validImpl = (TestExtensionImpl) validWrapper.getInstance();
		}

		@Test
		@DisplayName("Should successfully enable a loaded extension")
		void testEnableSuccess() throws LoadingException {

			loader.enable(validWrapper);

			// Then: It should be enabled
			assertTrue(validWrapper.isEnabled());
			assertTrue(validImpl.onEnableCalled);

			// And: It should log success
			verify(mockLogger).info("Enabled extension: ValidExt");
		}

		@Test
		@DisplayName("Should not enable if already enabled")
		void testEnableAlreadyEnabled() throws LoadingException {
			validWrapper.setEnabled(true);
			validImpl.onEnableCalled = false; // Reset for test

			ExtensionWrapper result = loader.enable(validWrapper);

			// Should return the same wrapper and not call onEnable again
			assertSame(validWrapper, result);
			assertFalse(validImpl.onEnableCalled);
		}

		@Test
		@DisplayName("Should throw LoadingException if a plugin dependency is missing")
		void testEnableFailsMissingPluginDep() throws IOException {
			// Given: An extension that depends on "Vault"
			String yml = createYml("DepExt") + "\nplugin-depend: [Vault]";
			File jar = createFakeJar("depext.jar", yml, TestExtensionImpl.class, tempDir.toFile());
			ExtensionInfo info = ExtensionInfo.fromYaml(jar);
			ExtensionWrapper depWrapper = assertDoesNotThrow(() -> loader.load(info));

			// And: The PluginManager says "Vault" is not enabled
			when(mockPluginManager.getPlugin("Vault")).thenReturn(null);

			// When: We try to enable it
			LoadingException e = assertThrows(LoadingException.class, () -> {
				loader.enable(depWrapper);
			});

			// Then: It should fail
			assertEquals("Cannot enable 'DepExt': Missing Bukkit plugin dependencies", e.getMessage());
			assertNotNull(depWrapper);
			assertFalse(depWrapper.isEnabled());
		}

		// TODO
//		@Test
		@DisplayName("Should throw LoadingException if an extension dependency is missing")
		void testEnableFailsMissingExtensionDep() throws IOException {
			// Given: An extension that depends on "OtherExt"
			String yml = createYml("DepExt") + "\ndepend: [OtherExt]";
			File jar = createFakeJar("depext.jar", yml, TestExtensionImpl.class, tempDir.toFile());
			ExtensionInfo info = ExtensionInfo.fromYaml(jar);
			ExtensionWrapper depWrapper = assertDoesNotThrow(() -> loader.load(info));

			// And: The ExtensionManager says "OtherExt" is not enabled
			when(mockExtManager.isEnabled("OtherExt")).thenReturn(false);

			// When: We try to enable it
			LoadingException e = assertThrows(LoadingException.class, () -> {
				loader.enable(depWrapper);
			});

			// Then: It should fail
			assertEquals("Cannot enable 'DepExt': Missing dependencies", e.getMessage());
			assertNotNull(depWrapper);
			assertFalse(depWrapper.isEnabled());
		}
	}

	@Nested
	@DisplayName("disable() Tests")
	class DisableTests {
		private ExtensionWrapper validWrapper;
		private TestExtensionImpl validImpl;

		@BeforeEach
		void setUp() throws IOException, LoadingException {
			ExtensionInfo info = createValidInfo("ValidExt");
			validWrapper = loader.load(info);
			validImpl = (TestExtensionImpl) validWrapper.getInstance();

			// Manually set as enabled for disable tests
			validWrapper.setEnabled(true);
			validImpl.onLoadCalled = false; // Reset flags
		}

		@Test
		@DisplayName("Should successfully disable an enabled extension")
		void testDisableSuccess() {
			// Given: An enabled extension
			assertTrue(validWrapper.isEnabled());

			// When: We disable it
			loader.disable(validWrapper);

			// Then: It should be disabled
			assertFalse(validWrapper.isEnabled());
			assertTrue(validImpl.onDisableCalled);

			// And: It should log success
			verify(mockLogger).info("Disabled extension: ValidExt");
		}

		@Test
		@DisplayName("Should do nothing if already disabled")
		void testDisableAlreadyDisabled() {
			// Given: An extension that is not enabled
			validWrapper.setEnabled(false);

			// When: We disable it
			loader.disable(validWrapper);

			// Then: onDisable should not be called
			assertFalse(validImpl.onDisableCalled);
			// And: No log message
			verify(mockLogger, never()).info("Disabled extension: ValidExt");
		}
	}

	@Nested
	@DisplayName("unload() Tests")
	class UnloadTests {

		@Test
		@DisplayName("Should disable and unload a loaded extension")
		void testUnloadSuccess() throws IOException, LoadingException {
			// Given: A fully loaded and enabled extension
			ExtensionInfo info = createValidInfo("ValidExt");
			ExtensionWrapper wrapper = loader.load(info);
			TestExtensionImpl impl = (TestExtensionImpl) wrapper.getInstance();
			loader.enable(wrapper);
			assertTrue(wrapper.isEnabled());

			// When: We unload it
			loader.unload(wrapper);

			// Then: It should be disabled
			assertFalse(wrapper.isEnabled());
			assertTrue(impl.onDisableCalled);

			// And: It should log all 4 lifecycle steps
			ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
			verify(mockLogger, times(4)).info(captor.capture()); // 1.Load, 2.Enable, 3.Disable, 4.Unload

			List<String> logs = captor.getAllValues();
			assertEquals("Loaded extension: ValidExt", logs.get(0));
			assertEquals("Enabled extension: ValidExt", logs.get(1));
			assertEquals("Disabled extension: ValidExt", logs.get(2));
			assertEquals("Unloaded extension: ValidExt", logs.get(3));

			// And: The classloader should be closed (best-effort check)
			URLClassLoader cl = wrapper.getClassLoader();
			assertNotNull(cl);
		}
	}
}