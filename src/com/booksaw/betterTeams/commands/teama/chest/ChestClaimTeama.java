package com.booksaw.betterTeams.commands.teama.chest;

import com.booksaw.betterTeams.CommandResponse;
import com.booksaw.betterTeams.Team;
import com.booksaw.betterTeams.commands.presets.TeamSelectSubCommand;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class ChestClaimTeama extends TeamSelectSubCommand {

    @Override
    public CommandResponse onCommand(CommandSender sender, String label, String[] args, Team team) {

        Player p = (Player) sender;

        Location loc = p.getLocation();

        Block block = loc.getBlock();
        loc = Team.normalise(loc);
        if (block.getType() != Material.CHEST) {
            return new CommandResponse("chest.claim.noChest");
        }
        Team claimedBy = Team.getClaimingTeam(block);
        if (claimedBy != null) {
            return new CommandResponse("chest.claim.claimed");
        }
        // they can claim the chest
        team.addClaim(loc);

        return new CommandResponse(true, "admin.chest.claim.success");
    }

    @Override
    public String getCommand() {
        return "claim";
    }

    @Override
    public String getNode() {
        return "admin.chest.claim";
    }

    @Override
    public String getHelp() {
        return "Force the specified team to claim the chest you are standing on";
    }

    @Override
    public String getArguments() {
        return "<team>";
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
            addTeamStringList(options, args[0]);
        }
    }

    @Override
    public boolean needPlayer() {
        return true;
    }

}
