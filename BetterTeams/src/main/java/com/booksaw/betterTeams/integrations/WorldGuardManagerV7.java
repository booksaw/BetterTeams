package com.booksaw.betterTeams.integrations;

import com.booksaw.betterTeams.Main;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.util.Location;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagConflictException;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import org.bukkit.entity.Player;

import java.util.logging.Level;

public class WorldGuardManagerV7 {

	public static StateFlag TEAM_PVP_FLAG;

	public WorldGuardManagerV7() {

		FlagRegistry registry = WorldGuard.getInstance().getFlagRegistry();

		try {
			StateFlag flag = new StateFlag("TeamPvp", false);
			registry.register(flag);
			TEAM_PVP_FLAG = flag; // only set our field if there was no error
		} catch (FlagConflictException e) {
			// some other plugin registered a flag by the same name already.
			// you can use the existing flag, but this may cause conflicts - be sure to
			// check type
			Flag<?> existing = registry.get("TeamPvp");
			if (existing instanceof StateFlag) {
				TEAM_PVP_FLAG = (StateFlag) existing;
			} else {
				Main.plugin.getLogger().log(Level.SEVERE, "Conflicting flag found for TeamPvp Flag");
			}
		}

	}

	public boolean canTeamPvp(Player p) {
		LocalPlayer localPlayer = WorldGuardPlugin.inst().wrapPlayer(p);

		Location loc = new Location(new BukkitWorld(p.getWorld()), p.getLocation().getX(), p.getLocation().getY(),
				p.getLocation().getZ());
		RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
		RegionQuery query = container.createQuery();
		ApplicableRegionSet set = query.getApplicableRegions(loc);

		return set.testState(localPlayer, TEAM_PVP_FLAG);

	}

}
