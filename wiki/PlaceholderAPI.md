## [Placeholder API](https://www.spigotmc.org/resources/placeholderapi.6245/)

_Click on placeholderAPI to go to the spigot page_

If you are unfamiliar with placeholderAPI please watch the explanation video
by [Premiere Setups](https://www.youtube.com/watch?v=r8weU5HRxL4) for the basics.

Placeholders are supported in many ways, primarily by [providing placeholders](#Provided-Placeholders). Along with the
provided placeholders, all messages in messages.yml support placeholders so you can alter all messages to your heart's
desire.

Please note, there is no ecloud expansion required for these placeholders. As long as you have BetterTeams and
PlaceholderAPI on your server, these will all work.

### Requesting a new placeholder

If you need a placeholder from better teams that is not currently listed below, you can request it
by [creating a feature request](https://github.com/booksaw/BetterTeams/issues/new/choose)

### Provided Placeholders

As there are so many placeholders, every iteration cannot be displayed. Instead, a method for building placeholders is
shown.
There are several different types of placeholders. Firstly you need to pick the context of the placeholder that is
relevant to you. Secondly the {type} variable in the placeholder can be replaced with the specific placeholder you want,
see below the first list for options.

* `%betterteams_{type}%` - A placeholder in the context of the current player, used for example to display team
  information on a scoreboard
* `%betterteams_position_{type}_{rank}%` - Displays information about the team at the defined {rank} on the
  `/team score` leaderboard
* `%betterteams_balanceposition_{type}_{rank}%` - Displays information about the team at the defined {rank} on the
  `/team baltop` leaderboard
* `%betterteams_membersposition_{type}_{rank}%` - Displays information about the team at the defined {rank} based on the
  number of members in the team
* `%betterteams_static_{type}_{team}%` - Displays information about the {team} referenced by name in the placeholder
* `%betterteams_staticplayer_{type}_{player}%` - Displays information about the {player} referenced by name in the
  placeholder

Now you have the base structure of the placeholder, you need to decide which placeholder for the specified team / player
you want to use in place of the {rank} variable. The options are as follows:

* `name` - The name of the team
* `tag` - The tag of the team
* `displayname` - The display name of the team
* `description` - The description of the team
* `open` - If the team is invite only or open to everyone
* `score` - The score of the team
* `money` - The current balance of the team
* `color` - The color set by the team
* `onlinelist` - A list of all online players in the team
* `offlinelist` - A list of all the offline players in the team
* `online` - The number of online players in the team
* `members` - The total number of players in the team
* `level` - The current level of the team
* `maxmoney` - The maximum balance the team is allowed at their current level
* `maxmembers` - The maximum number of players the team is allowed at their current level
* `maxwarps` - The maximum number of warps the team is allowed at their current level
* `pvp` - If the team has pvp toggled on using `/team pvp`
* `rank` - The rank the current player holds within the team (only works on
  `%betterteams_{type}% and %betterteams_staticplayer_{type}_{player}` placeholders)
* `title` - The current title of the player within the team (only works on
  `%betterteams_{type}% and %betterteams_staticplayer_{type}_{player}` placeholders)

## Example

A few examples follow so the syntax is more clear.

### Example 1

The team name to use in the tab list. This is a relational placeholder to the current player. `%betterteams_name%`

### Example 2

The score of the team with the highest score on the server. `%betterteams_position_score_1%`