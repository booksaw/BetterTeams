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

		if (!target.equals("team") && !target.equals("all") && !target.equals("player")) {
			return new CommandResponse(new HelpMessage(this, label));
		}

		// Either a single Team, all Teams, or a single Player
		List<Object> targetList = getTargetList(args);

		if (targetList == null) {
			return new CommandResponse(new HelpMessage(this, label));
		}

		if (targetList.isEmpty()) {
			if (target.equals("team") || target.equals("all")) {
				return new CommandResponse("noTeam");
			} else {
				return new CommandResponse("noPlayer");
			}
		}

		// Splices the array to get the arguments after the target
		// i.e. /teama team <team> anything here or /teama all anything here
		String[] actionArgs = Arrays.copyOfRange(args, target.equals("all") ? 1 : 2, args.length);

		boolean teleportedAllHome = false;

		switch (actionArgs.length) {
			case 0: // No arguments - Teleport to issuer (must be a player)
				if (!(sender instanceof Player)) {
					return new CommandResponse("needPlayer");
				}
				teleportTargets(targetList, ((Player) sender).getLocation());
				break;
			case 1: // home
				if (!actionArgs[0].equalsIgnoreCase("home")) {
					return new CommandResponse(new HelpMessage(this, label));
				}
				if (targetList.size() == 1) {
					if (targetList.get(0) instanceof Team) {
						if (((Team) targetList.get(0)).getTeamHome() == null) {
							return new CommandResponse("admin.home.noHome");
						}
					} else if (targetList.get(0) instanceof Player) {
						Team team = Team.getTeam((Player) targetList.get(0));
						if (team == null) {
							return new CommandResponse("admin.inTeam");
						}
						if (team.getTeamHome() == null) {
							return new CommandResponse("admin.home.noHome");
						}
					} else {
						// Should never happen
						throw new IllegalArgumentException("Only Team or Player objects can be in the targetList");
					}
				} else {
					teleportedAllHome = true;
				}
				teleportTargets(targetList, targetList.stream().map(o -> {
					if (o instanceof Team) {
						return ((Team) o).getTeamHome();
					} else if (o instanceof Player) {
						Team team = Team.getTeam((Player) o);
						if (team != null) {
							if (team.getTeamHome() != null) {
								return team.getTeamHome();
							}
						}
					}
					// Should never happen
					throw new IllegalArgumentException("Only Team or Player objects can be in the targetList");
				}).toArray(Location[]::new));
				break;
			case 2: // player <player>
				if (!actionArgs[0].equalsIgnoreCase("player")) {
					return new CommandResponse(new HelpMessage(this, label));
				}
				Player targetPlayer = Bukkit.getPlayer(actionArgs[1]);
				if (targetPlayer == null) {
					return new CommandResponse("noPlayer");
				}
				teleportTargets(targetList, targetPlayer.getLocation());
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
				teleportTargets(targetList, new Location(world, x, y, z, yaw, pitch));
				break;
			default: // Invalid number of arguments provided
				return new CommandResponse(new HelpMessage(this, label));

		}

		if (target.equals("all")) {
			if (teleportedAllHome) {
				return new CommandResponse(true, "admin.teleport.all.home.success");
			}
			return new CommandResponse(true, "admin.teleport.all.success");
		} else if (target.equals("player")) {
			return new CommandResponse(true, "admin.teleport.player.success");
		}

		return new CommandResponse(true, "admin.teleport.success");

	}

	@Nullable
	private List<Object> getTargetList(String[] args) {
		if (args[0].equalsIgnoreCase("team") || args[0].equalsIgnoreCase("player")) {
			if (args.length < 2) {
				return null;
			}
			List<Object> targetList = new ArrayList<>();
			Object specifiedTarget = args[0].equalsIgnoreCase("team") ? Team.getTeam(args[1]) : Bukkit.getPlayer(args[1]);
			if (specifiedTarget == null) {
				return targetList;
			}
			targetList.add(specifiedTarget);
			return targetList;
		} else if (args[0].equalsIgnoreCase("all")) {
			return new ArrayList<>(Team.getTeamManager().getLoadedTeamListClone().values());
		}
		return null;
	}

	private void teleportTargets(List<Object> targetList, Location... locations) {
		// Either one location for all or separate locations for each
		if (locations.length != 1 && locations.length != targetList.size()) {
			// Should never happen
			throw new IllegalArgumentException("Invalid location array. Either one location for all or separate locations for each");
		}

		new BukkitRunnable() {

			@Override
			public void run() {
				if (locations.length != 1) {
					// Can only ever be a list of teams
					List<Team> teamList = targetList.stream().map(o -> (Team) o).toList();
					for (int i = 0; i < locations.length; i++) {
						for (Player player : teamList.get(i).getOnlineMembers()) {
							if (locations[i] == null) continue; // Some teams may not have their home set
							player.teleport(locations[i]);
						}
					}
					return;
				}
				for (Object targetObject : targetList) {
					if (targetObject instanceof Team) {
						for (Player player : ((Team) targetObject).getOnlineMembers()) {
							player.teleport(locations[0]);
						}
					} else if (targetObject instanceof Player) {
						((Player) targetObject).teleport(locations[0]);
					} else {
						// Should never happen
						throw new IllegalArgumentException("Only Team or Player objects can be in the targetList");
					}
				}
			}

		}.runTask(Main.plugin);

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
		return "Teleports the specified target to the specified location (or executor's location)";
	}

	@Override
	public String getArguments() {
		// This is confusing and complex, but it expresses all the options
		return "<team|player|all> [<team>|<player>|home|location|player] [home|location|player|<x> <y> <z> [yaw|world] [pitch] [world]|<player>] [<x> <y> <z> [yaw|world] [pitch] [world]|<player>]";
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
			options.add("player");
			options.add("all");
			return;
		}
		String target = args[0].toLowerCase();
		if (!target.equals("team") && !target.equals("all") && !target.equals("player")) {
			return;
		}
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
				if ((target.equals("team") || target.equals("player")) && args.length < 3) {
					return;
				}
				String[] actionArgs = Arrays.copyOfRange(args, target.equals("all") ? 1 : 2, args.length);
				if (actionArgs.length == 1) {
					options.add("home");
					options.add("location");
					options.add("player");
					return;
				}
				if (actionArgs[0].equalsIgnoreCase("location") && actionArgs.length > 2) {
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