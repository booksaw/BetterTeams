---
sidebar_position: 7
---


# Score

Players are awarded score for kills (1 score for 1 kill), they lose score for dying and can be given score through
plugin integrations, both direct and indirect.

## Direct integrations

* [Zkoth](https://www.spigotmc.org/resources/zkoth-king-of-the-hill.76749/)

Check the [dependencies section](./dependencies/Dependencies#zkoth) of the wiki for more details.

## Indirect Integrations

Any plugin which lets you run a command can be used to give a team score using the inbuilt admin command set:

* `/teama score <set/add/remove> <player/team>`

## Configuration

This is where you configure the number of points gained and lost through performing certain actions.
The spam values are used to stop things like spawn camping which can often be considered bad sportsmanship. They allow
score increases to be limited so players cannot be targeted by a team aiming to increase their team score. If spam
reduction does not suit your server you can always disable it by setting `spamThreashold` to 0

This is an exert of config.yml.

```yaml
# All settings depending on the score aspect of this plugin
# For integrations of other plugins with score see the bottom section of the config


# This is a list of commands which are run by the console before a purge occurs
# A purge is where all teams scores are reset to 0
# If this is left blank, no commands will be run before a purge occurs
# You can use placeholders from placeholder API, though the player is set to be null (so things like %betterteams_team% will not work) 
#    the main reason for the placeholders is so you can use %betterteams_score_{rank}% to refrence a player on the leaderboard
#
# Possible values: [Any command valid on your server]
purgeCommands:
- 'give @a minecraft:dirt 1'

# This is used to track when the next purge should occur. This is a list of dates where a purge will be run 
# If this list is left blank, no purges will occur
# these dates must be in order of ealiest to latest
#
# Possible values: [{dateOfMonth}:{Hour of day}]
autoPurge:
#- '1:6'
# This will purge the users score at 6am on the first of every month

# The amount of time after an event occurs that repeat events are classed as spam
# This is in seconds
# Recognising spam is useful as it means players cannot spam kill 1 person to get score, this means you can give spam kills reduced score 
# - The spam kill tracker is unique to every event, so dying will not activate the spam timer on kills
#
# Possible values: [Any whole number, 0 to disable]
spamThreshold: 60

# MAKE SURE YOU READ SPAM THRESHOLD COMMENTS ABOVE
# If something happens on your server you want to influence score is not listed below, make a request for it here: https://github.com/booksaw/BetterTeams/issues/new/choose

# Score changes for when a player dies
events:
   death:
      score: 0
      spam: -1

# Score changes for when a player kills another player
   kill:
      score: 1
      spam: 0

# The minimum score a team can have
#
# Possible values: [Any whole number]
minScore: 0
```
