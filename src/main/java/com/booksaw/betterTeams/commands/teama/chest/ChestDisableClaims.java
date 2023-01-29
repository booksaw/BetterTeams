package com.booksaw.betterTeams.commands.teama.chest;

import com.booksaw.betterTeams.CommandResponse;
import com.booksaw.betterTeams.commands.SubCommand;
import com.booksaw.betterTeams.events.ChestManagement;
import com.booksaw.betterTeams.message.MessageManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.util.List;

public class ChestDisableClaims extends SubCommand {

	@Override
	public CommandResponse onCommand(CommandSender sender, String label, String[] args) {
		if (!ChestManagement.enableClaims) {
			return new CommandResponse("admin.chest.disable.already");
		}

		ChestManagement.enableClaims = false;
		Bukkit.broadcastMessage(MessageManager.getPrefix() + MessageManager.getMessage("admin.chest.disabled.bc"));
		return new CommandResponse("admin.chest.disable.success");
	}

	@Override
	public String getCommand() {
		return "disableclaims";
	}

	@Override
	public String getNode() {
		return "admin.chest.disable";
	}

	@Override
	public String getHelp() {
		return "Disable all chest claims for a period of time (either until '/teama chest enableclaims' is run or the server is restarted";
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

	
	
}
