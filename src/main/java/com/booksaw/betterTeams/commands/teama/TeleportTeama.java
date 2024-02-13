package com.booksaw.betterTeams.commands.teama;

import com.booksaw.betterTeams.CommandResponse;
import com.booksaw.betterTeams.Main;
import com.booksaw.betterTeams.Team;
import com.booksaw.betterTeams.commands.SubCommand;
import com.booksaw.betterTeams.message.HelpMessage;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TeleportTeama extends SubCommand {

	@Override
	public CommandResponse onCommand(CommandSender sender, String label, String[] args) {

		String target = args[0].toLowerCase();

		if (!target.equals("team") && !target.equals("all")) {
			return new CommandResponse(new HelpMessage(this, label));
		}

		List<Team> targetTeams = getTargetTeams(args);

		if (targetTeams == null) {
			return new CommandResponse(new HelpMessage(this, label));
		}

		if (targetTeams.isEmpty()) {
			return new CommandResponse("noTeam");
		}

		// Splices the array to get the arguments after the target
		// i.e. /teama team <team> anything here or /teama all anything here
		String[] actionArgs = Arrays.copyOfRange(args, target.equals("team") ? 2 : 1, args.length);

		boolean teleportResult = false;

		boolean teleportAllHome = false;

		switch (actionArgs.length) {
			case 0: // /teama team <team> or /teama all - teleport to player location
				if (!(sender instanceof Player)) {
					return new CommandResponse("needPlayer");
				}
				teleportResult = teleportTeams(targetTeams, ((Player) sender).getLocation());
				break;
			case 1: // /teama team <team> home or /teama all home - teleport to team home
				if (!actionArgs[0].equalsIgnoreCase("home")) {
					return new CommandResponse(new HelpMessage(this, label));
				}
				if (targetTeams.size() == 1) {
					if (targetTeams.get(0).getTeamHome() == null) {
						return new CommandResponse("admin.home.noHome");
					}
				} else {
					teleportAllHome = true;
				}
				teleportResult = teleportTeams(targetTeams, targetTeams.stream().map(Team::getTeamHome).toArray(Location[]::new));
				break;
			case 2: // /teama team <team> player <player> or /teama all player <player> - teleport to the player
				if (!actionArgs[0].equalsIgnoreCase("player")) {
					return new CommandResponse(new HelpMessage(this, label));
				}
				Player targetPlayer = Bukkit.getPlayer(actionArgs[1]);
				if (targetPlayer == null) {
					return new CommandResponse("noPlayer");
				}
				teleportResult = teleportTeams(targetTeams, targetPlayer.getLocation());
				break;
			case 4: // location <x> <y> <z>
			case 5: // location <x> <y> <z> [yaw|world]
			case 6: // location <x> <y> <z> [yaw] [pitch]
			case 7: // location <x> <y> <z> [yaw] [pitch] [world]
				if (!actionArgs[0].equalsIgnoreCase("location")) {
					return new CommandResponse(new HelpMessage(this, label));
				}
				double x, y, z;
				float yaw = 0, pitch = 0;
				World world = null;
				try {
					x = Double.parseDouble(actionArgs[1]);
					y = Double.parseDouble(actionArgs[2]);
					z = Double.parseDouble(actionArgs[3]);
				} catch (NumberFormatException e) {
					return new CommandResponse(new HelpMessage(this, label));
				}
				if (actionArgs.length >= 5) {
					world = Bukkit.getWorld(actionArgs[4]);
					if (world == null) {
						try {
							yaw = Float.parseFloat(actionArgs[4]);
						} catch (NumberFormatException E) {
							return new CommandResponse(new HelpMessage(this, label));
						}
					}
				}
				if (world != null && actionArgs.length > 5) {
					return new CommandResponse(new HelpMessage(this, label));
				}
				if (actionArgs.length >= 6) {
					try {
						pitch = Float.parseFloat(actionArgs[5]);
					} catch (NumberFormatException E) {
						return new CommandResponse(new HelpMessage(this, label));
					}
				}
				if (actionArgs.length == 7) {
					world = Bukkit.getWorld(actionArgs[6]);
					if (world == null) {
						return new CommandResponse("admin.teleport.noWorld");
					}
				}
				if (world == null) {
					if (!(sender instanceof Player)) {
						return new CommandResponse("needPlayer");
					}
					world = ((Player) sender).getWorld();
				}
				teleportResult = teleportTeams(targetTeams, new Location(world, x, y, z, yaw, pitch));
				break;
			default: // Invalid number of arguments provided
				return new CommandResponse(new HelpMessage(this, label));

		}

		if (!teleportResult) {
			// Should never happen
			return new CommandResponse("teleport.fail");
		}

		if (target.equals("all")) {
			if (teleportAllHome) {
				return new CommandResponse(true, "admin.teleport.all.home.success");
			}
			return new CommandResponse(true, "admin.teleport.all.success");
		}

		return new CommandResponse(true, "admin.teleport.success");

	}

	@Nullable
	private List<Team> getTargetTeams(String[] args) {
		if (args[0].equalsIgnoreCase("team")) {
			if (args.length < 2) {
				return null;
			}
			List<Team> targetTeams = new ArrayList<>();
			Team specifiedTeam = Team.getTeam(args[1]);
			if (specifiedTeam == null) {
				return targetTeams;
			}
			targetTeams.add(specifiedTeam);
			return targetTeams;
		} else if (args[0].equalsIgnoreCase("all")) {
			return Team.getTeamManager().getLoadedTeamListClone().values().stream().toList();
		}
		return null;
	}

	private boolean teleportTeams(List<Team> targetTeams, Location... locations) {
		// Either one location for all or separate locations for each
		if (locations.length != 1 && locations.length != targetTeams.size()) {
			return false;
		}

		new BukkitRunnable() {

			@Override
			public void run() {
				if (locations.length != 1) {
					for (int i = 0; i < locations.length; i++) {
						for (Player player : targetTeams.get(i).getOnlineMembers()) {
							if (locations[i] == null) continue; // Some teams may not have their home set
							player.teleport(locations[i]);
						}
					}
					return;
				}
				for (Team team : targetTeams) {
					for (Player player : team.getOnlineMembers()) {
						player.teleport(locations[0]);
					}
				}
			}

		}.runTask(Main.plugin);
		return true;
	}

	@Override
	public String getCommand() {
		return "teleport";
	}

	@Override
	public boolean needPlayer() {
		return false;
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
		// This is confusing and complex, but it expresses all the options
		return "<team|all> [<team>|home|location|player] [home|location|player|<x> <y> <z> [yaw|world] [pitch] [world]|<player>] [<x> <y> <z> [yaw|world] [pitch] [world]|<player>]";
	}

	@Override
	public int getMinimumArguments() {
		return 1;
	}

	@Override
	public int getMaximumArguments() {
		return 9;
	}

	@Override
	public void onTabComplete(List<String> options, CommandSender sender, String label, String[] args) {
		if (args.length == 1) {
			options.add("team");
			options.add("all");
			return;
		}
		String target = args[0].toLowerCase();
		String lastFullArg = args[args.length - 2];
		String lastArg = args[args.length - 1];
		switch (lastFullArg.toLowerCase()) {
			case "team":
				addTeamStringList(options, lastArg);
				break;
			case "home":
				break;
			case "location":
				options.add("[x]");
				break;
			case "player":
				addPlayerStringList(options, lastArg);
				break;
			default:
				if (target.equals("team") && args.length < 3) {
					return;
				}
				String[] actionArgs = Arrays.copyOfRange(args, target.equals("team") ? 2 : 1, args.length);
				if (actionArgs.length == 1) {
					options.add("home");
					options.add("location");
					options.add("player");
				} else if (actionArgs[0].equalsIgnoreCase("location") && actionArgs.length > 2) {
					switch (actionArgs.length) {
						case 3:
							options.add("[y]");
							break;
						case 4:
							options.add("[z]");
							break;
						case 5:
							options.add("[world]");
							options.add("[yaw]");
							break;
						case 6:
							options.add("[pitch]");
							break;
						case 7:
							options.add("[world]");
							break;
					}
				}
				break;
		}
	}
}