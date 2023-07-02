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
        public void createHolo(Location location, HologramType type) {
                LocalHologram holo = new DHHologramImpl(DHAPI.createHologram(UUID.randomUUID().toString(), location));
                holos.put(holo, type);
                reloadHolo(holo, type);
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
