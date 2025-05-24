package com.booksaw.betterTeams.commands.teama;

import com.booksaw.betterTeams.CommandResponse;
import com.booksaw.betterTeams.Main;
import com.booksaw.betterTeams.commands.SubCommand;
import com.booksaw.betterTeams.message.MessageManager;
import org.bukkit.command.CommandSender;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

public class ImportmessagesTeama extends SubCommand {
	@Override
	public CommandResponse onCommand(CommandSender sender, String label, String[] args) {
		try {
			File f = new File(Main.plugin.getDataFolder() + File.separator + MessageManager.MISSINGMESSAGES_FILENAME);
			try (BufferedReader reader = new BufferedReader(new FileReader(f))) {
				String line;
				while ((line = reader.readLine()) != null) {
					if (line.isEmpty() || line.startsWith("#")) {
						continue;
					}
					String[] split = line.split(": ", 2);
					if (split.length != 2) {
						continue;
					}
					MessageManager.getDefaultMessages().set(split[0], split[1]);
				}
				MessageManager.getDefaultMessagesConfigManager().save(true);
				f.delete();
			}
		} catch (IOException e) {
			e.printStackTrace();
			return new CommandResponse(false, "admin.import.fail");
		}
		return new CommandResponse(true, "admin.import.success");
	}

	@Override
	public String getCommand() {
		return "importmessages";
	}

	@Override
	public String getNode() {
		return "admin.importmessages";
	}

	@Override
	public String getHelp() {
		return "Import the messages from missingmessages.txt the current language";
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
