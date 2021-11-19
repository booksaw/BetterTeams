package com.booksaw.betterTeams.commands.teama;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.booksaw.betterTeams.CommandResponse;
import com.booksaw.betterTeams.Team;
import com.booksaw.betterTeams.commands.presets.TeamSelectSubCommand;
import com.booksaw.betterTeams.message.HelpMessage;

public class TeleportTeama extends TeamSelectSubCommand {

	@Override
	public CommandResponse onCommand(CommandSender sender, String label, String[] args, Team team) {
		Team specifiedTeam = Team.getTeam(args[0]);

		if (specifiedTeam == null) {
			return new CommandResponse("noTeam");
		}

		Player player = (Player) sender;
		Location location = player.getLocation(); // using the sender's location as a default
		if (args.length > 1) {

			if (args.length != 2 && args.length != 4 && args.length != 6) {
				return new CommandResponse(new HelpMessage(this, label));
			}

			if (args.length == 2) {
				Player target = Bukkit.getPlayer(args[1]);
				if (target == null) {
					return new CommandResponse("noPlayer");
				}

				specifiedTeam.getOnlineMemebers().forEach(p -> p.teleport(target.getLocation()));
				return new CommandResponse(true, "admin.teleport.success");
			}

			double x, y, z;
			float yaw = location.getYaw(), pitch = location.getPitch();

			try {
				x = Double.parseDouble(args[1]);
				y = Double.parseDouble(args[2]);
				z = Double.parseDouble(args[3]);
			} catch (NumberFormatException e) {
				return new CommandResponse(new HelpMessage(this, label));
			}

			if (args.length == 6) {
				try {
					yaw = Float.parseFloat(args[4]);
					pitch = Float.parseFloat(args[5]);
				} catch (NumberFormatException E) {
					return new CommandResponse(new HelpMessage(this, label));
				}
			}

			location = new Location(player.getWorld(), x, y, z, yaw, pitch);
		}

		for (Player p : specifiedTeam.getOnlineMemebers()) {
			p.teleport(location);
		}
		return new CommandResponse(true, "admin.teleport.success");
	}

	@Override
	public String getCommand() {
		return "teleport";
	}

	@Override
	public boolean needPlayer() {
		return true;
	}

	@Override
	public String getNode() {
		return "admin.teleport";
	}

	@Override
	public String getHelp() {
		return "Teleports the specified team to the specified location (or executor's location)";
	}

	@Override
	public String getArguments() {
		return "<team> [x|player] [y] [z] [yaw] [pitch]";
	}

	@Override
	public int getMinimumArguments() {
		return 1;
	}

	@Override
	public int getMaximumArguments() {
		return 6;
	}

	@Override
	public void onTabComplete(List<String> options, CommandSender sender, String label, String[] args) {
		switch (args.length) {
		case 1:
			addTeamStringList(options, args[0]);
			break;
		case 2:
			options.add("[x]");
			addPlayerStringList(options, args[1]);
			break;
		case 3:
			options.add("[y]");
			break;
		case 4:
			options.add("[z]");
			break;
		case 5:
			options.add("[yaw]");
			break;
		case 6:
			options.add("[pitch]");
			break;
		}
	}
}