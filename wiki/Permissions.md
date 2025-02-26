Here is a list of permissions, if the permission is default it means that all players are given that permission by default and if you want the user to no longer have access to that feature, you need to remove the permission node.

## Permission Node Explanation
A permission node can take 3 states: 
- True: The player has this permission no matter what
- False: The player does not have this permission no matter what
- Undefined: Which players have the permission node is defined by the individual plugin

In the case that you do not define the permission node, betterteams tells spigot that all players get permissions to do all /team subcommands, this is to save every single server from having to add each individual permission node for every single subcommand to their permissions file (as most servers have all subcommands enabled). but if they want to remove one of the features from betterteams it still can be done in the method described below

### Revoking a permission node with LuckPerms
As shown in the screenshot below, when giving the group / player the permission node, set the value to false. This will result in the player not having access to that permission.
![LuckPerms editor](https://cdn.discordapp.com/attachments/797431152549298206/800815395611213824/unknown.png)

## List of permissions
If you are struggling to find the exact permission node you are after, try using CTRL + F and searching for it. For example, to find /team chat search 'team chat'. For searching purposes, admin commands are demoted by 'teama' instead of 'teamadmin' 


| Command                   | Permission                        | Description                                                                                                                              | Default? |
|---------------------------|-----------------------------------|------------------------------------------------------------------------------------------------------------------------------------------|----------|
| team                      | betterteams.standard              | Access to any of /team                                                                                                                   | Yes      |
| team join                 | betterteams.join                  | Joins the specified team                                                                                                                 | Yes      |
| team create               | betterteams.create                | Creates a new team                                                                                                                       | Yes      |
| team leave                | betterteams.leave                 | Leave the players current team                                                                                                           | Yes      |
| team home                 | betterteams.home                  | Teleports you to your team home                                                                                                          | Yes      | 
| team sethome              | betterteams.sethome               | Sets your teams home                                                                                                                     | Yes      | 
| team chat                 | betterteams.chat                  | Gives access to the team chat                                                                                                            | Yes      | 
| team info                 | betterteams.info                  | Gives access to view information about other teams                                                                                       | Yes      |
| team invite               | betterteams.invite                | Invites a player to the specified team                                                                                                   | Yes      |
| team kick                 | betterteams.kick                  | Kicks a player from the specified team                                                                                                   | Yes      |
| team ban                  | betterteams.ban                   | Bans the specified player from the team                                                                                                  | Yes      | 
| team unban                | betterteams.unban                 | Unbans the specified player from the team                                                                                                | Yes      | 
| team disband              | betterteams.disband               | Disbands the team the player is in                                                                                                       | Yes      | 
| team name                 | betterteams.name                  | Sets the name for the team                                                                                                               | Yes      | 
| team description          | betterteams.description           | Sets the description for the team                                                                                                        | Yes      | 
| team open                 | betterteams.open                  | Toggles if the team is open to everyone                                                                                                  | Yes      | 
| team promote              | betterteams.promote               | Promotes the specified player up a rank in the team                                                                                      | Yes      |
| team demote               | betterteams.demote                | Demotes the specified player down a rank in the team                                                                                     | Yes      |  
| team bal                  | betterteams.balance               | View your teams balance                                                                                                                  | Yes      | 
| team withdraw             | betterteams.balance               | Withdraw money from the teams balance                                                                                                    | Yes      |
| team deposit              | betterteams.balance               | Deposit money into the teams balance                                                                                                     | Yes      |
| team title                | betterteams.title                 | Change that players title within the team                                                                                                | Yes      |
| team title                | betterteams.title.color.color     | Allows the user to add color codes into their titles                                                                                     | No       |
| team title                | betterteams.title.color.format    | Allows the user to add format codes into their titles                                                                                    | No       |
| team title                | betterteams.title.self            | Allows the user to change their own title even if they are not the team owner                                                            | No       |
| team rank                 | betterteams.rank                  | View the rank of a team                                                                                                                  | Yes      |
| team top                  | betterteams.top                   | View the top teams                                                                                                                       | Yes      | 
| team baltop               | betterteams.baltop                | View the richest                                                                                                                         | Yes      | 
| team color                | betterteams.color                 | Change the color of the team name                                                                                                        | Yes      | 
| team ally                 | betterteams.ally                  | Add an alliance for your team                                                                                                            | Yes      | 
| team neutral              | betterteams.neutral               | Remove an alliance from your team                                                                                                        | Yes      | 
| team allychat             | betterteams.allychat              | Send a message that only your team allies will see                                                                                       | Yes      |
| team setowner             | betterteams.setowner              | Set a player to be the new owner of the team                                                                                             | Yes      | 
| team warp                 | betterteams.warp                  | Warp to the location set by team members                                                                                                 | Yes      | 
| team setwarp              | betterteams.setwarp               | Create a new warp for your team to warp to                                                                                               | Yes      |
| team delwarp              | betterteams.delwarp               | Delete a warp from you team                                                                                                              | Yes      |
| team warps                | betterteams.warps                 | View a list of your teams warps                                                                                                          | Yes      | 
| team chest claim          | betterteams.chest.claim           | Claim a chest                                                                                                                            | Yes      | 
| team chest remove         | betterteams.chest.remove          | Remove a chest claimed by your team                                                                                                      | Yes      | 
| team chest removeall      | betterteams.chest.removeall       | Removes all team chest claims                                                                                                            | Yes      | 
| team echest               | betterteams.echest                | Opens your team's ender chest                                                                                                            | Yes      |
| team rankup               | betterteams.rankup                | Ranks your team up to the next level                                                                                                     | Yes      |
| team tag                  | betterteams.tag                   | Change the tag of your team                                                                                                              | Yes      |
| team pvp                  | betterteams.pvp                   | Toggles if pvp is allowed between team members                                                                                           | Yes      | 
| teama                     | betterteams.admin                 | Allows access to admin commands                                                                                                          | No       |  
| teama reload              | betterteams.admin.reload          | Reloads the config                                                                                                                       | No       |
| teama chatspy             | betterteams.admin.chatspy         | Spy on messages sent to team chats                                                                                                       | No       |
| teama version             | betterteams.admin.version         | View better teams plugin version                                                                                                         | No       | 
| teama home                | betterteams.admin.home            | Teleport to any teams home                                                                                                               | No       | 
| teama title               | betterteams.admin.title           | Change other users titles                                                                                                                | No       | 
| teama name                | betterteams.admin.name            | Change the name of a team                                                                                                                | No       | 
| teama description         | betterteams.admin.description     | Change the description of a team                                                                                                         | No       | 
| teama open                | betterteams.admin.open            | Set if a team is open or invite only                                                                                                     | No       | 
| teama invite              | betterteams.admin.invite          | Invite a player to a team                                                                                                                | No       | 
| teama create              | betterteams.admin.create          | Create a team without an owner                                                                                                           | No       |
| teama join                | betterteams.admin.join            | Allow a player to join a team                                                                                                            | No       | 
| teama demote              | betterteams.admin.demote          | Demote a player within their team                                                                                                        | No       | 
| teama promote             | betterteams.admin.promote         | Promote a player within their team                                                                                                       | No       |
| teama setowner            | betterteams.admin.setowner        | Used to set a player to team owner when "singleOwner" is set to true                                                                     | No       | 
| teama leave               | betterteams.admin.leave           | Force a player to leave a team                                                                                                           | No       | 
| teama warp                | betterteams.admin.warp            | Warp to a location set by a team                                                                                                         | No       | 
| teama setwarp             | betterteams.admin.setwarp         | Set a warp for a team                                                                                                                    | No       | 
| teama delwarp             | betterteams.admin.delwarp         | Delete a warp for a team                                                                                                                 | No       | 
| teama purge               | betterteams.admin.purge           | Purges all team scores and resets them back to 0                                                                                         | No       | 
| teama score set           | betterteams.admin.score.set       | Sets the teams score to the specified amount                                                                                             | No       | 
| teama score add           | betterteams.admin.score.add       | Adds the specified amount to the team's score                                                                                            | No       | 
| teama score remove        | betterteams.admin.score.remove    | Removes the specified amount from the team's score                                                                                       | No       | 
| teama money set           | betterteams.admin.money.set       | Sets the teams balance to the specified amount                                                                                           | No       | 
| teama money add           | betterteams.admin.money.add       | Adds the specified amount to the team's balance                                                                                          | No       | 
| teama moeny remove        | betterteams.admin.money.remove    | Removes the specified amount from the team's balance                                                                                     | No       | 
| teama disband             | betterteams.admin.disband         | Disbands the specified team                                                                                                              | No       | 
| teama setrank             | betterteams.admin.setrank         | Sets the rank of the specified team                                                                                                      | No       | 
| teama tag                 | betterteams.admin.tag             | Sets the tag of the specified team                                                                                                       | No       |
| teama color               | betterteams.admin.color           | Sets the specified teams color                                                                                                           | No       |    
| teama chest claim         | betterteams.admin.chest.claim     | Claim a chest for a team                                                                                                                 | No       | 
| teama chest remove        | betterteams.admin.chest.remove    | Remove a claim from a chest                                                                                                              | No       | 
| teama chest removeall     | betterteams.admin.chest.removeall | Remove all chest claims from a team                                                                                                      | No       | 
| teama chest disableclaims | betterteams.admin.chest.disable   | Disable chest claims                                                                                                                     | No       |
| teama chest enableclaims  | betterteams.admin.chest.enable    | Enable chest claims                                                                                                                      | No       |
| teama echest              | betterteams.admin.echest          | Open that team's echest                                                                                                                  | No       |
| teama teleport            | betterteams.admin.teleport        | Teleport the team                                                                                                                        | No       |
| teama holo create         | betterteams.admin.holo            | Creates a hologram at your location (Requires [HolographicDisplays](https://dev.bukkit.org/projects/holographic-displays))               | No       |  
| teama holo delete         | betterteams.admin.holo            | Deletes the closest better teams hologram to you  (Requires [HolographicDisplays](https://dev.bukkit.org/projects/holographic-displays)) | No       |  
| Command cooldown          | betterteams.cooldown.bypass       | Bypass the command cooldown which can be configured in team.yml                                                                          | No       | 
| Price bypass              | betterteams.cost.bypass           | Bypass the command cost which can be configured in team.yml                                                                              | No       | 
| Warmup bypass             | betterteams.warmup.bypass         | Bypass the warmup for teleport commands                                                                                                  | No       |
|                           | betterTeams.admin.selector        | Allows usage of @a, @p                                                                                                                   | No       | 
| Chest claim bypass        | betterTeams.chest.bypass          | Bypass all chest claims                                                                                                                  | No       |
|                           | betterTeams.teamName              | Display the players team name in the tab menu                                                                                            | Yes      | 