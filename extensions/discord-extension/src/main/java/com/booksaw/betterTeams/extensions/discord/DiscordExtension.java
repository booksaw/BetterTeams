package com.booksaw.betterTeams.extensions.discord;

import com.booksaw.betterTeams.extension.BetterTeamsExtension;

public class DiscordExtension extends BetterTeamsExtension {

	@Override
	public void onEnable() {
		getPlugin().getServer().getPluginManager().registerEvents(new WebhookHandler(getConfig().getConfig()), getPlugin());
	}
}
