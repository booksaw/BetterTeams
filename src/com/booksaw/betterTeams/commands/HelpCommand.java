package com.booksaw.betterTeams.commands;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map.Entry;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import com.booksaw.betterTeams.Main;
import com.booksaw.betterTeams.MessageManager;

import net.md_5.bungee.api.ChatColor;

public class HelpCommand extends SubCommand {

	private static boolean fullyCustom = false;

	public static void setupHelp() {
		fullyCustom = Main.plugin.getConfig().getBoolean("fullyCustomHelpMessages");
	}

	ParentCommand command;
	ChatColor prefix, description;

	public HelpCommand(ParentCommand command) {
		this.command = command;
		prefix = ChatColor.getByChar(Main.plugin.getConfig().getString("helpCommandColor").charAt(0));
		description = ChatColor.getByChar(Main.plugin.getConfig().getString("helpDescriptionColor").charAt(0));

	}

	@Override
	public String onCommand(CommandSender sender, String label, String[] args) {

		if (fullyCustom) {
			fullyCustom(sender, label);
			return null;
		}

		for (Entry<String, SubCommand> subCommand : command.getSubCommands().entrySet()) {
			if (sender.hasPermission("betterTeams." + subCommand.getValue().getNode())) {
				sender.sendMessage(
						createHelpMessage(label, subCommand.getKey() + " " + subCommand.getValue().getArguments(),
								subCommand.getValue().getHelpMessage()));
			}
		}

		return null;
	}

	/**
	 * Used to send a fully custom help message which is stored in a file
	 * @param sender the CommandSender that called the help message
	 * @param label the label for the message (the base command for example for /teamadmin it could be /teama as well) 
	 */
	public void fullyCustom(CommandSender sender, String label) {
		File f = new File(Main.plugin.getDataFolder() + File.separator + command.getCommand() + ".txt");

		if (!f.exists()) {
			try {
				f.createNewFile();

				PrintWriter writer = new PrintWriter(f);
				for (Entry<String, SubCommand> sub : command.getSubCommands().entrySet()) {
					writer.println("&b/" + label + " " + sub.getKey() + " " + sub.getValue().getArguments() + "&f - &6"
							+ sub.getValue().getHelpMessage());
				}
				writer.close();

			} catch (Exception e) {
				Bukkit.getLogger().log(Level.SEVERE,

						"Could not use fully custom help messages, inform booksaw (this should never happen)");
				sender.sendMessage(ChatColor.RED + "Something went wrong, inform your server admins");
				e.printStackTrace();
				fullyCustom = false;
			}
		}
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(f));
			String line = "";
			while ((line = reader.readLine()) != null) {
				sender.sendMessage(
						MessageManager.getPrefix() + org.bukkit.ChatColor.translateAlternateColorCodes('&', line));
			}
		} catch (Exception e) {
			Bukkit.getLogger().log(Level.SEVERE,
					"Could not use fully custom help messages, inform booksaw (this should never happen)");
			sender.sendMessage(ChatColor.RED + "Something went wrong, inform your server admins");
			e.printStackTrace();
			fullyCustom = false;
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (Exception e) {
					Bukkit.getLogger().log(Level.SEVERE,
							"Could not use fully custom help messages, inform booksaw (this should never happen)");
					sender.sendMessage(ChatColor.RED + "Something went wrong, inform your server admins");
				}
			}
		}
	}

	/**
	 * Used to create a formatted help message to explain what a command does to the
	 * user
	 * 
	 * @param label       the base command
	 * @param commandPath the rest of the command (i.e. help [param])
	 * @param description the description of the command
	 * @return the created message relating to that command
	 */
	public String createHelpMessage(String label, String commandPath, String description) {
		return prefix + "/" + label + " " + commandPath + ChatColor.WHITE + " - " + this.description + description;
	}

	@Override
	public String getCommand() {
		return "help";
	}

	@Override
	public String getNode() {
		return "";
	}

	@Override
	public String getHelp() {
		return "View this help page";
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
	public void onTabComplete(List<String> options, CommandSender sender, String label, String[] args) {

	}

	@Override
	public int getMaximumArguments() {
		return 0;
	}

}
