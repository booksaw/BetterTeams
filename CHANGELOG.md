# Changelog

All notable changes to this project will be documented in this file against their release version.

The newest release will always be maintained at the top of the file.

## 5.0.0

This is a major release which includes several breaking changes for the API

Introducing BetterTeams Extensions, Created by LpMind, these allow additional features to be added to BetterTeams
separate to the core plugin. Some existing features such as LuckPerms contexts, Discord integration and zKoth
integration have already been moved to this new system, see the features section of the changelog for more info.

The minimum supported version of minecraft after this release is 1.21, support for all previous versions has been
dropped. This was a difficult decision to make, but as less than 4% of BetterTeams users use older versions the decision
was made to significantly simplify the development and testing process. Along with this change, the minimum supported
java version has been bumped to java 21. This is the expected java version for 1.21 regardless, so this should not have
an impact.

### Features

* Added an Extensions system to BetterTeams where individual extensions are now packaged in their own file like
  Extensions - Credit lpMind
    * This includes a comprehensive new section of
      the [wiki dedicated to browsing extensions](https://betterteams.booksaw.dev/extensions)
* Added a `noTeleport` config option, which when enabled will display coordinates in chat rather than teleporting the
  player [#662](https://github.com/booksaw/BetterTeams/issues/662)
* Added Simplified Chinese - Credit Minecraft0122 (#907)

### Improvements

* Added %betterteams_moneyshort% placeholder for money formatted
  50.0k ([#590](https://github.com/booksaw/BetterTeams/issues/590))
* Balance formatting changes: ([#395](https://github.com/booksaw/BetterTeams/issues/395))
    * Added a config option `usShortMoney`, which when enabled will always use shorthand balances, i.e. 50.0k
    * Added a config option `moneyDecimalPlaces` to configure how many decimal places are displayed for money
* Added some additional information to some BetterTeams messages (will not automatically update config)
* Added further warnings when disbanding a team (#901)
* Added 2 new placeholders, %betterteams_colorname% and %betterteams_anchor% - Credit Just Lime
* Added support for Offline Players in placeholders - Credit Just Lime

### Bug Fixes

* Fixed a missing usage of the SQL database connection config (#927) - Credit lpMind
* Removed a potential duplication glitch with team echests
* Fixed a problem which caused the es_mx translation file to fail to load (#913)
* Fixed permission error with /teama delwarp
* Fixed a possible NullPointerException with the getOfflinePlayer method (#993)
* Fixed an issue where players team names were not being updated in some cases in the TAB menu (#902)

### API Changes

* Refactored the team levels codebase so Levels are wrapped in an object - Credit lpMind
* Added a way for the API to access and update a teams banned list - Credit Just Lime
* Removed Deprecated methods and classes from the API
    * Removed PrePurgeEvent,
      use [PurgeEvent](https://betterteams.booksaw.dev/apidocs/com/booksaw/betterTeams/customEvents/PurgeEvent.html)
      instead
    * Removed TeamPreMessageEvent,
      replace usages
      with [TeamSendMessageEvent](https://betterteams.booksaw.dev/apidocs/com/booksaw/betterTeams/customEvents/TeamSendMessageEvent.html)
    * Removed TeamMessageEvent,
      replace usages
      with [PostTeamSendMessageEvent](https://betterteams.booksaw.dev/apidocs/com/booksaw/betterTeams/customEvents/post/PostTeamSendMessageEvent.html)
    * Removed Team.getTeamList() as it no longer provides accurate results for all storage classes
    * Removed Team.getClamingTeam() due to the typo, please
      replace usages
      with [Team.getClaimingTeam()](https://betterteams.booksaw.dev/apidocs/com/booksaw/betterTeams/Team.html#getClaimingTeam(org.bukkit.Location))
    * Removed Team.removeAlly(UUID),
      replace usages
      with [Team.becomeNeutral(UUID, boolean)](https://betterteams.booksaw.dev/apidocs/com/booksaw/betterTeams/Team.html#becomeNeutral(java.util.UUID,boolean))
    * Removed Team.removeAlly(Team), replace usages
      with [Team.becomeNeutral(Team, boolean)](https://betterteams.booksaw.dev/apidocs/com/booksaw/betterTeams/Team.html#becomeNeutral(com.booksaw.betterTeams.Team,boolean))
    * Removed Team.getRequests in favour
      of [Team.getAllyRequests()](https://betterteams.booksaw.dev/apidocs/com/booksaw/betterTeams/Team.html#getAllyRequests())
    * Removed MessageManager.format(String, Object...) in favour
      of [StringUtil.setPlaceholders(String, Object...)](https://betterteams.booksaw.dev/apidocs/com/booksaw/betterTeams/util/StringUtil.html#setPlaceholders(java.lang.String,java.lang.Object...))
* Added automatically generated Javadocs
  a [new section of the BetterTeams Wiki](https://betterteams.booksaw.dev/apidocs/index.html)
