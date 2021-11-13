package com.booksaw.betterTeams.commands.team;

import com.booksaw.betterTeams.*;
import com.booksaw.betterTeams.commands.presets.TeamSubCommand;
import com.booksaw.betterTeams.customEvents.LevelupTeamEvent;
import com.booksaw.betterTeams.message.ReferencedFormatMessage;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.util.List;

public class RankupCommand extends TeamSubCommand {

	@Override
	public CommandResponse onCommand(TeamPlayer player, String label, String[] args, Team team) {

		String priceStr = Main.plugin.getConfig().getString("levels.l" + (team.getLevel() + 1) + ".price");

		if (priceStr == null || priceStr.equals("")) {
			return new CommandResponse("rankup.max");
		}

		boolean score = priceStr.contains("s");
		int price;
		try {
			price = Integer.parseInt(priceStr.substring(0, priceStr.length() - 1));
		} catch (Exception e) {
			price = 0;
		}

		if (price <= 0) {
			Bukkit.getLogger().warning("Rankup values setup wrong, price was found to be <= 0");
			return new CommandResponse("rankup.max");
		}
		// score is valid checking player has enough money score

		if (score) {

			if (team.getScore() < price) {
				return new CommandResponse(new ReferencedFormatMessage("rankup.score", price + ""));
			}

		} else {

			if (team.getMoney() < price) {
				return new CommandResponse(new ReferencedFormatMessage("rankup.money", price + ""));
			}

		}

		LevelupTeamEvent event = new LevelupTeamEvent(team, team.getLevel(), team.getLevel() + 1, price, score,
				player.getPlayer().getPlayer());
		Bukkit.getPluginManager().callEvent(event);
		if (event.isCancelled()) {
			return new CommandResponse(false);
		}

		if (score) {
			team.setScore(team.getScore() - price);
		} else {
			team.setMoney(team.getMoney() - price);
		}

		team.setLevel(team.getLevel() + 1);

		return new CommandResponse(true, "rankup.success");
	}

	@Override
	public String getCommand() {
		return "rankup";
	}

	@Override
	public String getNode() {
		return "rankup";
	}

	@Override
	public String getHelp() {
		return "Rank your team up to get more perks";
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

	@Override
	public PlayerRank getDefaultRank() {
		return PlayerRank.OWNER;
	}

}
