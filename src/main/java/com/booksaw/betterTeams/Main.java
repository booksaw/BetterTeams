package com.booksaw.betterTeams;

import com.booksaw.betterTeams.commands.HelpCommand;
import com.booksaw.betterTeams.commands.ParentCommand;
import com.booksaw.betterTeams.commands.PermissionParentCommand;
import com.booksaw.betterTeams.commands.team.*;
import com.booksaw.betterTeams.commands.team.chest.ChestCheckCommand;
import com.booksaw.betterTeams.commands.team.chest.ChestClaimCommand;
import com.booksaw.betterTeams.commands.team.chest.ChestRemoveCommand;
import com.booksaw.betterTeams.commands.team.chest.ChestRemoveallCommand;
import com.booksaw.betterTeams.commands.teama.*;
import com.booksaw.betterTeams.commands.teama.chest.*;
import com.booksaw.betterTeams.commands.teama.money.AddMoney;
import com.booksaw.betterTeams.commands.teama.money.RemoveMoney;
import com.booksaw.betterTeams.commands.teama.money.SetMoney;
import com.booksaw.betterTeams.commands.teama.score.AddScore;
import com.booksaw.betterTeams.commands.teama.score.RemoveScore;
import com.booksaw.betterTeams.commands.teama.score.SetScore;
import com.booksaw.betterTeams.cooldown.CooldownManager;
import com.booksaw.betterTeams.cost.CostManager;
import com.booksaw.betterTeams.events.*;
import com.booksaw.betterTeams.events.MCTeamManagement.BelowNameType;
import com.booksaw.betterTeams.integrations.UltimateClaimsManager;
import com.booksaw.betterTeams.integrations.WorldGuardManagerV7;
import com.booksaw.betterTeams.integrations.ZKothManager;
import com.booksaw.betterTeams.integrations.hologram.DHHologramManager;
import com.booksaw.betterTeams.integrations.hologram.HDHologramManager;
import com.booksaw.betterTeams.integrations.hologram.HologramManager;
import com.booksaw.betterTeams.integrations.placeholder.TeamPlaceholders;
import com.booksaw.betterTeams.message.MessageManager;
import com.booksaw.betterTeams.score.ScoreManagement;
import com.booksaw.betterTeams.team.storage.StorageType;
import com.booksaw.betterTeams.team.storage.convert.Converter;
import com.booksaw.betterTeams.team.storage.storageManager.YamlStorageManager;
import com.booksaw.betterTeams.util.WebhookHandler;
import lombok.Getter;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.SimplePie;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.logging.Level;

/**
 * Main class of the plugin, extends JavaPlugin
 *
 * @author booksaw
 */
public class Main extends JavaPlugin {

	public static Main plugin;
	public static Economy econ = null;
	public static Permission perms = null;
	public static boolean placeholderAPI = false;
	public boolean useHolograms = false;
	public MCTeamManagement teamManagement;
	public ChatManagement chatManagement;
	public WorldGuardManagerV7 wgManagement;
	@Getter
	private PermissionParentCommand teamCommand;

	@Getter
	private BooksawCommand teamBooksawCommand;

	private Metrics metrics = null;

	/**
	 * This is used to store the config file in which the the teams data is stored
	 */
	FileConfiguration teams;
	private DamageManagement damageManagement;

	private ConfigManager configManager;

	@Override
	public void onLoad() {
		plugin = this;
		configManager = new ConfigManager("config", true);

		if (Bukkit.getPluginManager().getPlugin("WorldGuard") != null
				&& configManager.config.getBoolean("worldGuard.enabled")) {
			char ver = Bukkit.getPluginManager().getPlugin("WorldGuard").getDescription().getVersion().charAt(0);
			if (ver == '7') {
				wgManagement = new WorldGuardManagerV7();
			} else {
				Main.plugin.getLogger().warning("Your version of worldgaurd ("
						+ Bukkit.getPluginManager().getPlugin("WorldGuard").getDescription().getVersion()
						+ ") is not yet supported (Currently supported: version 7.x.x), the betterteams flags will not be usable");
			}
		}
	}

	@Override
	public void onEnable() {
		setupMetrics();

		String language = getConfig().getString("language");
		MessageManager.setLanguage(language);
		if (Objects.requireNonNull(language).equals("en") || language.isEmpty()) {
			MessageManager.setLanguage("messages");
		}

		loadCustomConfigs();

		setupStorage();

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

		useHolograms = setupHolograms();

		if (!setupEconomy() || !getConfig().getBoolean("useVault")) {
			econ = null;
		}

		if (!setupPermissions()) {
			perms = null;
		}

		setupCommands();
		setupListeners();
	}

	@Override
	public void onDisable() {

		for (Entry<Player, Team> temp : InventoryManagement.adminViewers.entrySet()) {
			temp.getKey().closeInventory();
			temp.getValue().saveEchest();
		}

		if (useHolograms) {
			HologramManager.holoManager.disable();
		}

		if (teamManagement != null) {
			teamManagement.removeAll(false);
		}

		Team.disable();

		MessageManager.dumpMessages();

	}

	public void loadCustomConfigs() {

		File f = MessageManager.getFile();
		String language = MessageManager.getLanguage();
		try {
			if (!f.exists()) {
				saveResource(language + ".yml", false);
			}
		} catch (Exception e) {
			Main.plugin.getLogger().warning("Could not load selected language: " + language
					+ " go to https://github.com/booksaw/BetterTeams/wiki/Language to view a list of supported languages");
			Main.plugin.getLogger().warning("Reverting to english so the plugin can still function");
			MessageManager.setLanguage("messages");
			loadCustomConfigs();
			return;
		}

		ConfigManager messagesConfigManager = new ConfigManager(language, true);

		MessageManager.addMessages(messagesConfigManager);

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
				Main.plugin.getLogger().log(Level.WARNING, "Restart server for damage changes to apply");
			}
		}

		// loading the fully custom help message option
		HelpCommand.setupHelp();

	}

	/*
	 * Determines which holograms plugin the server is running, then creates a new
	 * HologramManager instance for the respective plugin.
	 */
	private boolean setupHolograms() {
		boolean hdHolos = Bukkit.getPluginManager().isPluginEnabled("HolographicDisplays");
		if (hdHolos) {
			new HDHologramManager();
		}
		boolean dhHolos = Bukkit.getPluginManager().isPluginEnabled("DecentHolograms");
		// Check to make sure the server isn't running both hologram plugins.
		// We don't need two HologramManager instances.
		if (!hdHolos && dhHolos) {
			new DHHologramManager();
		}
		return hdHolos || dhHolos;
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

	private boolean setupPermissions() {
		if (getServer().getPluginManager().getPlugin("Vault") == null) {
			return false;
		}

		RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
		if (rsp == null) {
			return false;
		}
		perms = rsp.getProvider();
		return perms != null;
	}

	public void reload() {

		onDisable();
		teamManagement = null;
		reloadConfig();
		configManager = new ConfigManager("config", true);

		ChatManagement.enable();
		damageManagement = null;
		onEnable();

	}

	public void setupCommands() {
		teamCommand = new PermissionParentCommand(new CostManager("team"), new CooldownManager("team"), "team");
		// add all sub commands here
		teamCommand.addSubCommands(new CreateCommand(teamCommand), new LeaveCommand(), new DisbandCommand(),
				new DescriptionCommand(), new InviteCommand(), new JoinCommand(), new NameCommand(), new OpenCommand(),
				new InfoCommand(teamCommand), new KickCommand(), new PromoteCommand(), new DemoteCommand(),
				new HomeCommand(), new SethomeCommand(), new BanCommand(), new UnbanCommand(),
				new ChatCommand(teamCommand), new ColorCommand(), new TitleCommand(), new TopCommand(),
				new BaltopCommand(), new RankCommand(), new DelHome(), new AllyCommand(), new NeutralCommand(),
				new AllyChatCommand(teamCommand), new ListCommand(), new WarpCommand(), new SetWarpCommand(),
				new DelwarpCommand(), new WarpsCommand(), new EchestCommand(), new RankupCommand(), new TagCommand(),
				new AnchorCommand(), new SetAnchorCommand());

		if (getConfig().getBoolean("disableCombat")) {
			teamCommand.addSubCommand(new PvpCommand());
		}
		// only used if a team is only allowed a single owner
		if (getConfig().getBoolean("singleOwner")) {
			teamCommand.addSubCommand(new SetOwnerCommand());
		}

		ParentCommand chest = new PermissionParentCommand("chest");
		chest.addSubCommands(new ChestClaimCommand(), new ChestRemoveCommand(), new ChestRemoveallCommand(), new ChestCheckCommand());
		teamCommand.addSubCommand(chest);

		teamBooksawCommand = new BooksawCommand("team", teamCommand, "betterteams.standard", "All commands for teams",
				getConfig().getStringList("command.team"));

		ParentCommand teamaCommand = new ParentCommand("teamadmin");

		teamaCommand.addSubCommands(new ReloadTeama(), new ChatSpyTeama(), new TitleTeama(),
				new VersionTeama("version"), new VersionTeama("debug"), new HomeTeama(), new NameTeama(),
				new DescriptionTeama(), new OpenTeama(), new InviteTeama(), new CreateTeama(), new JoinTeama(),
				new LeaveTeama(), new PromoteTeama(), new DemoteTeama(), new WarpTeama(), new SetwarpTeama(),
				new DelwarpTeama(), new PurgeTeama(), new DisbandTeama(), new ColorTeama(), new EchestTeama(),
				new SetrankTeama(teamaCommand), new TagTeama(), new TeleportTeama(teamaCommand), new AllyTeama(),
				new NeutralTeama(), new ImportmessagesTeama(), new AnchorTeama(), new SetAnchorTeama());

		if (getConfig().getBoolean("singleOwner")) {
			teamaCommand.addSubCommand(new SetOwnerTeama());
		}

		ParentCommand teamaScoreCommand = new ParentCommand("score");
		teamaScoreCommand.addSubCommands(new AddScore(), new SetScore(), new RemoveScore());
		teamaCommand.addSubCommand(teamaScoreCommand);

		ParentCommand teamaMoneyCommand = new ParentCommand("money");
		teamaMoneyCommand.addSubCommands(new AddMoney(), new SetMoney(), new RemoveMoney());
		teamaCommand.addSubCommand(teamaMoneyCommand);

		ParentCommand teamaChestCommand = new ParentCommand("chest");
		teamaChestCommand.addSubCommands(new ChestClaimTeama(), new ChestRemoveTeama(), new ChestRemoveallTeama(),
				new ChestEnableClaims(), new ChestDisableClaims());
		teamaCommand.addSubCommand(teamaChestCommand);

		if (useHolograms) {
			ParentCommand teamaHoloCommand = new ParentCommand("holo");
			teamaHoloCommand.addSubCommands(new CreateHoloTeama(), new RemoveHoloTeama());
			teamaCommand.addSubCommand(teamaHoloCommand);
		}

		if (econ != null) {
			teamCommand.addSubCommands(new DepositCommand(teamCommand), new BalCommand(),
					new WithdrawCommand(teamCommand));
		}

		new BooksawCommand("teamadmin", teamaCommand, "betterteams.admin", "All admin commands for teams",
				getConfig().getStringList("command.teama"));

	}

	public void setupListeners() {
		Main.plugin.getLogger().info("Display team name config value: " + getConfig().getString("displayTeamName"));
		BelowNameType type = BelowNameType.getType(Objects.requireNonNull(getConfig().getString("displayTeamName")));
		Main.plugin.getLogger().info("Loading below name. Type: " + type);
		if (getConfig().getBoolean("useTeams")) {
			if (teamManagement == null) {

				teamManagement = new MCTeamManagement(type);

				Bukkit.getScheduler().runTaskAsynchronously(this, () -> teamManagement.displayBelowNameForAll());
				getServer().getPluginManager().registerEvents(teamManagement, this);
				Main.plugin.getLogger().info("teamManagement declared: " + teamManagement);
			}
		} else {
			Main.plugin.getLogger().info("Not loading management");
			if (teamManagement != null) {
				Main.plugin.getLogger().log(Level.WARNING, "Restart server for minecraft team changes to apply");
			}
		}

		// loading the zkoth event listener
		if (getServer().getPluginManager().isPluginEnabled("zKoth")) {
			Main.plugin.getLogger().info("Found plugin zKoth, adding plugin integration");
			getServer().getPluginManager().registerEvents(new ZKothManager(), this);
		}

		getServer().getPluginManager().registerEvents((chatManagement = new ChatManagement()), this);
		getServer().getPluginManager().registerEvents(new ScoreManagement(), this);
		getServer().getPluginManager().registerEvents(new AllyManagement(), this);
		getServer().getPluginManager().registerEvents(new MessagesManagement(), this);

		// Only register webhook when hook support is enabled
		if (getConfig().getBoolean("hookSupport")) {
			getServer().getPluginManager().registerEvents(new WebhookHandler(), this);
		}

		if (getConfig().getBoolean("checkUpdates")) {
			getServer().getPluginManager().registerEvents(new UpdateChecker(this), this);
		}

		// disabling the chest checks (hoppers most importantly) to reduce needless
		// performance cost
		if (teamCommand.isEnabled("chest")) {
			getServer().getPluginManager().registerEvents(new ChestManagement(), this);
		}

		getServer().getPluginManager().registerEvents(new InventoryManagement(), this);
		getServer().getPluginManager().registerEvents(new RankupEvents(), this);
		if (getConfig().getBoolean("anchor.enable")){
			HomeAnchorManagement homeAnchorListener = new HomeAnchorManagement(this);
    		homeAnchorListener.registerEvent();
		}
	}

	public void setupMetrics() {
		if (metrics == null) {
			int pluginId = 7855;
			metrics = new Metrics(this, pluginId);
			metrics.addCustomChart(new SimplePie("language", () -> getConfig().getString("language")));
			metrics.addCustomChart(new SimplePie("storage_type", () -> getConfig().getString("storageType")));
		}
	}

	@Override
	public @NotNull FileConfiguration getConfig() {
		return configManager.config;
	}

	public void setupStorage() {
		File f = new File("plugins/BetterTeams/" + YamlStorageManager.TEAMLISTSTORAGELOC + ".yml");

		if (!f.exists()) {
			Main.plugin.saveResource("teams.yml", false);
		}

		YamlConfiguration teamStorage = YamlConfiguration.loadConfiguration(f);
		StorageType from = StorageType.getStorageType(teamStorage.getString("storageType", "FLATFILE"));
		StorageType to = StorageType.getStorageType(getConfig().getString("storageType", ""));

		if (from != to) {
			Converter converter = Converter.getConverter(from, to);

			if (converter == null) {
				Main.plugin.getLogger().info("Cannot convert to the selected storage type (" + to.toString()
						+ "), continuing with preexisting one (" + from.toString() + ")");
				to = from;
			} else {
				converter.convertStorage();
			}
		}

		Team.setupTeamManager(to);
		Team.getTeamManager().loadTeams();
	}
}