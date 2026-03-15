---
sidebar_position: 3
---

# Message Management for Extensions

The extensions system includes support for all of the BetterTeams translation management system. This includes:
* Configuration of selected language, to allow for community translations
* Automatic fallback to english if a translation is missing from other languages
* Automatically adding new messages as they become available
* Providing simple ways for Server owners to share any missing translations via Discord or GitHub


## Getting messages using the message manager system

The default `getMessages()` function is provided by the BetterTeamsExtension class, which is the main class of your 
extension. 

### Get
```java
getMessages().get("welcomePath");
getMessages().get("player.joined", "player", "Steve", "team", "Warriors");
getMessages().getWithPrefix("welcome");
```

### Direct send
```java
getMessages().send(player, "welcome");
getMessages().send(player, "player.joined", "player", "Steve", "team", "Warriors");
```

### Response (for commands)
```java
return getMessages().response(true, "command.success");
return getMessages().response(false, "error.no-permission");
return getMessages().response(true, "command.done", "player", "Steve");

 // or:
return new CommandResponse(getMessages().toStatic("command.success"));
```

### StaticMessage (for send to command sender or use in commandResponse
```java
getMessages().toStatic("welcome");
getMessages().toStatic("player.joined", "player", "Steve", "team", "Warriors");
```

### Builder ( for complex message )
```java
// replace placeholder {kills} -> 10
getMessages().builder("player.stats")
    .with("kills", 10)
    .with("deaths", 5)
    .with("ratio", 2.0)
    .send(player);

getMessages().builder("complex.message")
    .with("player", player.getName())
    .with("team", team.getName())
    .toResponse(true);
```

### Placeholder Format

Placeholders use `{key}` format in messages.yml:
```yaml
player:
  joined: "&a{player} joined team {team}!"
```

Usage: `"key", value, "key", value, ...`
```java
getMessages().send(player, "player.joined", "player", "Steve", "team", "Warriors");
```

Result: `Steve joined team Warriors!`

## Adding new messages & Translations

To add new messages, simply add the message to your extensions `messages.yml` file, in the same directory as the 
`extensions.yml` file. BetterTeams will proceed to automatically detect the changes, and add them to the users files!

To add additional translations just add a new file in your maven resources directory named after the language code (i.e. 
de for German), the same directory as the `messages.yml` file, it comes with all the same management and processing of 
the messages.yml file.