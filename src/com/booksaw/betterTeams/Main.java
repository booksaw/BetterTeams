package com.booksaw.betterTeams;

import java.io.File;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import com.booksaw.betterTeams.commands.HelpCommand;
import com.booksaw.betterTeams.commands.ParentCommand;
import com.booksaw.betterTeams.commands.PermissionParentCommand;
import com.booksaw.betterTeams.commands.team.AllyChatCommand;
import com.booksaw.betterTeams.commands.team.AllyCommand;
import com.booksaw.betterTeams.commands.team.BalCommand;
import com.booksaw.betterTeams.commands.team.BaltopCommand;
import com.booksaw.betterTeams.commands.team.BanCommand;
import com.booksaw.betterTeams.commands.team.ChatCommand;
import com.booksaw.betterTeams.commands.team.ColorCommand;
import com.booksaw.betterTeams.commands.team.CreateCommand;
import com.booksaw.betterTeams.commands.team.DelHome;
import com.booksaw.betterTeams.commands.team.DelwarpCommand;
import com.booksaw.betterTeams.commands.team.DemoteCommand;
import com.booksaw.betterTeams.commands.team.DepositCommand;
import com.booksaw.betterTeams.commands.team.DescriptionCommand;
import com.booksaw.betterTeams.commands.team.DisbandCommand;
import com.booksaw.betterTeams.commands.team.EchestCommand;
import com.booksaw.betterTeams.commands.team.HomeCommand;
import com.booksaw.betterTeams.commands.team.InfoCommand;
import com.booksaw.betterTeams.commands.team.InviteCommand;
import com.booksaw.betterTeams.commands.team.JoinCommand;
import com.booksaw.betterTeams.commands.team.KickCommand;
import com.booksaw.betterTeams.commands.team.LeaveCommand;
import com.booksaw.betterTeams.commands.team.ListCommand;
import com.booksaw.betterTeams.commands.team.NameCommand;
import com.booksaw.betterTeams.commands.team.NeutralCommand;
import com.booksaw.betterTeams.commands.team.OpenCommand;
import com.booksaw.betterTeams.commands.team.PromoteCommand;
import com.booksaw.betterTeams.commands.team.PvpCommand;
import com.booksaw.betterTeams.commands.team.RankCommand;
import com.booksaw.betterTeams.commands.team.RankupCommand;
import com.booksaw.betterTeams.commands.team.SetOwnerCommand;
import com.booksaw.betterTeams.commands.team.SetWarpCommand;
import com.booksaw.betterTeams.commands.team.SethomeCommand;
import com.booksaw.betterTeams.commands.team.TagCommand;
import com.booksaw.betterTeams.commands.team.TitleCommand;
import com.booksaw.betterTeams.commands.team.TopCommand;
import com.booksaw.betterTeams.commands.team.UnbanCommand;
import com.booksaw.betterTeams.commands.team.WarpCommand;
import com.booksaw.betterTeams.commands.team.WarpsCommand;
import com.booksaw.betterTeams.commands.team.WithdrawCommand;
import com.booksaw.betterTeams.commands.team.chest.ChestClaimCommand;
import com.booksaw.betterTeams.commands.team.chest.ChestRemoveCommand;
import com.booksaw.betterTeams.commands.team.chest.ChestRemoveallCommand;
import com.booksaw.betterTeams.commands.teama.ChatSpyTeama;
import com.booksaw.betterTeams.commands.teama.ColorTeama;
import com.booksaw.betterTeams.commands.teama.CreateHoloTeama;
import com.booksaw.betterTeams.commands.teama.CreateTeama;
import com.booksaw.betterTeams.commands.teama.DelwarpTeama;
import com.booksaw.betterTeams.commands.teama.DemoteTeama;
import com.booksaw.betterTeams.commands.teama.DescriptionTeama;
import com.booksaw.betterTeams.commands.teama.DisbandTeama;
import com.booksaw.betterTeams.commands.teama.EchestTeama;
import com.booksaw.betterTeams.commands.teama.HomeTeama;
import com.booksaw.betterTeams.commands.teama.InviteTeama;
import com.booksaw.betterTeams.commands.teama.JoinTeama;
import com.booksaw.betterTeams.commands.teama.LeaveTeama;
import com.booksaw.betterTeams.commands.teama.NameTeama;
import com.booksaw.betterTeams.commands.teama.OpenTeama;
import com.booksaw.betterTeams.commands.teama.PromoteTeama;
import com.booksaw.betterTeams.commands.teama.PurgeTeama;
import com.booksaw.betterTeams.commands.teama.ReloadTeama;
import com.booksaw.betterTeams.commands.teama.RemoveHoloTeama;
import com.booksaw.betterTeams.commands.teama.SetOwnerTeama;
import com.booksaw.betterTeams.commands.teama.SetrankTeama;
import com.booksaw.betterTeams.commands.teama.SetwarpTeama;
import com.booksaw.betterTeams.commands.teama.TagTeama;
import com.booksaw.betterTeams.commands.teama.TitleTeama;
import com.booksaw.betterTeams.commands.teama.VersionTeama;
import com.booksaw.betterTeams.commands.teama.WarpTeama;
import com.booksaw.betterTeams.commands.teama.chest.ChestClaimTeama;
import com.booksaw.betterTeams.commands.teama.chest.ChestDisableClaims;
import com.booksaw.betterTeams.commands.teama.chest.ChestEnableClaims;
import com.booksaw.betterTeams.commands.teama.chest.ChestRemoveTeama;
import com.booksaw.betterTeams.commands.teama.chest.ChestRemoveallTeama;
import com.booksaw.betterTeams.commands.teama.score.AddScore;
import com.booksaw.betterTeams.commands.teama.score.RemoveScore;
import com.booksaw.betterTeams.commands.teama.score.SetScore;
import com.booksaw.betterTeams.cooldown.CooldownManager;
import com.booksaw.betterTeams.cost.CostManager;
import com.booksaw.betterTeams.events.AllyManagement;
import com.booksaw.betterTeams.events.ChatManagement;
import com.booksaw.betterTeams.events.ChestManagement;
import com.booksaw.betterTeams.events.DamageManagement;
import com.booksaw.betterTeams.events.InventoryManagement;
import com.booksaw.betterTeams.events.MCTeamManagement;
import com.booksaw.betterTeams.events.MCTeamManagement.BelowNameType;
import com.booksaw.betterTeams.events.RankupEvents;
import com.booksaw.betterTeams.integrations.HologramManager;
import com.booksaw.betterTeams.integrations.TeamPlaceholders;
import com.booksaw.betterTeams.integrations.UltimateClaimsManager;
import com.booksaw.betterTeams.integrations.WorldGaurdManager;
import com.booksaw.betterTeams.integrations.ZKothManager;
import com.booksaw.betterTeams.message.MessageManager;
import com.booksaw.betterTeams.metrics.Metrics;
import com.booksaw.betterTeams.score.ScoreManagement;
import com.booksaw.betterTeams.team.storage.StorageType;

import net.milkbowl.vault.economy.Economy;

/**
 * Main class of the plugin, extends JavaPlugin
 *
 * @author booksaw
 */
public class Main extends JavaPlugin {

	public static Main plugin;
	public static Economy econ = null;
	public static boolean placeholderAPI = false;
	public boolean useHolographicDisplays;
	public MCTeamManagement teamManagement;
	public ChatManagement chatManagement;
	public WorldGaurdManager wgManagement;

	private Metrics metrics = null;

	/**
	 * This is used to store the config file in which the the teams data is stored
	 */
	FileConfiguration teams;
	private DamageManagement damageManagement;

	private ConfigManager configManager;

	@Override
	public void onLoad() {
		if (Bukkit.getPluginManager().getPlugin("WorldGuard") != null) {
			wgManagement = new WorldGaurdManager();
		}
	}

	@Override
	public void onEnable() {
		plugin = this;

		configManager = new ConfigManager("config", true);

		setupMetrics();

		Team.setupTeamManager(StorageType.getStorageType(getConfig().getString("storageType")));
		Team.getTeamManager().loadTeams();

		String language = getConfig().getString("language");
		MessageManager.setLanguage(language);
		if (Objects.requireNonNull(language).equals("en") || language.equals("")) {
			MessageManager.setLanguage("messages");
		}

		loadCustomConfigs();
		ChatManagement.enable();

		if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null
				&& Objects.requireNonNull(Bukkit.getPluginManager().getPlugin("PlaceholderAPI")).isEnabled()) {
			placeholderAPI = true;
			new TeamPlaceholders(this).register();
		}

		if (Bukkit.getPluginManager().getPlugin("UltimateClaims") != null
				&& Objects.requireNonNull(Bukkit.getPluginManager().getPlugin("UltimateClaims")).isEnabled()) {
			if (getConfig().getBoolean("ultimateClaims.enabled")) {
				new UltimateClaimsManager();
			}
		}

		useHolographicDisplays = (Bukkit.getPluginManager().getPlugin("HolographicDisplays") != null
				&& Bukkit.getPluginManager().isPluginEnabled("HolographicDisplays"));

		if (useHolographicDisplays) {
			updateHolos();
		}

		if (!setupEconomy() || !getConfig().getBoolean("useVault")) {
			econ = null;
		}

		setupListeners();
		setupCommands();
	}

	@Override
	public void onDisable() {

		for (Entry<Player, Team> temp : InventoryManagement.adminViewers.entrySet()) {
			temp.getKey().closeInventory();
			temp.getValue().saveEchest();
		}

		if (useHolographicDisplays) {
			HologramManager.holoManager.disable();
		}

		if (teamManagement != null) {
			teamManagement.removeAll();
		}

	}

	public void loadCustomConfigs() {

		File f = MessageManager.getFile();
		String language = MessageManager.getLanguage();
		try {
			if (!f.exists()) {
				saveResource(language + ".yml", false);
			}
		} catch (Exception e) {
			Bukkit.getLogger().warning("Could not load selected language: " + language
					+ " go to https://github.com/booksaw/BetterTeams/wiki/Language to view a list of supported languages");
			Bukkit.getLogger().warning("Reverting to english so the plugin can still function");
			MessageManager.setLanguage("messages");
			loadCustomConfigs();
			return;
		}

		ConfigManager messagesConfigManager = new ConfigManager(language, true);

		MessageManager.addMessages(messagesConfigManager.config);

		if (!language.equals("messages")) {
			messagesConfigManager = new ConfigManager("messages", true);
			MessageManager.addBackupMessages(messagesConfigManager.config);
		}

		if (getConfig().getBoolean("disableCombat")) {
			if (damageManagement == null) {
				damageManagement = new DamageManagement();
				getServer().getPluginManager().registerEvents(damageManagement, this);
			}

		} else {
			if (damageManagement != null) {
				Bukkit.getLogger().log(Level.WARNING, "Restart server for damage changes to apply");
			}
		}

		// loading the fully custom help message option
		HelpCommand.setupHelp();

	}

	/**
	 * Used to manage all holograms information
	 */
	public void updateHolos() {
		new HologramManager();
	}

	private boolean setupEconomy() {
		if (getServer().getPluginManager().getPlugin("Vault") == null) {
			return false;
		}
		RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
		if (rsp == null) {
			return false;
		}
		econ = rsp.getProvider();
		return true;
	}

	public void reload() {

		onDisable();
		teamManagement = null;
		reloadConfig();
		ChatManagement.enable();
		damageManagement = null;
		onEnable();

	}

	public void setupCommands() {
		ParentCommand teamCommand = new PermissionParentCommand(new CostManager("team"), new CooldownManager("team"),
				"team");
		// add all sub commands here
		teamCommand.addSubCommands(new CreateCommand(), new LeaveCommand(), new DisbandCommand(),
				new DescriptionCommand(), new InviteCommand(), new JoinCommand(), new NameCommand(), new OpenCommand(),
				new InfoCommand(), new KickCommand(), new PromoteCommand(), new DemoteCommand(), new HomeCommand(),
				new SethomeCommand(), new BanCommand(), new UnbanCommand(), new ChatCommand(), new ColorCommand(),
				new TitleCommand(), new TopCommand(), new BaltopCommand(), new RankCommand(), new DelHome(),
				new AllyCommand(), new NeutralCommand(), new AllyChatCommand(), new ListCommand(), new WarpCommand(),
				new SetWarpCommand(), new DelwarpCommand(), new WarpsCommand(), new EchestCommand(),
				new RankupCommand(), new TagCommand());

		if (getConfig().getBoolean("disableCombat")) {
			teamCommand.addSubCommand(new PvpCommand());
		}
		// only used if a team is only allowed a single owner
		if (getConfig().getBoolean("singleOwner")) {
			teamCommand.addSubCommand(new SetOwnerCommand());
		}

		ParentCommand chest = new PermissionParentCommand("chest");
		chest.addSubCommands(new ChestClaimCommand(), new ChestRemoveCommand(), new ChestRemoveallCommand());
		teamCommand.addSubCommand(chest);

		new BooksawCommand("team", teamCommand, "betterteams.standard", "All commands for teams",
				getConfig().getStringList("command.team"));

		ParentCommand teamaCommand = new ParentCommand("teamadmin");

		teamaCommand.addSubCommands(new ReloadTeama(), new ChatSpyTeama(), new TitleTeama(), new VersionTeama(),
				new HomeTeama(), new NameTeama(), new DescriptionTeama(), new OpenTeama(), new InviteTeama(),
				new CreateTeama(), new JoinTeama(), new LeaveTeama(), new PromoteTeama(), new DemoteTeama(),
				new WarpTeama(), new SetwarpTeama(), new DelwarpTeama(), new PurgeTeama(), new DisbandTeama(),
				new ColorTeama(), new EchestTeama(), new SetrankTeama(), new TagTeama());

		if (getConfig().getBoolean("singleOwner")) {
			teamaCommand.addSubCommand(new SetOwnerTeama());
		}

		ParentCommand teamaScoreCommand = new ParentCommand("score");
		teamaScoreCommand.addSubCommands(new AddScore(), new SetScore(), new RemoveScore());
		teamaCommand.addSubCommand(teamaScoreCommand);

		ParentCommand teamaChestCommand = new ParentCommand("chest");
		teamaChestCommand.addSubCommands(new ChestClaimTeama(), new ChestRemoveTeama(), new ChestRemoveallTeama(),
				new ChestEnableClaims(), new ChestDisableClaims());
		teamaCommand.addSubCommand(teamaChestCommand);

		if (useHolographicDisplays) {
			ParentCommand teamaHoloCommand = new ParentCommand("holo");
			teamaHoloCommand.addSubCommands(new CreateHoloTeama(), new RemoveHoloTeama());
			teamaCommand.addSubCommand(teamaHoloCommand);
		}

		if (econ != null) {
			teamCommand.addSubCommands(new DepositCommand(), new BalCommand(), new WithdrawCommand());
		}

		new BooksawCommand("teamadmin", teamaCommand, "betterteams.admin", "All admin commands for teams",
				getConfig().getStringList("command.teama"));

	}

	public void setupListeners() {
		Bukkit.getLogger().info("Display team name config value: " + getConfig().getString("displayTeamName"));
		BelowNameType type = BelowNameType.getType(Objects.requireNonNull(getConfig().getString("displayTeamName")));
		Bukkit.getLogger().info("Loading below name. Type: " + type);
		if (getConfig().getBoolean("useTeams")) {
			if (teamManagement == null) {

				teamManagement = new MCTeamManagement(type);
				teamManagement.displayBelowNameForAll();
				getServer().getPluginManager().registerEvents(teamManagement, this);
				Bukkit.getLogger().info("teamManagement declared: " + teamManagement);
			}
		} else {
			Bukkit.getLogger().info("Not loading management");
			if (teamManagement != null) {
				Bukkit.getLogger().log(Level.WARNING, "Restart server for minecraft team changes to apply");
			}
		}

		// loading the zkoth event listener
		if (getServer().getPluginManager().isPluginEnabled("zKoth")) {
			Bukkit.getLogger().info("Found plugin zKoth, adding plugin integration");
			getServer().getPluginManager().registerEvents(new ZKothManager(), this);
		}

		getServer().getPluginManager().registerEvents((chatManagement = new ChatManagement()), this);
		getServer().getPluginManager().registerEvents(new ScoreManagement(), this);
		getServer().getPluginManager().registerEvents(new AllyManagement(), this);
		getServer().getPluginManager().registerEvents(new UpdateChecker(this), this);
		getServer().getPluginManager().registerEvents(new ChestManagement(), this);
		getServer().getPluginManager().registerEvents(new InventoryManagement(), this);
		getServer().getPluginManager().registerEvents(new RankupEvents(), this);

	}

	public void setupMetrics() {
		if (metrics == null) {
			int pluginId = 7855;
			metrics = new Metrics(this, pluginId);
			metrics.addCustomChart(new Metrics.SimplePie("language", () -> getConfig().getString("language")));
		}
	}

	@Override
	public FileConfiguration getConfig() {
		return configManager.config;
	}
}
