package com.booksaw.betterTeams.commands.teama.meta;

import com.booksaw.betterTeams.CommandResponse;
import com.booksaw.betterTeams.Team;
import com.booksaw.betterTeams.commands.presets.TeamSelectSubCommand;
import com.booksaw.betterTeams.message.ReferencedFormatMessage;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class MetaGetTeama extends TeamSelectSubCommand {
	@Override
	public CommandResponse onCommand(CommandSender sender, String label, String[] args, Team team) {
		if (args.length == 1) {
			Map<String, String> allMetaData = team.getMeta().get().getAll();

			if (allMetaData.isEmpty()) { return new CommandResponse(false, "admin.meta.get.noMetaData"); }

			String formattedMetaData = allMetaData.entrySet().stream()
					.map(entry -> entry.getKey() + ": " + entry.getValue())
					.collect(Collectors.joining(", "));

			return new CommandResponse(true, new ReferencedFormatMessage("admin.meta.get.allMetaData", formattedMetaData));

		} else if (args.length == 2) {
			String key = args[1];
			Optional<String> value = team.getMeta().get().get(key);

			if (value.isPresent()) {
				return new CommandResponse(true, new ReferencedFormatMessage("admin.meta.get.specificMetaData", key, value.get()));
			} else {
				return new CommandResponse(false, "admin.meta.get.noexist");
			}
		}
		return new CommandResponse(false);


	}

	@Override
	public String getCommand() {
		return "get";
	}

	@Override
	public String getNode() {
		return "admin.meta.get";
	}

	@Override
	public String getHelp() {
		return "Get a metadata value for a specific team";
	}

	@Override
	public String getArguments() {
		return "<team> [<key>]";
	}

	@Override
	public int getMinimumArguments() {
		return 1;
	}

	@Override
	public int getMaximumArguments() {
		return 2;
	}

	@Override
	public void onTabComplete(List<String> options, CommandSender sender, String label, String[] args) {
		if (args.length == 1) {
			addTeamStringList(options, args[0]);
			addPlayerStringList(options, args[0]);
		} else if (args.length == 2) {
			addMetaStringList(options, Team.getTeam(args[0]), args[1]);
		}
	}
}
