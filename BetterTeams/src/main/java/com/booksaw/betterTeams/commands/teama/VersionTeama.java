package com.booksaw.betterTeams.commands.teama;

import com.booksaw.betterTeams.CommandResponse;
import com.booksaw.betterTeams.Main;
import com.booksaw.betterTeams.Team;
import com.booksaw.betterTeams.commands.SubCommand;
import com.booksaw.betterTeams.extension.ExtensionWrapper;
import com.booksaw.betterTeams.message.MessageManager;
import com.booksaw.betterTeams.message.ReferencedFormatMessage;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.List;

public class VersionTeama extends SubCommand {

	private final String command;

	public VersionTeama(String command) {
		this.command = command;
	}

	@Override
	public CommandResponse onCommand(CommandSender sender, String label, String[] args) {
		MessageManager.sendMessage(sender, "admin.versionstorage", Team.getTeamManager().getClass().getName());
		MessageManager.sendMessage(sender, "admin.versionversion", Main.plugin.getServer().getVersion());
		MessageManager.sendMessage(sender, "admin.versionlanguage", MessageManager.getLanguage());
		MessageManager.sendMessage(sender, "admin.versiononline", Boolean.toString(Bukkit.getOnlineMode()));
		MessageManager.sendMessage(sender, "admin.versionplayers", Integer.toString(Bukkit.getOnlinePlayers().size()));
		MessageManager.sendMessage(sender, "admin.versionplugins", getPluginIntegrations());
		MessageManager.sendMessage(sender, "admin.versionconflicts", getConflictingPlugins());
		MessageManager.sendMessage(sender, "admin.versionextensions", getEnabledExtensions());
		return new CommandResponse(true,
				new ReferencedFormatMessage("admin.version", Main.plugin.getDescription().getVersion()));


	}

	@Override
	public String getCommand() {
		return command;
	}

	@Override
	public String getNode() {
		return "admin.version";
	}

	@Override
	public String getHelp() {
		return "Check the plugin version";
	}

	@Override
	public String getArguments() {
		return "";
	}

	@Override
	public int getMinimumArguments() {
		return 0;
	}

	@Override
	public int getMaximumArguments() {
		return 0;
	}

	@Override
	public void onTabComplete(List<String> options, CommandSender sender, String label, String[] args) {
	}

	private String getPluginIntegrations() {

		String placeholderAPI = (Main.placeholderAPI ? ChatColor.GREEN + "ENA-" : ChatColor.RED + "DIS-") + "PlaceholderAPI";
		String ultimateClaims = (Main.plugin.isUltimateClaimsEnabled() ? ChatColor.GREEN + "ENA-" : ChatColor.RED + "DIS-") + "UltimateClaims";
		String vault = (Main.econ != null ? ChatColor.GREEN + "ENA-" : ChatColor.RED + "DIS-") + "Vault";
		String holograms = ChatColor.RED + "noHologram";
		if (Bukkit.getPluginManager().isPluginEnabled("HolographicDisplays")) {
			holograms = ChatColor.GREEN + "HolographicDisplays";
		} else if (Bukkit.getPluginManager().isPluginEnabled("DecentHolograms")) {
			holograms = ChatColor.GREEN + "DecentHolograms";
		}

		return placeholderAPI + " " + ultimateClaims + " " + vault + " " + holograms;
	}

	private String getConflictingPlugins() {
		String plugins = "";

		if (Bukkit.getPluginManager().isPluginEnabled("Geyser-Spigot")) {
			plugins = plugins + "Geyser-Spigot ";
		}

		if (plugins.isEmpty()) {
			return MessageManager.getMessage("admin.versionnoconflicts");
		}

		return plugins;
	}

	private String getEnabledExtensions() {
		if (Main.plugin.getExtensionManager() == null) {
			return MessageManager.getMessage("admin.versionnomanager");
		}

		List<ExtensionWrapper> enabled = Main.plugin.getExtensionManager().getStore().getWrappersByState(true);

		if (enabled.isEmpty()) {
			return MessageManager.getMessage("admin.versionnoextensions");
		}

		StringBuilder sb = new StringBuilder();
		for (ExtensionWrapper wrapper : enabled) {
			if (!sb.isEmpty()) {
				sb.append(ChatColor.WHITE).append(", ");
			}
			sb.append(ChatColor.GREEN).append(wrapper.getInfo().getName());
			sb.append(ChatColor.GRAY).append(" v").append(wrapper.getInfo().getVersion());
		}

		return sb.toString();
	}


}
