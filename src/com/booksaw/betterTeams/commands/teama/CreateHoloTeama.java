package com.booksaw.betterTeams.commands.teama;

import com.booksaw.betterTeams.CommandResponse;
import com.booksaw.betterTeams.commands.SubCommand;
import com.booksaw.betterTeams.integrations.HologramManager;
import com.booksaw.betterTeams.integrations.HologramManager.HologramType;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class CreateHoloTeama extends SubCommand {

	@Override
	public CommandResponse onCommand(CommandSender sender, String label, String[] args) {
		Player p = (Player) sender;
		if (args[0].equals("score")) {
			HologramManager.holoManager.createHolo(p.getLocation(), HologramType.LEADERBOARD);
			return new CommandResponse(true, "admin.holo.create.success");
		} else if (args[0].equals("money")) {
			HologramManager.holoManager.createHolo(p.getLocation(), HologramType.MONEY);
			return new CommandResponse(true, "admin.holo.create.success");
		}

		return new CommandResponse("help");
	}

	@Override
	public String getCommand() {
		return "create";
	}

	@Override
	public String getNode() {
		return "admin.holo";
	}

	@Override
	public String getHelp() {
		return "Create a hologram at your current location";
	}

	@Override
	public String getArguments() {
		return "<leaderboard/money>";
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
		if (args.length == 1) {
			options.add("leaderboard");
			options.add("money");
		}

	}

	@Override
	public boolean needPlayer() {
		return true;
	}

}
