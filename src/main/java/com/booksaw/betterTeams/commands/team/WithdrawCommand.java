package com.booksaw.betterTeams.commands.team;

import java.util.List;

import com.booksaw.betterTeams.customEvents.TeamWithdrawEvent;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import com.booksaw.betterTeams.CommandResponse;
import com.booksaw.betterTeams.Main;
import com.booksaw.betterTeams.PlayerRank;
import com.booksaw.betterTeams.Team;
import com.booksaw.betterTeams.TeamPlayer;
import com.booksaw.betterTeams.commands.ParentCommand;
import com.booksaw.betterTeams.commands.presets.TeamSubCommand;
import com.booksaw.betterTeams.message.HelpMessage;

import net.milkbowl.vault.economy.EconomyResponse;

public class WithdrawCommand extends TeamSubCommand {
	
	private final ParentCommand parentCommand;
	
	public WithdrawCommand(ParentCommand parentCommand) {
		this.parentCommand = parentCommand;
	}
	
	@Override
	public CommandResponse onCommand(TeamPlayer player, String label, String[] args, Team team) {

		double amount;
		try {
			amount = Double.parseDouble(args[0]);
		} catch (Exception e) {
			return new CommandResponse(new HelpMessage(this, label, parentCommand));
		}

		if (amount <= 0) {
			return new CommandResponse("withdraw.tooLittle");
		}

		final TeamWithdrawEvent event = new TeamWithdrawEvent(team, player, amount);

		Bukkit.getPluginManager().callEvent(event);

		if (event.isCancelled()) {
			return new CommandResponse("withdraw.fail");
		}

		if (amount != event.getAmount())
			amount = event.getAmount();

		if (team.getMoney() - amount < 0) {
			return new CommandResponse("withdraw.notEnough");
		}

		EconomyResponse response = Main.econ.depositPlayer(player.getPlayer(), amount);

		if (!response.transactionSuccess()) {
			return new CommandResponse("withdraw.fail");
		}

		team.setMoney(team.getMoney() - amount);

		return new CommandResponse(true, "withdraw.success");
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

	@Override
	public PlayerRank getDefaultRank() {
		return PlayerRank.ADMIN;
	}

	@Override
	public boolean runAsync(String[] args) {
		return false;
	}

}
