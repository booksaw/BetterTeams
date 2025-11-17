package com.booksaw.betterTeams.commands;

import com.booksaw.betterTeams.CommandResponse;
import com.booksaw.betterTeams.Main;
import com.booksaw.betterTeams.message.MessageManager;
import com.booksaw.betterTeams.text.Formatter;
import com.booksaw.betterTeams.text.LegacyTextUtils;
import net.kyori.adventure.text.Component;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ClickEvent.Action;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.command.CommandSender;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.logging.Level;

public class HelpCommand extends SubCommand {

	private final static int COMMANDS_PER_PAGE = 7;

	public static String prefix, description;
	private static boolean fullyCustom = false;
	final ParentCommand command;

	public HelpCommand(ParentCommand command) {
		this.command = command;
		prefix = "&" + Optional.ofNullable(Main.plugin.getConfig().getString("helpCommandColor")).orElse("b");
		description = "&" + Optional.ofNullable(Main.plugin.getConfig().getString("helpDescriptionColor")).orElse("b");

		if (prefix.length() == 1) {
			prefix = "&" + prefix;
		}
		if (description.length() == 1) {
			description = "&" + description;
		}

	}

	public static void setupHelp() {
		fullyCustom = Main.plugin.getConfig().getBoolean("fullyCustomHelpMessages");
	}

	@Override
	public CommandResponse onCommand(CommandSender sender, String label, String[] args) {

		if (fullyCustom) {
			fullyCustom(sender, label);
			return null;
		}

		// Send specific help message if command found
		if (args.length != 0 && command.getSubCommands().containsKey(args[0])) {
			SubCommand subcommand = command.getSubCommands().get(args[0]);
			MessageManager.sendFullMessage(sender, createHelpMessage(label,
					subcommand.getCommandAndArgMessage(command),
					subcommand.getHelpMessage(command)));
			return null;
		}

		List<SubCommand> permissiveCommands = new ArrayList<>();
		command.getSubCommands().values().forEach(c -> {
			if (sender.hasPermission("betterteams." + c.getNode())) {
				permissiveCommands.add(c);
			}
		});

		int maxPage = permissiveCommands.size() / COMMANDS_PER_PAGE
				+ ((permissiveCommands.size() % COMMANDS_PER_PAGE == 0) ? 0 : 1);
		int page;
		if (args.length > 0) {
			page = getPage(args[0], maxPage);
		} else {
			page = 0;
		}
		MessageManager.sendMessage(sender, "help.header");

		for (int i = COMMANDS_PER_PAGE * page; i < permissiveCommands.size()
				&& i < COMMANDS_PER_PAGE * (page + 1); i++) {
			SubCommand subCommand = permissiveCommands.get(i);
			MessageManager.sendFullMessage(sender, createClickableHelpMiniMessage(label,
					subCommand.getCommandAndArgMessage(command),
					subCommand.getHelpMessage(command)));
		}

		MessageManager.sendMessage(sender, "help.footer", page + 1, maxPage, command.getCommand());

		return null;
	}

	/**
	 * Used to send a fully custom help message which is stored in a file
	 *
	 * @param sender the CommandSender that called the help message
	 * @param label  the label for the message (the base command for example for
	 *               /teamadmin it could be /teama as well)
	 */
	public void fullyCustom(CommandSender sender, String label) {
		File f = new File(Main.plugin.getDataFolder() + File.separator + command.getCommand() + ".txt");

		if (!f.exists()) {
			try {
				f.createNewFile();

				PrintWriter writer = new PrintWriter(f);
				for (Entry<String, SubCommand> sub : command.getSubCommands().entrySet()) {
					writer.println(
							Main.plugin.getConfig().getString("prefixFormat") + "&b/" + label + " " + sub.getKey() + " "
									+ sub.getValue().getArgMessage(command) + "&f - &6"
									+ sub.getValue().getHelpMessage(command));
				}
				writer.close();

			} catch (Exception e) {
				Main.plugin.getLogger().log(Level.SEVERE,

						"Could not use fully custom help messages, inform booksaw (this should never happen)");
				MessageManager.sendMessage(sender, "help.wrong");
				Main.plugin.getLogger().severe(e.toString());
				fullyCustom = false;
			}
		}
		try (BufferedReader reader = new BufferedReader(new FileReader(f))) {
			String line;
			while ((line = reader.readLine()) != null) {
				MessageManager.sendFullMessage(sender, line);
			}
		} catch (Exception e) {
			Main.plugin.getLogger().log(Level.SEVERE,
					"Could not use fully custom help messages, inform booksaw (this should never happen)");
			MessageManager.sendMessage(sender, "help.wrong");
			Main.plugin.getLogger().severe(e.toString());
			fullyCustom = false;
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

		return prefix + "/" + label + " " + commandPath + ChatColor.WHITE + " - " + HelpCommand.description
				+ description;
	}

	public TextComponent createClickableHelpMessage(String label, String commandPath, String description) {

		TextComponent message = new TextComponent(
				LegacyTextUtils.parseAdventure(MessageManager.getPrefix() + prefix + "/" + label + " " + commandPath
						+ ChatColor.WHITE + " - " + HelpCommand.description
						+ description));
		message.setClickEvent(new ClickEvent(Action.SUGGEST_COMMAND, "/" + label + " " + commandPath));
		message.setHoverEvent(
				new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(prefix + "/" + label + " " + commandPath)));

		return message;
	}

	public Component createClickableHelpMiniMessage(String label, String commandPath, String description) {
		Component parsedComponent = Formatter.absolute().process(
				MessageManager.getPrefix() + prefix + "/" + label + " " + commandPath
						+ " <white>-</white> " + HelpCommand.description + description);

		return parsedComponent
				.clickEvent(net.kyori.adventure.text.event.ClickEvent.suggestCommand("/" + label + " " + commandPath))
				.hoverEvent(net.kyori.adventure.text.event.HoverEvent
						.showText(Formatter.absolute().process(prefix + "/" + label + " " + commandPath)));
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

	private int getPage(String request, int maxPage) {

		try {
			int page = Integer.parseInt(request);
			if (page > maxPage) {
				return maxPage;
			}
			return page - 1;
		} catch (NumberFormatException e) {
			return 0;
		}

	}

}
