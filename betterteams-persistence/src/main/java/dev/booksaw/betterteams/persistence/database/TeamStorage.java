package dev.booksaw.betterteams.persistence.database;

import dev.booksaw.betterteams.domain.Team;

import java.sql.SQLException;

public interface TeamStorage {

	void initialize() throws SQLException;

	void saveTeam(Team team) throws SQLException;

}
