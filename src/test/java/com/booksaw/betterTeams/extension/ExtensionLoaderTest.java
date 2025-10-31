package com.booksaw.betterTeams.extension;

import com.booksaw.betterTeams.Main;
import com.booksaw.betterTeams.exceptions.LoadingException;
import org.bukkit.Server;
import org.bukkit.plugin.PluginManager;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.logging.Logger;

import static com.booksaw.betterTeams.extension.ExtensionTestUtil.createFakeJar;
import static com.booksaw.betterTeams.extension.ExtensionTestUtil.createYml;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("ExtensionLoader Tests")
class ExtensionLoaderTest {

	@TempDir
	Path tempDir;

	private ExtensionLoader loader;
	private Main mockPlugin;
	private Logger mockLogger;
	private ExtensionManager mockExtManager;
	private PluginManager mockPluginManager;


	@BeforeEach
	void setUp() {
		mockPlugin = mock(Main.class);
		mockLogger = mock(Logger.class);
		mockExtManager = mock(ExtensionManager.class);
		mockPluginManager = mock(PluginManager.class);
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
			assertFalse(wrapper.getEnabled());

			assertInstanceOf(TestExtensionImpl.class, wrapper.getInstance());
			TestExtensionImpl impl = (TestExtensionImpl) wrapper.getInstance();
			assertTrue(impl.onLoadCalled);
			assertFalse(impl.onEnableCalled);
			verify(mockLogger).info("Loaded extension: ValidExt");
		}
	}
}