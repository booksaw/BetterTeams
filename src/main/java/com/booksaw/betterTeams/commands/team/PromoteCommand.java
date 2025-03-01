package com.booksaw.betterTeams.commands.team;

import com.booksaw.betterTeams.*;
import com.booksaw.betterTeams.commands.presets.TeamSubCommand;
import com.booksaw.betterTeams.message.MessageManager;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.Objects;

/**
 * This class handles the command /team promote [player]
 *
 * @author booksaw
 */
public class PromoteCommand extends TeamSubCommand {

	@Override
	public CommandResponse onCommand(TeamPlayer teamPlayer, String label, String[] args, Team team) {

		/*
		 * method is depreciated as it does not guarantee the expected player, in most
		 * use cases this will work and it will be down to the user if it does not due
		 * to name changes This method is appropriate to use in this use case (so users
		 * can view offline users teams by name not just by team name)
		 */
		TeamPlayerResult teamPlayerResult = getTeamPlayer(team, args[0]);
		if (teamPlayerResult.isCR()) {
			return teamPlayerResult.getCr();
		}

		TeamPlayer promotePlayer = teamPlayerResult.getPlayer();

		if (Objects.requireNonNull(promotePlayer).getRank() == PlayerRank.OWNER) {
			return new CommandResponse("promote.max");
		}

		if (promotePlayer.getRank() == PlayerRank.ADMIN && teamPlayer.getRank() != PlayerRank.OWNER) {
			return new CommandResponse("promote.noPerm");
		}

		if (promotePlayer.getRank() == PlayerRank.ADMIN && Main.plugin.getConfig().getBoolean("singleOwner")) {
			return new CommandResponse("setowner.use");
		}

		if (promotePlayer.getRank() == PlayerRank.DEFAULT && team.isMaxAdmins()) {
			return new CommandResponse("promote.maxAdmins");
		} else if (promotePlayer.getRank() == PlayerRank.ADMIN && team.isMaxOwners()) {
			return new CommandResponse("promote.maxOwners");
		}

		team.promotePlayer(promotePlayer);
		if (promotePlayer.getPlayer().isOnline()) {
			MessageManager.sendMessage(promotePlayer.getPlayer().getPlayer(), "promote.notify");
		}

		return new CommandResponse(true, "promote.success");

	}

	@Override
	public String getCommand() {
		return "promote";
	}

	@Override
	public int getMinimumArguments() {
		return 1;
	}

	@Override
	public String getNode() {
		return "promote";
	}

	@Override
	public String getHelp() {
		return "Promote the specified player within your team";
	}

	@Override
	public String getArguments() {
		return "<player>";
	}

	@Override
	public int getMaximumArguments() {
		return 1;
	}

	@Override
	public void onTabComplete(List<String> options, CommandSender sender, String label, String[] args) {
		addPlayerStringList(options, (args.length == 0) ? "" : args[0]);
	}

	@Override
	public PlayerRank getDefaultRank() {
		return PlayerRank.OWNER;
	}

}
