package com.booksaw.betterTeams.commands.teama.meta;

import com.booksaw.betterTeams.commands.ParentCommand;

public class MetaTeama extends ParentCommand {

	public MetaTeama() {
		super("meta");
		addSubCommand(new MetaSetTeama());
		addSubCommand(new MetaGetTeama());
		addSubCommand(new MetaRemoveTeama());
	}
}
