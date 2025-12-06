package com.booksaw.betterTeams.integrations.placeholder;

import com.booksaw.betterTeams.Team;
import com.booksaw.betterTeams.TeamPlayer;
import com.booksaw.betterTeams.Utils;
import com.booksaw.betterTeams.message.MessageManager;
import com.booksaw.betterTeams.team.TeamManager;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@DisplayName("TeamPlaceholders Test")
@SuppressWarnings("SpellCheckingInspection")
@ExtendWith(MockitoExtension.class)
class TeamPlaceholdersTest {

	private TeamPlaceholders teamPlaceholders;

	@Mock
	private Plugin mockPlugin;
	@Mock
	private FileConfiguration mockConfig;
	@Mock
	private Player mockPlayer;
	@Mock
	private OfflinePlayer mockOfflinePlayer;

	@Mock
	private Team mockTeam;
	@Mock
	private TeamPlayer mockTeamPlayer;
	@Mock
	private TeamManager mockTeamManager;

	private MockedStatic<Team> teamStatic;
	private MockedStatic<MessageManager> messageManagerStatic;
	private MockedStatic<Utils> utilsStatic;
	private MockedStatic<TeamPlaceholderService> placeholderServiceStatic;

	@BeforeEach
	@SuppressWarnings("ResultOfMethodCallIgnored")
	void setUp() {
		when(mockPlugin.getConfig()).thenReturn(mockConfig);
//		when(mockConfig.getInt("invalidateCacheSeconds", 60)).thenReturn(30);

		teamStatic = mockStatic(Team.class);
		messageManagerStatic = mockStatic(MessageManager.class);
		utilsStatic = mockStatic(Utils.class);
		placeholderServiceStatic = mockStatic(TeamPlaceholderService.class);

		teamStatic.when(Team::getTeamManager).thenReturn(mockTeamManager);
		messageManagerStatic.when(() -> MessageManager.getMessage(anyString()))
				.thenAnswer(invocation -> "msg:" + invocation.getArgument(0));

		teamPlaceholders = new TeamPlaceholders(mockPlugin);
	}

	@AfterEach
	void tearDown() {
		teamStatic.close();
		messageManagerStatic.close();
		utilsStatic.close();
		placeholderServiceStatic.close();
	}

	@Nested
	@DisplayName("onRequest() Player-Specific Tests")
	class onRequestTests {

		@Test
		@DisplayName("Should return null if player is null")
		void testRequestWithNullPlayer() {
			assertNull(teamPlaceholders.onRequest(null, "any_identifier"));
		}

		@Test
		@DisplayName("Should return 'not in team' message for %betterteams_inTeam% when player is not in a team")
		void testPlayerNotInTeamForInTeamPlaceholder() {
			when(mockTeamManager.isInTeam(mockPlayer)).thenReturn(false);
			String result = teamPlaceholders.onRequest(mockPlayer, "inTeam");
			assertEquals("msg:placeholder.notinteam", result);
		}

		@Test
		@DisplayName("Should return 'in team' message for %betterteams_inTeam% when player is in a team")
		void testPlayerInTeamForInTeamPlaceholder() {
			when(mockTeamManager.isInTeam(mockPlayer)).thenReturn(true);
			String result = teamPlaceholders.onRequest(mockPlayer, "inTeam");
			assertEquals("msg:placeholder.inteam", result);
		}

		@Test
		@DisplayName("Should return 'no team' message if player is not in a team for a team-specific placeholder")
		void testPlayerNotInTeamForTeamSpecificPlaceholder() {
			teamStatic.when(() -> Team.getTeam(mockPlayer)).thenReturn(null);
			String result = teamPlaceholders.onRequest(mockPlayer, "name");
			assertEquals("msg:placeholder.noTeam", result);
		}

		@Test
		@DisplayName("Should handle a standard placeholder request correctly")
		void testStandardPlaceholderRequest() {
			teamStatic.when(() -> Team.getTeam(mockPlayer)).thenReturn(mockTeam);
			when(mockTeam.getTeamPlayer(mockPlayer)).thenReturn(mockTeamPlayer);
			placeholderServiceStatic.when(() -> TeamPlaceholderService.getPlaceholder("name", mockTeam, mockTeamPlayer))
					.thenReturn("TeamAlpha");

			String result = teamPlaceholders.onRequest(mockPlayer, "name");

			assertEquals("TeamAlpha", result);
			placeholderServiceStatic.verify(() -> TeamPlaceholderService.getPlaceholder("name", mockTeam, mockTeamPlayer));
		}

		@Test
		@DisplayName("Should handle a placeholder with data correctly")
		void testDataPlaceholderRequest() {
			String identifier = "meta_SomeKey";

			teamStatic.when(() -> Team.getTeam(mockPlayer)).thenReturn(mockTeam);
			when(mockTeam.getTeamPlayer(mockPlayer)).thenReturn(mockTeamPlayer);
			placeholderServiceStatic.when(() -> TeamPlaceholderService.requiresData("meta")).thenReturn(true);
			placeholderServiceStatic.when(() -> TeamPlaceholderService.getPlaceholder("meta", mockTeam, mockTeamPlayer, "SomeKey"))
					.thenReturn("SomeValue");

			String result = teamPlaceholders.onRequest(mockPlayer, identifier);

			assertEquals("SomeValue", result);
			placeholderServiceStatic.verify(() -> TeamPlaceholderService.getPlaceholder("meta", mockTeam, mockTeamPlayer, "SomeKey"));
		}
	}

	@Nested
	@DisplayName("Static and Ranked Placeholder Tests")
	class StaticAndRankedPlaceholderTests {

		@Test
		@DisplayName("Should resolve a ranked placeholder by score")
		void testRankedPlaceholder_Score() {
			String[] sortedTeams = {"TeamBravo", "TeamAlpha"};
			when(mockTeamManager.sortTeamsByScore()).thenReturn(sortedTeams);
			teamStatic.when(() -> Team.getTeam("TeamAlpha")).thenReturn(mockTeam);
			placeholderServiceStatic.when(() -> TeamPlaceholderService.getPlaceholder("name", mockTeam, null))
					.thenReturn("TeamAlpha");

			String result = teamPlaceholders.onRequest(null, "position_name_2");
			assertEquals("TeamAlpha", result);
			verify(mockTeamManager).sortTeamsByScore();
		}

		@Test
		@DisplayName("Should resolve a ranked placeholder by balance")
		void testRankedPlaceholder_Balance() {
			String[] sortedTeams = {"TeamBravo", "TeamAlpha"};
			when(mockTeamManager.sortTeamsByBalance()).thenReturn(sortedTeams);
			teamStatic.when(() -> Team.getTeam("TeamBravo")).thenReturn(mockTeam);
			placeholderServiceStatic.when(() -> TeamPlaceholderService.getPlaceholder("name", mockTeam, null))
					.thenReturn("TeamBravo");

			String result = teamPlaceholders.onRequest(null, "balanceposition_name_1");
			assertEquals("TeamBravo", result);
			verify(mockTeamManager).sortTeamsByBalance();
		}

		@Test
		@DisplayName("Should return null for a ranked placeholder with an invalid rank")
		void testRankedPlaceholder_InvalidRank() {
			assertNull(teamPlaceholders.onRequest(null, "position_name_abc"));
			assertNull(teamPlaceholders.onRequest(null, "position_name_0"));
		}

		@Test
		@DisplayName("Should return 'no team' message for a ranked placeholder with rank out of bounds")
		void testRankedPlaceholder_RankOutOfBounds() {
			String[] sortedTeams = {"TeamAlpha"};
			when(mockTeamManager.sortTeamsByScore()).thenReturn(sortedTeams);
			String result = teamPlaceholders.onRequest(null, "position_name_2");
			assertEquals("msg:placeholder.noTeam", result);
		}

		@Test
		@DisplayName("Should resolve a static team placeholder successfully")
		void testStaticTeamPlaceholder_Success() {
			teamStatic.when(() -> Team.getTeam("myteam")).thenReturn(mockTeam);
			placeholderServiceStatic.when(() -> TeamPlaceholderService.getPlaceholder("score", mockTeam, null))
					.thenReturn("100");

			String result = teamPlaceholders.onRequest(null, "static_score_MyTeam");
			assertEquals("100", result);
		}

		@Test
		@DisplayName("Should return 'no team' message if static team placeholder team is not found")
		void testStaticTeamPlaceholder_TeamNotFound() {
			teamStatic.when(() -> Team.getTeam("noteam")).thenReturn(null);

			String result = teamPlaceholders.onRequest(null, "static_score_NoTeam");
			assertEquals("msg:placeholder.noTeam", result);
		}

		@Test
		@DisplayName("Should resolve a static player placeholder successfully")
		void testStaticPlayerPlaceholder_Success() {
			String playerName = "testplayer";
			utilsStatic.when(() -> Utils.getOfflinePlayer(playerName)).thenReturn(mockOfflinePlayer);
			teamStatic.when(() -> Team.getTeam(mockOfflinePlayer)).thenReturn(mockTeam);
			when(mockTeam.getTeamPlayer(mockOfflinePlayer)).thenReturn(mockTeamPlayer);
			placeholderServiceStatic.when(() -> TeamPlaceholderService.getPlaceholder("rank", mockTeam, mockTeamPlayer))
					.thenReturn("Admin");

			String result = teamPlaceholders.onRequest(null, "staticplayer_rank_TestPlayer");

			assertEquals("Admin", result);
			verify(mockTeam).getTeamPlayer(mockOfflinePlayer);
		}

		@Test
		@DisplayName("Should return 'no team' message if static player placeholder player is not found")
		void testStaticPlayerPlaceholder_PlayerNotFound() {
			utilsStatic.when(() -> Utils.getOfflinePlayer("noplayer")).thenReturn(null);

			String result = teamPlaceholders.onRequest(null, "staticplayer_title_NoPlayer");
			assertEquals("msg:placeholder.noTeam", result);
		}
	}

	@Nested
	@DisplayName("Cache Logic Tests")
	class CacheTests {

		@Test
		@DisplayName("Should cache the result of a static placeholder request")
		void testStaticPlaceholderIsCached() {
			// --- First Request ---
			teamStatic.when(() -> Team.getTeam("myteam")).thenReturn(mockTeam);
			placeholderServiceStatic.when(() -> TeamPlaceholderService.getPlaceholder("score", mockTeam, null))
					.thenReturn("100");

			String result1 = teamPlaceholders.onRequest(null, "static_score_MyTeam");
			assertEquals("100", result1);

			// Verify the underlying service was called
			placeholderServiceStatic.verify(() -> TeamPlaceholderService.getPlaceholder("score", mockTeam, null), times(1));

			// --- Second Request ---
			String result2 = teamPlaceholders.onRequest(null, "static_score_MyTeam");
			assertEquals("100", result2);

			// Verify the service was NOT called again, proving the cache was hit
			placeholderServiceStatic.verifyNoMoreInteractions();
		}

		@Test
		@DisplayName("invalidateCache() should clear the cache")
		void testInvalidateCache() {
			teamStatic.when(() -> Team.getTeam("myteam")).thenReturn(mockTeam);
			placeholderServiceStatic.when(() -> TeamPlaceholderService.getPlaceholder("score", mockTeam, null))
					.thenReturn("100");

			// First request to populate the cache
			teamPlaceholders.onRequest(null, "static_score_MyTeam");
			placeholderServiceStatic.verify(() -> TeamPlaceholderService.getPlaceholder("score", mockTeam, null), times(1));

			// Invalidate the cache
			teamPlaceholders.invalidateCache();

			// Second request should trigger the service call again
			teamPlaceholders.onRequest(null, "static_score_MyTeam");
			placeholderServiceStatic.verify(() -> TeamPlaceholderService.getPlaceholder("score", mockTeam, null), times(2));
		}

		@Test
		@DisplayName("Should not cache player-specific placeholder requests")
		void testPlayerSpecificPlaceholderIsNotCached() {
			teamStatic.when(() -> Team.getTeam(mockPlayer)).thenReturn(mockTeam);
			when(mockTeam.getTeamPlayer(mockPlayer)).thenReturn(mockTeamPlayer);
			placeholderServiceStatic.when(() -> TeamPlaceholderService.getPlaceholder("name", mockTeam, mockTeamPlayer))
					.thenReturn("TeamAlpha");

			// First call
			teamPlaceholders.onRequest(mockPlayer, "name");
			placeholderServiceStatic.verify(() -> TeamPlaceholderService.getPlaceholder("name", mockTeam, mockTeamPlayer), times(1));

			// Second call
			teamPlaceholders.onRequest(mockPlayer, "name");
			// The service should be called again, proving it was not cached
			placeholderServiceStatic.verify(() -> TeamPlaceholderService.getPlaceholder("name", mockTeam, mockTeamPlayer), times(2));
		}
	}
}