# Plugin Integrations

This plugin supports many other plugins. Here is a list of other plugins BetterTeams can work with and how they work together:

:::note

If your plugin uses the BetterTeams API, [make a blank issue](https://github.com/booksaw/BetterTeams/issues/new/choose) with information including a link to your plugin and I will add it to the wiki or you can make a PR!

:::

## Free plugins

### [BetterTeamsAddon](https://www.spigotmc.org/resources/betterteamsaddon.119246/)

This plugin adds additional placeholders for information such as kills and deaths, which integrate with betterteams. There are also placeholders setup so they can be used within learderboards, allowing to show the teams with the top number of kills etc..
Examples of placeholders (check the plugin page to see the full list):

* `%betterteamsaddon_kills%` - Returns the player's team's kills
* `%betterteamsaddon_deaths%` - Returns the player's team's deaths
* `%betterteamsaddon_damages%` - Returns the player's team's damages
* `%betterteamsaddon_leaderboard_kills_[number]_name` - The name of the team that is in position [number] on the kills leaderboard.

### [BetterTeamsClaim](https://modrinth.com/plugin/betterteamsclaim)

This plugin provides land claiming support for BetterTeams. The key features are:

* Only team owners can create claims.
* Region size calculation and claim validation.
* Prevents overlapping claims.

### [BetterTeamsGUI](https://modrinth.com/plugin/betterteamsgui)

This plugin provides several GUIs, such as a Member List GUI, which make interacting with BetterTeams a simpler user experience. Some of the commands are as follows:

* `/teams` - open team GUI
* `/teams warp` - open warp GUI
* `/teams members` - open team members GUI
* `/teams balance` - open balance GUI

### [DecentHolograms](https://www.spigotmc.org/resources/96927/) or [Holographic displays](https://dev.bukkit.org/projects/holographic-displays)

Either of these plugins allow for the creation of holographic leaderboards which can either order the teams by score or by team balance. Commands associated with these integrations are

* `/teama holo create <score/money>` - Create a holographic leaderboard of that type where you are standing
* `/teama holo remove` - Remove the closest BetterTeams managed hologram to you

### [Defensive Turrets](https://www.spigotmc.org/resources/defensiveturrets-defend-yourself-using-turrets-1-8-1-16.67188/)

Turrets do not attack allies

### [Placeholder API](https://www.spigotmc.org/resources/placeholderapi.6245/)

This plugin supports many placeholders which can be used through placeholder API. For more information view the [placeholderAPI page of the wiki](./PlaceholderAPI)

### [LuckPerms](https://luckperms.net/)

BetterTeams can hook into the LuckPerms permission plugin to provide dynamic contexts. For more information view the [LuckPerms page of the wiki](./LuckPerms).

### [PrimaxTeamGlow](https://www.spigotmc.org/resources/primaxteamglow.127974/)

Highlight your fellow teammates with the glowing effect!

### [UltimateClaims](https://songoda.com/marketplace/product/ultimateclaims-the-ultimate-claiming-plugin.65)

This plugin supports altering how the claiming system of UltimateClaims works. For more information view the [UltimateClaims page of the wiki](./UltimateClaims)

### [Vault](https://www.spigotmc.org/resources/vault.34315/)

A team can have a balance associated with their team, this is managed with the commands

* `/team deposit <amount>`
* `/team withdraw <amount>`
* `/team bal [team]`
* `/team baltop`
This allows teams to have a balance associated with them. This feature also allows for better teams commands to have an associated cost which can be taken out of the team balance when they are run.

### [WorldGuard](https://dev.bukkit.org/projects/worldguard)

There is a WorldGuard flag called `teamPvp` which when set to allow, will let players of the same team hit each other within that region.

## Premium Plugins

:::warning

Support for these plugins is either added by the authors of these plugins or community members, they are not tested by the lead developer of BetterTeams so purchase at your own risk!

:::

### [AxKoth](https://www.spigotmc.org/resources/axkoth-the-all-in-one-koth-plugin.114159/)

King of hill plugin with team support for BetterTeams

### [zKoth](https://www.spigotmc.org/resources/zkoth-king-of-the-hill.76749/)

Players can win a configurable amount of score for their team when winning king of the hill events. This amount is configurable in the config.yml file under zKoth pointsPerCapture heading.
