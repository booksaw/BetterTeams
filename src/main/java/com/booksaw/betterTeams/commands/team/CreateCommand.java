package com.booksaw.betterTeams.commands.team;

import com.booksaw.betterTeams.CommandResponse;
import com.booksaw.betterTeams.Main;
import com.booksaw.betterTeams.Team;
import com.booksaw.betterTeams.commands.ParentCommand;
import com.booksaw.betterTeams.commands.presets.NoTeamSubCommand;
import com.booksaw.betterTeams.message.HelpMessage;
import com.booksaw.betterTeams.message.ReferencedFormatMessage;
import com.booksaw.betterTeams.util.TeamUtil;
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
	private final boolean enforceTag;

	public CreateCommand(ParentCommand parentCommand) {
		this.parentCommand = parentCommand;
		enforceTag = Main.plugin.getConfig().getBoolean("enforceTag");
	}

	@Override
	public CommandResponse onCommand(Player sender, String label, String[] args) {
		CommandResponse response = TeamUtil.verifyTeamName(args[0]);
		if (response != null) {
			return response;
		}

		if (args.length <= 1 && enforceTag) {
			return new CommandResponse(new HelpMessage(this, label, parentCommand));
		}

		if (args.length > 1) {
			response = TeamUtil.verifyTagName(args[1]);
			if (response != null) {
				return response;
			}
		}

		if (Team.getTeamManager().isTeam(args[0])) {
			return new CommandResponse("create.exists");
		}

		Team team = Team.getTeamManager().createNewTeam(args[0], sender);

		if (args.length > 1) {
			team.setTag(args[1]);
		}

		return new CommandResponse(true, new ReferencedFormatMessage("create.success", team.getName()));

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
