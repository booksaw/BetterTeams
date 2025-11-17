package com.booksaw.betterTeams.commands;

import com.booksaw.betterTeams.ConfigManager;
import com.booksaw.betterTeams.Main;
import com.booksaw.betterTeams.PlayerRank;
import com.booksaw.betterTeams.commands.presets.TeamSubCommand;
import com.booksaw.betterTeams.cooldown.CooldownManager;
import com.booksaw.betterTeams.cost.CostManager;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.Objects;

public class PermissionParentCommand extends ParentCommand {

	ConfigManager yamlManager;
	YamlConfiguration config;

	public PermissionParentCommand(String command) {
		super(command);

		loadConfig(command);
	}

	public PermissionParentCommand(CostManager prices, CooldownManager cooldowns, String command) {
		super(prices, cooldowns, command, false);
		loadConfig(command);
	}

	private void loadConfig(String command) {
		yamlManager = new ConfigManager(command + "permissions", false);
		config = yamlManager.config;
	}

	@Override
	public void addSubCommand(SubCommand command) {

		if (config.isConfigurationSection(command.getCommand())) {
			// already exists
			// checking if it is enabled
			if (config.getBoolean(command.getCommand() + ".enabled")) {
				super.addSubCommand(command);

				if (command instanceof TeamSubCommand) {
					PlayerRank rank = PlayerRank
							.getRank(Objects.requireNonNull(config.getString(command.getCommand() + ".rank")));
					if (rank == null) {
						Main.plugin.getLogger().warning("The command " + command.getCommand()
								+ " has the rank set to something invalid, using the default rank");
					} else {
						((TeamSubCommand) command).setRequiredRank(rank);
					}
				}

			}
		} else {
			// needs generating
			super.addSubCommand(command);
			config.set(command.getCommand() + ".enabled", true);

			if (command instanceof TeamSubCommand) {
				config.set(command.getCommand() + ".rank", ((TeamSubCommand) command).getDefaultRank().toString());
			}

			yamlManager.save(false);

		}
	}

	public boolean isEnabled(String command) {
		return config.getBoolean(command + ".enabled");
	}

}
