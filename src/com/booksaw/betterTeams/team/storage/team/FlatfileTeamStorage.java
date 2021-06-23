package com.booksaw.betterTeams.team.storage.team;

import java.util.List;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.booksaw.betterTeams.Team;
import com.booksaw.betterTeams.team.storage.storageManager.FlatfileStorageManager;

public class FlatfileTeamStorage extends TeamStorage {

	private final FlatfileStorageManager teamStorage;

	public FlatfileTeamStorage(Team team, boolean newTeam, FlatfileStorageManager teamStorage) {
		super(team);
		this.teamStorage = teamStorage;
	}

	public ConfigurationSection getConfig() {
		ConfigurationSection section = teamStorage.getTeamStorage().getConfigurationSection(getConfigPath());
		if (section == null) {
			section = teamStorage.getTeamStorage().createSection(getConfigPath());
		}
		return section;
	}

	private String getConfigPath() {
		return "team." + team.getID();
	}

	@Override
	protected void setValue(String location, TeamStorageType storageType, Object value) {
		getConfig().set(location, value);
		teamStorage.saveTeamsFile();
	}

	@Override
	public String getString(String reference) {
		return getConfig().getString(reference);
	}

	@Override
	public boolean getBoolean(String reference) {
		return getConfig().getBoolean(reference);
	}

	@Override
	public double getDouble(String reference) {
		return getConfig().getDouble(reference);
	}

	@Override
	public int getInt(String reference) {
		return getConfig().getInt(reference);
	}

	@Override
	public List<String> getPlayerList() {
		return getConfig().getStringList("players");
	}

	@Override
	public void setPlayerList(List<String> players) {
		setValue("players", TeamStorageType.STRING, players);
	}

	@Override
	public List<String> getBanList() {
		return getConfig().getStringList("bans");
	}

	@Override
	public void setBanList(List<String> players) {
		setValue("bans", TeamStorageType.STRING, players);
	}

	@Override
	public List<String> getAllyList() {
		return getConfig().getStringList("allies");
	}

	@Override
	public void setAllyList(List<String> players) {
		setValue("allies", TeamStorageType.STRING, players);
	}

	@Override
	public void getEchestContents(Inventory inventory) {

		ConfigurationSection section = getConfig();

		for (int i = 0; i < 27; i++) {
			ItemStack is = section.getItemStack("echest." + i);
			if (is != null) {
				inventory.setItem(i, is);
			}
		}
	}

	@Override
	public void setEchestContents(Inventory inventory) {

		ConfigurationSection section = getConfig();

		for (int i = 0; i < 27; i++) {

			if (inventory.getItem(i) != null) {
				section.set("echest." + i, inventory.getItem(i));
			}
		}
	}

	@Override
	public List<String> getAllyRequestList() {
		return getConfig().getStringList("allyrequests");
	}

	@Override
	public void setAllyRequestList(List<String> players) {
		setValue("allyrequests", TeamStorageType.STRING, players);
	}

	@Override
	public List<String> getWarps() {
		return getConfig().getStringList("warps");
	}

	@Override
	public void setWarps(List<String> warps) {
		setValue("warps", TeamStorageType.STRING, warps);
	}

	@Override
	public List<String> getClaimedChests() {
		return getConfig().getStringList("chests");
	}

	@Override
	public void setClaimedChests(List<String> chests) {
		setValue("chests", TeamStorageType.STRING, chests);

	}

}
