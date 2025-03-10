Betterteams supports displaying the team name in the TAB menu, and next to the players name in-game.

There are 2 main ways this can be done.

## Method 1 - Using the inbuilt TAB management system

To use this system a few configuration options must be set. These options are all found in the BetterTeams config.yml
file.

* Set `useTeams` to `true`
* Set `displayTeamName` to either `prefix` or `suffix` depending on where you want the team name displayed

With these values configured, the team name should be displayed both in the TAB menu and alongside the player's name
in-game. If it is not, see the [conflict section of this wiki page](#conflict)

## Method 2 - Using placeholders

This method takes more configuration, but works with more plugin configurations. As mentioned on
the [PlaceholderAPI wiki page](https://github.com/booksaw/BetterTeams/wiki/PlaceholderAPI), to display the team name
using placeholders, the placeholder `BetterTeams_name` should be used.

This then must be used within another plugin for
example [TabList](https://www.spigotmc.org/resources/animated-tab-tablist.46229) (please note, this individual plugin
cannot be tested, and there are alternative options, but this is the style of plugin you should use). For more
information you will have to view the plugin page of the plugin that supports PlaceholderAPI.

# Conflict

Plugin conflicts in this area are caused by minecraft limitations. To display team names in the TAB menu along with
other functionality, the player must be assigned to a [
`/minecraft:team` team](https://minecraft.fandom.com/wiki/Commands/team). These teams by nature are more limited than
the teams created within the BetterTeams plugin along with being more temperamental to work with.
The issues arise as a player can only be assigned to a single team, but as many plugins make use of this feature,
sometimes two plugins cannot function on the same server as both plugins require the player to be added to a plugin
controlled `minecraft:team`.

The solution to this problem is to disable the Betterteams `minecraft:team` component and
use [Method 2](#method-2---using-placeholders) to display the team name in the TAB menu. To disable the BetterTeams
`minecrat:team` component, in the BetterTeams config.yml set `useTeams` to false to totally disable the feature and
remove any conflicts.

Alternatively, if you want to continue using [Method 1](#method-1---using-the-inbuilt-tab-management-system), you can
progressively disable other plugins until the team name displays in the TAB list. This will then identify the
conflicting plugin, so additional configurations could be changed in that plugin instead.