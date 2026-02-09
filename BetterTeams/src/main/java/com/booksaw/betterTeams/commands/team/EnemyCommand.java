package com.booksaw.betterTeams.commands.team;

import com.booksaw.betterTeams.CommandResponse;
import com.booksaw.betterTeams.PlayerRank;
import com.booksaw.betterTeams.Team;
import com.booksaw.betterTeams.TeamPlayer;
import com.booksaw.betterTeams.commands.presets.TeamSubCommand;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public class EnemyCommand extends TeamSubCommand {

	@Override
	public CommandResponse onCommand(TeamPlayer player, String label, String[] args, Team team) {

		if (args.length == 0) {
			return new CommandResponse("noTeam");
		}

		Team enemy = Team.getTeam(args[0]);
		if (enemy == null) {
			return new CommandResponse("noTeam");
		}
		if (enemy == team) {
			return new CommandResponse("enemy.self");
		}

		// check if they are already allies
		if (enemy.isEnemy(team.getID())) {
			return new CommandResponse("enemy.already");
		}

		// checking limit
		if (team.hasMaxEnemies()) {
			// Don't check the other sides limit! Because otherwise we get abusers that will just create stupid small
			//   teams in order to not be able to have any other enemies.
			return new CommandResponse("enemy.limit");
		}

		enemy.addEnemy(team.getID(), false);
		team.addEnemy(enemy.getID(), true);
		return new CommandResponse(true, "enemy.success");
	}

	@Override
	public String getCommand() {
		return "enemy";
	}

	@Override
	public String getNode() {
		return "enemy";
	}

	@Override
	public String getHelp() {
		return "Used to set another team as your teams enemy";
	}

	@Override
	public String getArguments() {
		return "<team>";
	}

	@Override
	public int getMinimumArguments() {
		return 0;
	}

	@Override
	public int getMaximumArguments() {
		return 1;
	}

	@Override
	public void onTabComplete(List<String> options, CommandSender sender, String label, String[] args) {
		if (args.length == 1) {
			Team myTeam = getMyTeam(sender);

			Set<UUID> knownTeams = null;
			if (myTeam != null) {
				knownTeams = myTeam.getEnemies().getClone();
				knownTeams.add(myTeam.getID());
			}

			addTeamStringList(options, args[0], knownTeams, null);
		}
	}

	@Override
	public PlayerRank getDefaultRank() {
		return PlayerRank.OWNER;
	}

}
