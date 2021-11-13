package com.booksaw.betterTeams.events;

import com.booksaw.betterTeams.Main;
import com.booksaw.betterTeams.Team;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.projectiles.ProjectileSource;

import java.util.Collection;
import java.util.Objects;

/**
 * This class is used to ensure that members of the same team cannot hit each
 * other
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
	 * This is used to cancel any events which would cause 2 players of the same
	 * team to damage each other
	 *
	 * @param e the damage event
	 */
	@EventHandler(ignoreCancelled = true)
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
			} else if (e.getDamager() instanceof Projectile && !(e.getDamager() instanceof ThrownPotion)) {
				Projectile arrow = (Projectile) e.getDamager();
				ProjectileSource source = arrow.getShooter();
				if (source instanceof Player
						&& !Objects.requireNonNull(Team.getTeam((Player) source)).canDamage(temp, (Player) source)) {
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
						&& !Objects.requireNonNull(Team.getTeam((Player) source)).canDamage(temp, (Player) source)) {
					// they are on the same team
					e.setCancelled(true);
				}
			}
		} catch (NullPointerException ex) {
			// thrown if the players team is null
		}
	}

	/**
	 * This method is used to detect if a negative potion is being thrown at members
	 * of the same team
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
			String type = effect.getType().getName();
			if (type.equals(PotionEffectType.BAD_OMEN.getName()) || type.equals(PotionEffectType.BLINDNESS.getName())
					|| type.equals(PotionEffectType.CONFUSION.getName()) || type.equals(PotionEffectType.HARM.getName())
					|| type.equals(PotionEffectType.HUNGER.getName())
					|| type.equals(PotionEffectType.SLOW_DIGGING.getName())
					|| type.equals(PotionEffectType.UNLUCK.getName())
					|| type.equals(PotionEffectType.WEAKNESS.getName())
					|| type.equals(PotionEffectType.POISON.getName())) {
				cancel = true;
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
