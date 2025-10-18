# Changelog

All notable changes to this project will be documented in this file against their release version.

The newest release will always be maintained at the top of the file.

## 5.0.0

This is a major release which includes several breaking changes for the API

### Features

### Improvements

### Bug Fixes

### API Changes

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
