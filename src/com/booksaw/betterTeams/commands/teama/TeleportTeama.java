package com.booksaw.betterTeams.commands.teama;

import com.booksaw.betterTeams.CommandResponse;
import com.booksaw.betterTeams.Team;
import com.booksaw.betterTeams.commands.presets.TeamSelectSubCommand;
import com.booksaw.betterTeams.message.HelpMessage;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class TeleportTeama extends TeamSelectSubCommand {

    @Override
    public CommandResponse onCommand(CommandSender sender, String label, String[] args, Team team) {
        Team specifiedTeam = Team.getTeam(args[0]);

        if (specifiedTeam == null) {
            return new CommandResponse("noTeam");
        }

        if (!(sender instanceof Player)) {
            return new CommandResponse("needPlayer");
        }

        Player player = (Player) sender;
        Location location = player.getLocation(); // using the sender's location as a default
        if (args.length == 1) {
            for (Player p : specifiedTeam.getOnlineMemebers()) {
                p.teleport(location);
            }

            return new CommandResponse(true, "admin.teleport.success");
        } else if (args.length == 4) { // coordinates only, no yaw/pitch
            double x, y, z;
            try {
                x = Double.parseDouble(args[1]);
                y = Double.parseDouble(args[2]);
                z = Double.parseDouble(args[3]);
            } catch (NumberFormatException e) {
                return new CommandResponse(new HelpMessage(this, label));
            }

            location = new Location(player.getWorld(), x, y, z);
            for (Player p : specifiedTeam.getOnlineMemebers()) {
                p.teleport(location);
            }
            return new CommandResponse(true, "admin.teleport.success");
        } else if (args.length == 6) {
            double x, y, z;
            float yaw, pitch;
            try {
                x = Double.parseDouble(args[1]);
                y = Double.parseDouble(args[2]);
                z = Double.parseDouble(args[3]);
                yaw = Float.parseFloat(args[4]);
                pitch = Float.parseFloat(args[5]);
            } catch (NumberFormatException E) {
                return new CommandResponse(new HelpMessage(this, label));
            }

            location = new Location(player.getWorld(), x, y, z, yaw, pitch);
            for (Player p : specifiedTeam.getOnlineMemebers()) {
                p.teleport(location);
            }
            return new CommandResponse(true, "admin.teleport.success");
        } else {
            return new CommandResponse(new HelpMessage(this, label));
        }
    }

    @Override
    public String getCommand() {
        return "teleport";
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
        return "<team> [x] [y] [z] [yaw] [pitch]";
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

        if (args.length == 1) {
            addTeamStringList(options, args[0]);
        }

    }
}
