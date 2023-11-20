# API 

With this project, most changes made by booksaw are pushed immediately to the master branch, if you want to view the code at the time of a specific release, check the [releases page](https://github.com/booksaw/BetterTeams/releases/)

If you release a plugin that utilises the BetterTeams API, let me know for it to be added to the wiki. To contact me either [join the discord](https://discord.gg/JF9DNs3) or [open a blank issue](https://github.com/booksaw/BetterTeams/issues/new/choose).

## Setting up your workspace

You can either add this plugin as a build path to your project or add it via maven: 

```xml
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
```
```xml
    <dependency>
       <groupId>com.github.booksaw</groupId>
       <artifactId>BetterTeams</artifactId>
       <version>VERSION</version>
       <scope>provided</scope>
    </dependency>
```

## Adding the dependency
In your plugin.yml add: `softDepend: [BetterTeams]`

Or if your plugin is reliant on betterTeams to work as expected: `depend: [BetterTeams]`

## Basic Methods
As teams are the fundamental system which this plugin is based off. Most of the time the first thing you will need to do is get the Team that you need, this can be done using several values:
* With a player who is in a team: `Team.getTeam(Player player)` This static method will return the team that the player is in, or null if they are not in a team. 
* With a team name: `Team.getTeam(String teamName)` This static method will return the team that has that name. like Minecraft player names, this should only be used when allowing a player to run a command, this is because team names can be changed.
* With the team UUID: `Team.getTeam(UUID teamUUID)` This is the recommended method when storing a reference to a team, this is because it will remain constant for the entire existence of a team.  
Once you have a team view the [Team class](https://songoda.com/marketplace/product/ultimateclaims-the-ultimate-claiming-plugin.65) to see what methods are available. 

[TeamPlayers:](https://github.com/booksaw/BetterTeams/blob/master/src/main/java/com/booksaw/betterTeams/TeamPlayer.java) are players which are in a team, In short this class is used as a wrapper to store all information about a player relating to their status with their current team (ie rank). these should only be used to get values and change their ranks, read the javadocs carefully as some methods should not be used. 
## Events 
The plugin contains a few events which are supported within the Spigot event system, this means that you just need to register the event like any other spigot event. [The events for this plugin are found here](https://github.com/booksaw/BetterTeams/tree/master/src/main/java/com/booksaw/betterTeams/customEvents)
