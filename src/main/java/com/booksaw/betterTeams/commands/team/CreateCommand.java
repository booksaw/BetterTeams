package com.booksaw.betterTeams.commands.team;

import com.booksaw.betterTeams.CommandResponse;
import com.booksaw.betterTeams.Main;
import com.booksaw.betterTeams.Team;
import com.booksaw.betterTeams.commands.ParentCommand;
import com.booksaw.betterTeams.commands.presets.NoTeamSubCommand;
import com.booksaw.betterTeams.message.HelpMessage;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * This class handles the /team create [team] command
 *
 * @author booksaw
 */
public class CreateCommand extends NoTeamSubCommand {
	private final ParentCommand parentCommand;
	boolean enforceTag;

	public CreateCommand(ParentCommand parentCommand) {
		this.parentCommand = parentCommand;
		enforceTag = Main.plugin.getConfig().getBoolean("enforceTag");
	}

	@Override
	public CommandResponse onCommand(Player sender, String label, String[] args) {

		if (!Team.isValidTeamName(args[0])) {
			return new CommandResponse("create.banned");
		}

		if (args.length <= 1 && enforceTag) {
			return new CommandResponse(new HelpMessage(this, label, parentCommand));
		}

		if (args.length > 1) {
			if (!Team.isValidTeamName(args[1])) {
				return new CommandResponse("tag.banned");
			}

			int max = Main.plugin.getConfig().getInt("maxTagLength");
			if (max > 55) {
				max = 55;
			}
			if (max != -1 && max < args[1].length()) {
				return new CommandResponse("tag.maxLength");
			}

		}

		int max = Main.plugin.getConfig().getInt("maxTeamLength");
		if (max > 55) {
			max = 55;
		}

		if (max != -1 && max < args[0].length()) {
			return new CommandResponse("create.maxLength");
		}

		int min = Main.plugin.getConfig().getInt("minTeamLength");
		if (min <= 0 || min > 55) {
			min = 0;
		}

		if (min != 0 && min > args[0].length()) {
			return new CommandResponse("create.minLength");
		}

		if (Team.getTeamManager().isTeam(args[0])) {
			// team already exists
			return new CommandResponse("create.exists");
		}

		Team team = Team.getTeamManager().createNewTeam(args[0], sender);

		if (args.length > 1) {
			team.setTag(args[1]);
		}

		return new CommandResponse(true, "create.success");

	}

	@Override
	public String getCommand() {
		return "create";
	}

	@Override
	public int getMinimumArguments() {
		return 1;
	}

	@Override
	public String getNode() {
		return "create";
	}

	@Override
	public String getHelp() {
		return "Create a team with the specified name";
	}

	@Override
	public String getArguments() {
		if (enforceTag) {
			return "<name> <tag>";
		}
		return "<name> [tag]";
	}

	@Override
	public int getMaximumArguments() {
		return -1;
	}

	@Override
	public void onTabComplete(List<String> options, CommandSender sender, String label, String[] args) {

	}

}
