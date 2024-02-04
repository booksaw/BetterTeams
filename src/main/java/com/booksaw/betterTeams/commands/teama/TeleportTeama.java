package com.booksaw.betterTeams.commands.teama;

import com.booksaw.betterTeams.CommandResponse;
import com.booksaw.betterTeams.Main;
import com.booksaw.betterTeams.Team;
import com.booksaw.betterTeams.commands.SubCommand;
import com.booksaw.betterTeams.message.HelpMessage;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;

public class TeleportTeama extends SubCommand {

	@Override
	public CommandResponse onCommand(CommandSender sender, String label, String[] args) {
		Team specifiedTeam = null; // If null then all teams are being teleported
		if (!args[0].equalsIgnoreCase(":all")) {
			specifiedTeam = Team.getTeam(args[0]);

			if (specifiedTeam == null) {
				return new CommandResponse("noTeam");
			}
		}

		Player player = (Player) sender;
		Location location = player.getLocation(); // using the sender's location as a default
		if (args.length > 1) {

			if (args.length != 2 && args.length != 4 && args.length != 6) {
				return new CommandResponse(new HelpMessage(this, label));
			}

			if (args.length == 2) {
				if (args[1].equalsIgnoreCase(":home")) {
					if (specifiedTeam == null) {
						location = null; // Will only ever be null if :all AND :home are used
					} else {
						if (specifiedTeam.getTeamHome() == null) {
							return new CommandResponse("admin.home.noHome");
						}
						location = specifiedTeam.getTeamHome();
					}
				} else {
					Player target = Bukkit.getPlayer(args[1]);
					if (target == null) {
						return new CommandResponse("noPlayer");
					}
					location = target.getLocation();
				}
			} else {
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
		}

		final Location locationFinal = location;
		final Team specifiedTeamFinal = specifiedTeam;

		new BukkitRunnable() {

			@Override
			public void run() {
				if (specifiedTeamFinal == null) {
					// All teams are being teleported
					for (Entry<UUID, Team> entry : Team.getTeamManager().getLoadedTeamListClone().entrySet()) {
						if (locationFinal == null) {
							// Teams are being teleported to their homes if they have one set
							if (entry.getValue().getTeamHome() != null) {
								for (Player p : entry.getValue().getOnlineMembers()) {
									p.teleport(entry.getValue().getTeamHome());
								}
							}
						} else {
							for (Player p : entry.getValue().getOnlineMembers()) {
								p.teleport(locationFinal);
							}
						}
					}
				} else {
					for (Player p : specifiedTeamFinal.getOnlineMembers()) {
						p.teleport(locationFinal);
					}
				}
			}

		}.runTask(Main.plugin);

		String messageNode = "admin.teleport.success";
		if (specifiedTeam == null) {
			if (location != null) {
				messageNode = "admin.teleport.all.success";
			} else {
				messageNode = "admin.teleport.all.home.success";
			}
		}
		return new CommandResponse(true, messageNode);
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
		return "<:all|team> [:home|x|player] [y] [z] [yaw] [pitch]";
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
			options.add(":all");
			addTeamStringList(options, args[0]);
			break;
		case 2:
			options.add(":home");
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