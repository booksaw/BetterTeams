package com.booksaw.betterTeams.events;

import com.booksaw.betterTeams.Main;
import com.booksaw.betterTeams.Team;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.projectiles.ProjectileSource;

import java.util.Collection;
import java.util.Objects;

/**
 * This class is used to ensure that members of the same team cannot hit each other
 *
 * @author booksaw
 */
public class DamageManagement implements Listener {

	private final boolean disablePotions;
	private final boolean disableSelf;

	public DamageManagement() {
		disablePotions = Main.plugin.getConfig().getBoolean("disablePotions");
		disableSelf = Main.plugin.getConfig().getBoolean("playerDamageSelf");
	}

	/**
	 * This is used to cancel any events which would cause 2 players of the same team to damage each
	 * other
	 *
	 * @param e the damage event
	 */
	@EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
	public void onDamage(EntityDamageByEntityEvent e) {

		if (!(e.getEntity() instanceof Player)) {
			return;
		}
		Team temp = Team.getTeam((Player) e.getEntity());
		if (temp == null) {
			return;
		}
		try {
			if (e.getDamager() instanceof Player) {
				if (!Objects.requireNonNull(Team.getTeam((Player) e.getDamager())).canDamage(temp,
						(Player) e.getDamager())) {
					// they are on the same team
					e.setCancelled(true);
				}
			} else if (e.getDamager() instanceof Projectile
					&& !(e.getDamager() instanceof ThrownPotion)) {
				Projectile arrow = (Projectile) e.getDamager();
				ProjectileSource source = arrow.getShooter();
				if (source instanceof Player
						&& !Objects.requireNonNull(Team.getTeam((Player) source))
						.canDamage(temp, (Player) source)) {
					// they are on the same team
					if (disableSelf && source == e.getEntity()) {
						return;
					}
					e.setCancelled(true);
				}
			} else if (e.getDamager() instanceof ThrownPotion && disablePotions) {
				ThrownPotion arrow = (ThrownPotion) e.getDamager();
				ProjectileSource source = arrow.getShooter();
				if (source instanceof Player
						&& !Objects.requireNonNull(Team.getTeam((Player) source))
						.canDamage(temp, (Player) source)) {
					// they are on the same team
					e.setCancelled(true);
				}
			} else if (e.getDamager() instanceof TNTPrimed) {
				TNTPrimed explosive = (TNTPrimed) e.getDamager();
				Entity source = explosive.getSource();
				if (source instanceof Player
						&& !Objects.requireNonNull(Team.getTeam((Player) source))
						.canDamage(temp, (Player) source)) {
					// they are on the same team
					if (disableSelf && source == e.getEntity()) {
						return;
					}
					e.setCancelled(true);
				}
			}
		} catch (NullPointerException ex) {
			// thrown if the players team is null
		}
	}

	/**
	 * This method is used to detect if a negative potion is being thrown at members of the same team
	 *
	 * @param e the potion splash event
	 */
	@EventHandler(ignoreCancelled = true)
	public void onPotion(PotionSplashEvent e) {
		if (!(e.getEntity().getShooter() instanceof Player) || !disablePotions) {
			return;
		}
		Player thrower = (Player) e.getEntity().getShooter();
		Team team = Team.getTeam(thrower);
		// thrower is not in team
		if (team == null) {
			return;
		}

		Collection<PotionEffect> effects = e.getPotion().getEffects();
		boolean cancel = false;
		for (PotionEffect effect : effects) {
			PotionEffectType type = effect.getType();
			if (type.equals(PotionEffectType.BAD_OMEN) || type.equals(PotionEffectType.BLINDNESS)
					|| type.equals(PotionEffectType.NAUSEA) || type.equals(PotionEffectType.INSTANT_DAMAGE)
					|| type.equals(PotionEffectType.HUNGER)
					|| type.equals(PotionEffectType.MINING_FATIGUE)
					|| type.equals(PotionEffectType.UNLUCK)
					|| type.equals(PotionEffectType.WEAKNESS)
					|| type.equals(PotionEffectType.POISON)) {
				cancel = true;
				break;
			}
		}

		if (cancel) {
			Collection<LivingEntity> affectedEntities = e.getAffectedEntities();
			for (LivingEntity entity : affectedEntities) {
				try {
					if (entity instanceof Player
							&& !Objects.requireNonNull(Team.getTeam((Player) entity)).canDamage(team, thrower)) {
						if (disableSelf && entity == thrower) {
							continue;
						}
						e.setIntensity(entity, 0);
					}
				} catch (NullPointerException ex) {
					// thrown if the players team is null
				}
			}
		}
	}
}
