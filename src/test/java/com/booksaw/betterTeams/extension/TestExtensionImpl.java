package com.booksaw.betterTeams.extension;

// only for testing
public class TestExtensionImpl extends BetterTeamsExtension {

	public boolean onLoadCalled = false;
	public boolean onEnableCalled = false;
	public boolean onDisableCalled = false;


	@Override
	public void onLoad() {
		onLoadCalled = true;
	}

	@Override
	public void onEnable() {
		onEnableCalled = true;
	}

	@Override
	public void onDisable() {
		onDisableCalled = true;
	}
}
