package com.booksaw.betterTeams.score;

import com.booksaw.betterTeams.Main;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class ScoreChange {

	public static final List<ScoreChange> spams = new ArrayList<>();
	public final ChangeType type;
	public final Player source;
	public final Player target;
	private long expires;

	public ScoreChange(ChangeType type, Player source) {
		this(type, source, null);
	}

	public ScoreChange(ChangeType type, Player source, Player target) {
		this.type = type;
		this.source = source;
		this.target = target;
		update();
		spams.add(this);
	}

	public static boolean isSpam(ChangeType type, Player source) {
		return isSpam(type, source, null);
	}

	/**
	 * Used to check if an action is a spam action, this will increase the timer if
	 * it is Returns false if it is not a spam kill
	 *
	 * @param type   The type of score change
	 * @param source The source player of the score change
	 * @param target The target player of the score change
	 * @return If the event is spam
	 */
	public static boolean isSpam(ChangeType type, Player source, Player target) {

		ScoreChange change = null;

		for (ScoreChange temp : spams) {
			if (temp.type == type && temp.source == source && temp.target == target) {
				change = temp;
				break;
			}
		}

		if (change == null) {
			return false;
		}

		if (change.hasExpired()) {
			spams.remove(change);
			return false;
		}

		change.update();
		return true;

	}

	public boolean hasExpired() {
		return expires < System.currentTimeMillis();
	}

	public void update() {
		expires = System.currentTimeMillis() + (Main.plugin.getConfig().getInt("spamThreshold") * 1000L);
	}

	public enum ChangeType {
		KILL, DEATH
	}

}
