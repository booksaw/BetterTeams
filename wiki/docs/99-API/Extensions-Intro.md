---
sidebar_position: 2
---

# BetterTeams Extensions

BetterTeams offers an extensions capability for developers who want to add functionality to BetterTeams without needing to create a full plugin. 

Creating an extension comes with many advantages, such as automatically-updating configuration files, reload support 
and [languages support](./extensions-mesages.md).

BetterTeams provides an [template extension](https://github.com/booksaw/BetterTeams-Template-Extension) which can act as 
a foundation for extensions. Additionally, BetterTeams has several [Official Extensions](https://github.com/booksaw/BetterTeams/tree/master/extensions) 
which can be used for reference and inspiration.

The BetterTeams wiki also includes an [extension browser](/extensions), information on how to add your extension can be 
found below.

## Setting up your workspace

Configure your maven just like if you were using the API in an external plugin: 
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

## Create your extensions.yml

Like spigots [plugin.yml](https://www.spigotmc.org/wiki/plugin-yml/), the extension API requires an extension.yml file 
with metadata about the extension. See the possible options below (this is taken from the template extension)
```yaml
# [Mandatory] The unique name of the extension
name: MyFirstExtension

# [Mandatory] The fully qualified path to your main class
main: com.example.betterteams.MyFirstExtension

# [Optional] The version of the extension (uses Maven filtering)
version: ${project.version}

# [Optional] The author of the extension
author: ADeveloper

# [Optional] A short description of what the extension does
description: An example extension that demonstrates the BetterTeams API.

# [Optional] The website for the extension (e.g., GitHub link)
website: https://github.com/booksaw/BetterTeams

# [Optional] List of other BetterTeams EXTENSIONS required to run
depend: []

# [Optional] List of other BetterTeams EXTENSIONS that are optional
softdepend: []

# [Optional] List of Bukkit PLUGINS required to run (e.g., Vault, WorldGuard)
plugin-depend: []

# [Optional] List of Bukkit PLUGINS that are optional
plugin-softdepend: []
```

## Main Class

Each Extension must include a class (referenced in extension.yml) which extends [`BetterTeamsExtension`](https://betterteams.booksaw.dev/apidocs/com/booksaw/betterTeams/extension/BetterTeamsExtension.html). This class is the 
entrypoint of the plugin and is responsible for registering any required events or commands.

The most important methods likely will be the `onEnable` method which is called when the extension is enabled, and `onDisable` 
which is called when the plugin is disabled. These methods are also called when BetterTeams is reloaded to ensure automatic 
consistency with BetterTeams.

```yaml
@Override
public void onEnable() {

}

@Override
public void onDisable() {

}
```

## Adding your extension to the wiki

Once you have created and published an extension, you can add the extension to the [BetterTeams extension browser](/extensions). 
Simply create a PR to add a new Markdown file to the [Wiki Extensions](https://github.com/booksaw/BetterTeams/tree/master/wiki/extensions)
directory. This page includes a Markdown table at the top of the page which is used to populate sidebar with information. 