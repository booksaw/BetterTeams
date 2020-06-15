package com.booksaw.betterTeams.commands.team;

import java.util.List;

import org.bukkit.command.CommandSender;

import com.booksaw.betterTeams.Main;
import com.booksaw.betterTeams.PlayerRank;
import com.booksaw.betterTeams.Team;
import com.booksaw.betterTeams.TeamPlayer;
import com.booksaw.betterTeams.commands.presets.TeamSubCommand;

import net.milkbowl.vault.economy.EconomyResponse;

public class WithdrawCommand extends TeamSubCommand {

	@Override
	public String onCommand(TeamPlayer player, String label, String[] args, Team team) {

		if (player.getRank() == PlayerRank.DEFAULT) {
			return "needAdmin";
		}

		double amount;
		try {
			amount = Double.parseDouble(args[0]);
		} catch (Exception e) {
			return "help";
		}

		if (amount <= 0) {
			return "withdraw.tooLittle";
		}

		if (team.getMoney() - amount < 0) {
			return "withdraw.notEnough";
		}
		EconomyResponse response = Main.econ.depositPlayer(player.getPlayer(), amount);

		if (!response.transactionSuccess()) {
			return "withdraw.fail";
		}

		team.setMoney(team.getMoney() - amount);

		return "withdraw.success";
	}

	@Override
	public String getCommand() {
		return "withdraw";
	}

	@Override
	public String getNode() {
		return "balance";
	}

	@Override
	public String getHelp() {
		return "Withdraw money from the teams balance";
	}

	@Override
	public String getArguments() {
		return "<amount>";
	}

	@Override
	public int getMinimumArguments() {
		return 1;
	}

	@Override
	public int getMaximumArguments() {
		return 1;
	}

	@Override
	public void onTabComplete(List<String> options, CommandSender sender, String label, String[] args) {
		options.add("<amount>");
	}

}
