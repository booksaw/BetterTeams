---
sidebar_position: 20
---

# Registering Custom Commands

Along with providing an extensive API, BetterTeams supports adding both custom `/team` commands along with `/teama` admin commands. 
This can allow you to integrate your plugin closely with the BetterTeams API, so users can use a single command to interact
both with BetterTeams, and your extension or plugin.

## Extending the SubCommand interface

All commands used within the BetterTeams ecosystem are required to implement/extend one of the following interfaces or classes:

* [implements SubCommand](https://betterteams.booksaw.dev/apidocs/com/booksaw/betterTeams/commands/SubCommand.html) - This is the 
  generic interface to manage a command, its TAB completion, permissions and help message
* [extends TeamSubCommand](https://betterteams.booksaw.dev/apidocs/com/booksaw/betterTeams/commands/presets/TeamSubCommand.html) -
  If your command targets the current players team this can be used to automatically get this information for you. An 
  example of where this is used is the [/team name](https://github.com/booksaw/BetterTeams/blob/master/BetterTeams/src/main/java/com/booksaw/betterTeams/commands/team/NameCommand.java)
  command
* [extends TeamSelectSubCommand](https://betterteams.booksaw.dev/apidocs/com/booksaw/betterTeams/commands/presets/TeamSelectSubCommand.html) -
  This is the admin equivalent of `TeamSubCommand` for when admins need to target a team with their command, such as 
  [/teama name \<team\>](https://github.com/booksaw/BetterTeams/blob/master/BetterTeams/src/main/java/com/booksaw/betterTeams/commands/teama/NameTeama.java)

Please read the JavaDocs linked above for information about the fields to populate.

The [CommandResponse](https://betterteams.booksaw.dev/apidocs/com/booksaw/betterTeams/CommandResponse.html) class is used to determine
if the command was successful, and what message to return to the user. This is done as BetterTeams (including any custom commands) can be configured 
to have a [command cost or cooldown](../04-configuration/Command-Cost-and-Cooldown.md). These functions are only activated
when the command is successful, this is done using the method `new CommandResponse(true, Message mesasge)` or in the case
the command failed, instead return `new CommandResponse(false, Message)`

:::danger
It is not recommended to use the constructor signature `CommandResponse(boolean, String)` as this is a message reference 
in the BetterTeams messages.yml file, not your plugin / extensions configuration. 
:::

To create a response message, use the message types in [the messages package](https://betterteams.booksaw.dev/apidocs/com/booksaw/betterTeams/message/package-summary.html).
This is subject to change to improve the extensibility of BetterTeams as the message API is a little cumbersome. 


## Adding custom /team command

After you have created a class following the information above, to add a `/team` command use the following line:

```java
import com.booksaw.betterTeams.Main;

public void onEnable() {
    Main.plugin.getTeamCommand().addSubCommand(new YourCommandHere());
}
```

## Adding custom /teama command

After you have created a class following the information above, to add a `/teama` command use the following line:

```java
import com.booksaw.betterTeams.Main;

public void onEnable() {
    Main.plugin.getTeamaCommand().addSubCommand(new YourAdminCommandHere());
}
```

## Re-registering the command after a reload of BetterTeams

:::note
This section is not required by [Custom Extensions](Extensions-Intro.md), it is only required while using the API within a full plugin. 
:::

When BetterTeams is reloaded, all registered commands for both `/team` and `/teama` are refreshed. When this happens
any custom commands will need to be re-registered (when writing an extension, this is done automatically).

```java
import com.booksaw.betterTeams.customEvents.post.PostBetterTeamsReloadEvent;

@EventHandler
public void onBetterTeamsReload(PostBetterTeamsReloadEvent event) {
  // Re-register your plugin's commands
}
```
