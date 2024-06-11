package com.booksaw.betterTeams.team;

import com.booksaw.betterTeams.Team;
import com.booksaw.betterTeams.team.storage.team.TeamStorage;
import org.bukkit.OfflinePlayer;

import java.util.UUID;

public class BanListComponent extends UuidListComponent {

	@Override
	public String getSectionHeading() {
		return "bans";
	}

	/**
	 * Used to check if the uuid of the specified player is stored within the ban
	 * list
	 * 
	 * @param player The player to check
	 * @return If that player is banned or not
	 */
	public boolean contains(OfflinePlayer player) {
		return contains(player.getUniqueId());
	}

	@Override
	public void load(TeamStorage section) {
		load(section.getBanList());
	}

	@Override
	public void save(TeamStorage storage) {
		storage.setBanList(getConvertedList());
	}

	@Override
	public void add(Team team, UUID component) {
		super.add(team, component);
		team.getStorage().addBan(component);
	}
	
	@Override
	public void remove(Team team, UUID component) {
		super.remove(team, component);
		team.getStorage().removeBan(component);
	}
	

}
