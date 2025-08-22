package com.booksaw.betterTeams.commands.teama.meta;

import com.booksaw.betterTeams.CommandResponse;
import com.booksaw.betterTeams.Team;
import com.booksaw.betterTeams.commands.presets.TeamSelectSubCommand;
import com.booksaw.betterTeams.message.ReferencedFormatMessage;
import org.bukkit.command.CommandSender;

import java.util.List;

public class MetaRemoveTeama extends TeamSelectSubCommand {
	@Override
	public CommandResponse onCommand(CommandSender sender, String label, String[] args, Team team) {
		String key = args[1];

		if (!team.getMeta().get().has(key)) {
			return new CommandResponse(false, "admin.meta.get.noexist");
		}

		team.getMeta().get().remove(key);
		team.getStorage().saveMeta(team.getMeta().get());

		return new CommandResponse(true, new ReferencedFormatMessage("admin.meta.remove.success", key));
	}

	@Override
	public String getCommand() {
		return "remove";
	}

	@Override
	public String getNode() {
		return "admin.meta.remove";
	}

	@Override
	public String getHelp() {
		return "Removes a metadata key from a team";
	}

	@Override
	public String getArguments() {
		return "<team> <key>";
	}

	@Override
	public int getMinimumArguments() {
		return 2;
	}

	@Override
	public int getMaximumArguments() {
		return 2;
	}

	@Override
	public void onTabComplete(List<String> options, CommandSender sender, String label, String[] args) {
		if (args.length == 1) {
			addTeamStringList(options, args[0]);
		} else if (args.length == 2) {
			Team team = Team.getTeam(args[0]);
			if (team != null) {
				team.getMeta().get().getAll().keySet().stream()
						.filter(key -> key.toLowerCase().startsWith(args[1].toLowerCase()))
						.forEach(options::add);
			} else {
				options.add("<key>");
			}
		}
	}
}
