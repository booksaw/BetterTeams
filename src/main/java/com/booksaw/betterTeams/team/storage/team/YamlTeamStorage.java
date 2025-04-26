package com.booksaw.betterTeams.team.storage.team;

import com.booksaw.betterTeams.Team;
import com.booksaw.betterTeams.TeamPlayer;
import com.booksaw.betterTeams.Warp;
import com.booksaw.betterTeams.team.storage.storageManager.YamlStorageManager;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public abstract class YamlTeamStorage extends TeamStorage {

	protected final YamlStorageManager teamStorage;

	protected YamlTeamStorage(Team team, YamlStorageManager teamStorage) {
		super(team);
		this.teamStorage = teamStorage;
	}

	@Override
	protected void setValue(String location, TeamStorageType storageType, Object value) {
		getConfig().set(location, value);
		saveFile();
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
	public List<TeamPlayer> getPlayerList() {
		List<String> lst = getConfig().getStringList("players");
		List<TeamPlayer> toReturn = new ArrayList<>();

		for (String temp : lst) {
			TeamPlayer player = new TeamPlayer(temp);
			if(getAnchoredPlayerList().contains(player.getPlayerUUID()))
				player.setAnchor(true);
			toReturn.add(player);
		}

		return toReturn;
	}

	@Override
	public void setPlayerList(List<String> players) {
		setValue("players", TeamStorageType.STRING, players);
	}

	@Override
	public List<UUID> getAnchoredPlayerList() {
		List<String> lst = getConfig().getStringList("anchoredPlayers");
		List<UUID> toReturn = new ArrayList<>();

		for (String temp : lst) {
			toReturn.add(UUID.fromString(temp));
		}

		return toReturn;
	}

	@Override
	public void setAnchoredPlayerList(List<String> players) {
		setValue("anchoredPlayers", TeamStorageType.STRING, players);
	}

	@Override
	public void setAnchor(TeamPlayer player, boolean anchor) {
		// covered by setAnchoredPlayerList
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
			} else {
				section.set("echest." + i, null);
			}
		}
		saveFile();
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

	public abstract ConfigurationSection getConfig();

	/**
	 * Used to save whaver needs saving when a change is made
	 */
	protected abstract void saveFile();

	@Override
	public void addBan(UUID component) {
		// not needed
	}

	@Override
	public void removeBan(UUID component) {
		// not needed
	}

	@Override
	public void addAlly(UUID ally) {
		// not needed
	}

	@Override
	public void removeAlly(UUID ally) {
		// not needed
	}

	@Override
	public void addAllyRequest(UUID requesting) {
		// not needed
	}

	@Override
	public void removeAllyRequest(UUID requesting) {
		// not needed
	}

	@Override
	public void addWarp(Warp component) {
		// not needed
	}

	@Override
	public void removeWarp(Warp component) {
		// not needed
	}

	@Override
	public void promotePlayer(TeamPlayer promotePlayer) {
		// not needed 
	}

	@Override
	public void demotePlayer(TeamPlayer demotePlayer) {
		// not needed
	}

	@Override
	public void setTitle(TeamPlayer player) {
		// not needed
	}
}
