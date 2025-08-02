---
sidebar_position: 98
---

# FAQ

## What is score?

By default, players are awarded score for kills (1 score for 1 kill), however players may also gain and lose score through plugin integrations, both direct and indirect. For more information view the dedicated wiki page on [Score](./Score)

## How do I unally a team/player?

Use `/team neutral <teamname/playername>`. Replacing teamname with the name of the team you wish to unally or replacing playername with the name of a player on the team you wish to unally.

Further information on commands can be found on the [commands page](./Commands)

## How do I kick or ban offline players?

BetterTeams allows offline players to be kicked and banned by default. This issue occurs often when you allow bedrock players to join your server, alternatively this can occur on some 'optimised' forks of paper such as pufferfish generally when the server is running in offline mode.

This occurs because BetterTeams uses UUIDs to store player information and in certain circumstances these UUIDs do not seem to be accurately returning player data when using the server configurations described above.

If you are able to find any reproducible steps from a fresh install of your server software / plugins, please add it to [this github issue](https://github.com/booksaw/BetterTeams/issues/489) so I can notify the relevant developer about the issue.

NOTE: Using plugins like Geyser are not officially supported by BetterTeams and you will have to demonstrate any bugs you encounter while using that software are not caused by Geyser.

## How do I charge a player to use a command/How do I add a cooldown to a command?

See the dedicated wiki page on [command costs and cooldowns](./configuration/Command-Cost-and-Cooldown)

## How do I disable the team name prefixing messages in chat?

You can enable/disable the team prefix within the `config.yml` file.

To do this you change `prefix: true` to `prefix: false`
This can be found near the top of the `config.yml` file

## What placeholders are included with BetterTeams?

See the dedicated wiki page for all information about [PlaceholderAPI](./dependencies/PlaceholderAPI)

## How do I stop players running a specific betterteams command?

* **Option 1**: If you do not want anyone to be able to run them, disable the commands, see the [Team Permissions](./permissions/Team-Permissions) wiki page
* **Option 2**: Revoke the permission node for the command using your permission manager, see the [Permission Nodes](./permissions/Permissions) wiki page

## How do I disable the team name displaying in the tab menu?

This is caused by the minecraft team mechanics, these mechanics are required for a players team name to be displayed above their players head. See the wiki page on [managing the tab menu](./configuration/Managing-the-TAB-Menu) for solutions

## How do I stop the team name appearing in death, join and achievement messages?

This is the same cause and solution as the [question above](#how-do-i-disable-the-team-name-displaying-in-the-tab-menu)

## How do I increase the maximum number of players on a team?

In the `config.yml` file, you can edit the value of `teamLimit` in the levels section of the config. For more information about how team limits and team levels work, see the [Team Levels](./configuration/Team-Levels) page of the wiki
