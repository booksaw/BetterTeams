package com.booksaw.betterTeams.commands.team;

import com.booksaw.betterTeams.*;
import com.booksaw.betterTeams.commands.ParentCommand;
import com.booksaw.betterTeams.commands.presets.TeamSubCommand;
import com.booksaw.betterTeams.customEvents.TeamDepositEvent;
import com.booksaw.betterTeams.customEvents.post.PostTeamDepositEvent;
import com.booksaw.betterTeams.message.HelpMessage;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.util.List;

public class DepositCommand extends TeamSubCommand {

	private final ParentCommand parentCommand;
	
	public DepositCommand(ParentCommand parentCommand) {
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
			return new CommandResponse("deposit.tooLittle");
		}

		final TeamDepositEvent event = new TeamDepositEvent(team, player, amount);

		Bukkit.getPluginManager().callEvent(event);

		if (event.isCancelled()) {
			return new CommandResponse("deposit.fail");
		}

		if (amount != event.getAmount())
			amount = event.getAmount();

		double result = team.getMoney() + amount;

		if (result > team.getMaxMoney() && team.getMaxMoney() >= 0) {
			return new CommandResponse("deposit.max");
		}

		EconomyResponse response = Main.econ.withdrawPlayer(player.getPlayer(), amount);

		if (!response.transactionSuccess()) {
			return new CommandResponse("deposit.fail");
		}

		team.setMoney(result);

		Bukkit.getPluginManager().callEvent(new PostTeamDepositEvent(team, player, amount));

		return new CommandResponse(true, "deposit.success");
	}

	@Override
	public String getCommand() {
		return "deposit";
	}

	@Override
	public String getNode() {
		return "balance";
	}

	@Override
	public String getHelp() {
		return "Deposit money into the teams balance";
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
		return PlayerRank.DEFAULT;
	}

	@Override
	public boolean runAsync(String[] args) {
		return false;
	}

}
