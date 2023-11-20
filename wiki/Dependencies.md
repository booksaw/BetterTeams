This plugin supports many other plugins. Here is a list of other plugins BetterTeams can work with and how they work together: 

## [Placeholder API](https://www.spigotmc.org/resources/placeholderapi.6245/)
This plugin supports many placeholders which can be used through placeholder API. For more information view the [placeholderAPI page of the wiki](https://github.com/booksaw/BetterTeams/wiki/PlaceholderAPI)

## [UltimateClaims](https://songoda.com/marketplace/product/ultimateclaims-the-ultimate-claiming-plugin.65)
This plugin supports altering how the claiming system of UltimateClaims works. For more information view the [UltimateClaims page of the wiki](https://github.com/booksaw/BetterTeams/wiki/UltimateClaims)

## [Vault](https://www.spigotmc.org/resources/vault.34315/)
A team can have a balance associated with their team, this is managed with the commands 
* /team deposit <amount>
* /team withdraw <amount>
* /team bal [team]
* /team baltop 
This allows teams to have a balance associated with them. This feature also allows for better teams commands to have an associated cost which can be taken out of the team balance when they are run. 

## [DecentHolograms](https://www.spigotmc.org/resources/96927/) or [Holographic displays](https://dev.bukkit.org/projects/holographic-displays)
Either of these plugins allow for the creation of holographic leaderboards which can either order the teams by score or by team balance. Commands associated with these integrations are
* /teama holo create <score/money> - Create a holographic leaderboard of that type where you are standing
* /teama holo remove - Remove the closest BetterTeams managed hologram to you

## [zKoth](https://www.spigotmc.org/resources/zkoth-king-of-the-hill.76749/)
The latest version of zKoth no longer works with BetterTeams

Players can win a configurable amount of score for their team when winning king of the hill events. This amount is configurable in the config.yml file under zKoth pointsPerCapture heading.

## [Defensive Turrets](https://www.spigotmc.org/resources/defensiveturrets-defend-yourself-using-turrets-1-8-1-16.67188/)
Turrets do not attack allies

## [WorldGuard](https://dev.bukkit.org/projects/worldguard)
There is a WorldGaurd flag called `teamPvp` which when set to allow, will let players of the same team hit each other within that region.