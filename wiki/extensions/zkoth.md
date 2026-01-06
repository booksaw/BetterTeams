---
title: "ZKoth Extension"
description: "Award team score automatically when players win ZKoth King of the Hill events."
author: "Community"
image: "https://www.spigotmc.org/data/resource_icons/76/76749.jpg"
downloadUrl: "https://github.com/booksaw/BetterTeams/releases"
sourceUrl: "https://github.com/booksaw/BetterTeams/tree/master/extensions/zkoth-extension"
tags: ["Official", "Score", "KOTH"]
---

# ZKoth Extension

:::warning
Support for these plugins is either added by the authors of these plugins or community members, they are not tested by the lead developer of BetterTeams so purchase at your own risk!
:::

This extension provides a direct integration between **BetterTeams** and **[zKoth - King of the Hill](https://www.spigotmc.org/resources/zkoth-king-of-the-hill.76749/)**.

It allows teams to compete for **Team Score** by capturing King of the Hill points. When a player captures a KOTH, their team is automatically awarded a configurable amount of score.

## Features
- **Score Rewards**: Automatically give `score` to a team when they win a KOTH.
- **Configurable Points**: Set exactly how many points a capture is worth.

---

## Configuration

You can configure the points awarded in the `config.yml` of the extension (located in `/plugins/BetterTeams/extensions/zkoth/`):

```yaml
# The amount of score to give to the team that wins the KOTH
pointsPerCapture: 10
````

:::info
Ensure you have both **BetterTeams** and **zKoth** installed on your server for this extension to function correctly.
:::