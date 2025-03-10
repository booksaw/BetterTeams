## Command Selectors

BetterTeams supports minecraft command selectors, these include `@a` and `@p` (the other selectors are technically
supported but not very useful), these can be used both in place of a player requirement and a team name (the action will
be performed on the team that the player is in). This is useful when dealing with a teams score in particular

## Command List

If you are struggling to find the exact command you are after, try using CTRL + F and searching for it. For example, to
find /team chat search 'team chat'. For searching purposes, admin commands are demoted by 'teama' instead of 'teamadmin'

## /team

For permissions view the [permissions page](https://github.com/booksaw/BetterTeams/wiki/Permissions)

All /team commands can have a cost and cooldown associated with them, to edit these details, view the team.yml file
within the plugin folder.

* /team join <team> - Joins the specified team
* /team create <team> - Creates a new team with that name
* /team leave - Leaves the current team
* /team home - Teleports to the teams home location
* /team chat [message] - If a message is included, it will send a single message to the team chat, if no message is
  included the player will be moved to the team chat (all further messages will go there)
* /team info [team/player] - Provides information on the team or the team that the specified player is in.
* /team top - View the top teams
* /team baltop - View the richest teams
* /team rank [team] - View the rank of a team
* /team bal - View your teams balance
* /team deposit <amount> - Deposit money into the teams balance
* /team allychat [message] - Send a message that only team allies can see
* /team warp <name> [password] - Warp to a location set by your team
* /team echest - Open your team's ender chest

* /team
* /team help - Provides a personalised help page, which will only show the commands that the user has access to

Commands that admins and owners of teams have access to:

* /team invite <player> - Invites the specified player to the team
* /team kick <player> - Kicks the specified player from the team (players can only kick members a lower rank than their
  own)
* /team ban <player> - Bans a player from the team (players can only ban users a lower rank than their own)
* /team unban <player> - Allows the specified player to join back into the team.
* /team sethome - Sets the location of the team home (accessed by /team home)
* /team delhome - Delete your teams home
* /team withdraw <amount> - Withdraw money from the teams balance
* /team setwarp <name> \[password] - Set a new warp for your team
* /team delwarp <warp> \[password] - Delete a warp from your team, the password is not required for owners]
* /team chest claim - Claim the chest you are standing on
* /team chest remove - Remove the claim on the chest you are standing on
* /team pvp - Toggle if pvp is enabled between team members

Commands that only the owners of teams have access to:

* /team disband - Forces all members of the team to leave (cannot be undone)
* /team name <name> - Changes the name of the team
* /team description <description> - Changes the description of the team
* /team open - Toggles if the team is invite-only or open to everyone
* /team promote <player> - Promotes a player to the next rank
* /team demote <player> - Demotes a player from their current rank
* /team title <player/me> <title> - Change that players title within the team
* /team color <color> - Change the color of the team name
* /team ally <team> - Request / accept an alliance with another team
* /team neutral <team> - Remove an alliance with another team
* /team setowner <player> - Set a member of the team to be the new team owner (only avaliable if "singleOwner" is
  enabled)
* /team chest removeall - Remove all claimed chests from this team
* /team rankup - Advance to the next level
* /team tag - Change the teams tag

## /teama

For permissions view the [permissions page](https://github.com/booksaw/BetterTeams/wiki/Permissions)

Admin commands can have the prefix [teamadmin] or [teama] both work exactly the same

* /teama help - Displays a help page of all admin commands
* /teama reload - Reloads all configuration files
* /teama chatspy - Spy on messages sent to team chats
* /teama title < player > <title> - Sets that players title
* /teama home <team> - Teleport to a teams home
* /teama version - View better teams plugin version
* /teama name <team> <name> - change the name of a team
* /teama description <team> <description> - Change the description of a team
* /teama invite <team> <player> - Send an invite for a team to that player
* /teama join <team> <player> - force a player to join a team
* /teama leave <player> - Force a player to leave their team
* /teama create <team> - Create a new team without any members (other admin commands will be required to add a player to
  the team)
* /teama promote <player> - Promote a player within their team
* /teama demote <player> - Demote a player within their team
* /teama setowner <player> - Set that player to be owner of their team (only avaliable if "singleOwner" is enabled)
* /teama warp <team> <warp> - Warp to a location set by a team, leave the warp blank for a list of that teams warps
* /teama setwarp <team> <warp> \[password] - Set a new warp for that team with the specified password (leave blank for
  no password)
* /teama delwarp <team> <warp> - Delete a warp from the specified team
* /teama purge - Reset all team scores back to 0
* /teama score <set/add/remove> <player/team> <score> - Edit a team/players score through commands
* /teama money <set/add/remove> <player/team> <balance> - Edit a team/players balance through commands
* /teama disband <team> - Disbands the specified team
* /teama setrank <team> <rank> - Sets the rank of the specified team
* /teama tag <team> <tag> - Change the tag for the specified team
* /teama color <team> <color> - Changes that teams color code to the specified version
* /teama chest claim <team> - Claims the chest you are standing on for that team
* /teama chest remove - Removes the claim from the chest you are standing on
* /teama chest removeall <team> - Removes all chest claims from that team
* /teama chest disableclaims - Disables chest claims until they are re-enabled or the server restarts
* /teama chest enableclaims - Enables chest claims
* /teama echest <team> - view the specified team's ender chest
* /teama teleport <team> \[x] \[y] \[z] \[pitch] \[yaw] - Teleport the team to the specified location
* /teama holo create - Creates a hologram at your location (
  requires [DecentHolograms](https://www.spigotmc.org/resources/96927/)
  or [HolographicDisplays](https://dev.bukkit.org/projects/holographic-displays))
* /teama holo remove - Deletes the closest hologram to you (
  requires [DecentHolograms](https://www.spigotmc.org/resources/96927/)
  or [HolographicDisplays](https://dev.bukkit.org/projects/holographic-displays))