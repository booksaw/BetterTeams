package com.booksaw.betterTeams.commands.teama.meta;

import com.booksaw.betterTeams.CommandResponse;
import com.booksaw.betterTeams.Team;
import com.booksaw.betterTeams.commands.presets.TeamSelectSubCommand;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.List;

public class MetaSetTeama extends TeamSelectSubCommand {
	@Override
	public CommandResponse onCommand(CommandSender sender, String label, String[] args, Team team) {
		String key = args[1];
		List<String> valueParts = Arrays.asList(Arrays.copyOfRange(args, 2, args.length));
		String valueStr = String.join(" ", valueParts);

		if (valueStr.isEmpty()) {
			return new CommandResponse(false, "admin.meta.set.needsValue");
		}

		team.setAndSaveMeta(key, valueStr);
		return new CommandResponse(true, "admin.meta.set.success");

	}

	@Override
	public String getCommand() {
		return "set";
	}

	@Override
	public String getNode() {
		return "admin.meta.set";
	}

	@Override
	public String getHelp() {
		return "Set a metadata value for a specific team";
	}

	@Override
	public String getArguments() {
		return "<team> <key> <value>";
	}

	@Override
	public int getMinimumArguments() {
		return 3;
	}

	@Override
	public int getMaximumArguments() {
		return -1;
	}

	@Override
	public void onTabComplete(List<String> options, CommandSender sender, String label, String[] args) {
		if (args.length == 1) {
			addTeamStringList(options, args[0]);
		} else if (args.length == 2) {
			options.add("<key>");
		} else if (args.length == 3) {
			options.add("<value>");
		}
	}
}
