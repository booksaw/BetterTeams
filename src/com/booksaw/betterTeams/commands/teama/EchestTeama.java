package com.booksaw.betterTeams.commands.teama;

import com.booksaw.betterTeams.CommandResponse;
import com.booksaw.betterTeams.Team;
import com.booksaw.betterTeams.commands.presets.TeamSelectSubCommand;
import com.booksaw.betterTeams.events.InventoryManagement;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class EchestTeama extends TeamSelectSubCommand {

    @Override
    public CommandResponse onCommand(CommandSender sender, String label, String[] args, Team team) {

        InventoryManagement.adminViewers.put((Player) sender, team);
        ((Player) sender).openInventory(team.getEchest());

        return new CommandResponse(true);
    }

    @Override
    public String getCommand() {
        return "echest";
    }

    @Override
    public String getNode() {
        return "admin.echest";
    }

    @Override
    public String getHelp() {
        return "View and edit that teams ender chest";
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
    }

    @Override
    public boolean needPlayer() {
        return true;
    }

}
