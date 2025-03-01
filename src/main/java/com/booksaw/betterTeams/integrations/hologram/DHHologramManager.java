package com.booksaw.betterTeams.integrations.hologram;

import eu.decentsoftware.holograms.api.DHAPI;
import eu.decentsoftware.holograms.api.holograms.Hologram;
import org.bukkit.Location;

import java.util.Collections;
import java.util.UUID;

public class DHHologramManager extends HologramManager {
	/*
	 * Creates a new DecentHolograms hologram.
	 */
	@Override
	public LocalHologram createLocalHolo(Location location, HologramType type) {
		return new DHHologramImpl(DHAPI.createHologram(UUID.randomUUID().toString(), location));
	}

	/*
	 * A wrapper class for interfacing with DecentHolograms holograms.
	 */
	private static final class DHHologramImpl implements LocalHologram {
		private final Hologram hologram;

		public DHHologramImpl(Hologram hologram) {
			this.hologram = hologram;
		}

		@Override
		public void appendText(String text) {
			DHAPI.addHologramLine(hologram, text);
		}

		@Override
		public void clearLines() {
			DHAPI.setHologramLines(hologram, Collections.emptyList());
		}

		@Override
		public void delete() {
			hologram.delete();
		}

		@Override
		public Location getLocation() {
			return hologram.getLocation();
		}
	}
}
