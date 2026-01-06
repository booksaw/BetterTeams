---
title: "LuckPerms Extension"
description: "Grant permissions dynamically based on team rank, level, and status using LuckPerms contexts."
author: "LpMind"
image: "https://luckperms.net/logo.png"
downloadUrl: "https://github.com/booksaw/BetterTeams/releases"
sourceUrl: "https://github.com/booksaw/BetterTeams/tree/master/extensions/luckperms-extension"
tags: ["Official", "Permissions", "Contexts"]
---

# LuckPerms Extension

This extension integrates **BetterTeams** with **[LuckPerms](https://luckperms.net/)** to provide dynamic **contexts**. This allows you to grant permissions to players based on their live team data, such as their rank, team level, or PVP status.

## Features
- **Dynamic Contexts**: Filter permissions based on team properties.
- **Rank Integration**: Give specific permissions to Team Owners or Admins automatically.
- **Level Integration**: Unlock permissions as a team levels up.

---

## Available Contexts

When installed, this extension provides the following contexts (all keys start with `bt_`):

| Context Key          | Description                                      | Example Value(s)            |
|:---------------------|:-------------------------------------------------|:----------------------------|
| `bt_inteam`          | Checks if a player is in any team.               | `true`, `false`             |
| `bt_pvp`             | Checks if the team has friendly-fire enabled.    | `true`, `false`             |
| `bt_open`            | Checks if the team is open for anyone to join.   | `true`, `false`             |
| `bt_hashome`         | Checks if the team has set a home.               | `true`, `false`             |
| `bt_rank`            | The player's rank within their team.             | `owner`, `admin`, `default` |
| `bt_teamchat`        | Current chat mode the player is using.           | `Team Chat`                 |
| `bt_level`           | The current level of the player's team.          | `1`, `2`, `3`, ...          |
| `bt_positionscore`   | The team's rank on the score leaderboard.        | `1`, `2`, `3`, …            |
| `bt_positionbal`     | The team's rank on the balance leaderboard.      | `1`, `2`, `3`, …            |
| `bt_positionmembers` | The team's rank on the member-count leaderboard. | `1`, `2`, `3`, …            |

---

## Usage Examples

### Team Membership Permission

To give the `kits.member` permission only if the player is in any team:

```bash
/lp group default permission set kits.member true bt_inteam=true
````

:::note
Some context values are configurable in your language files. The values are sourced from the following placeholder keys:

- **`bt_rank`**: `placeholder.owner`, `placeholder.admin`, `placeholder.default`
- **`bt_teamchat`**: `placeholder.teamChat`, `placeholder.allyChat`, `placeholder.globalChat`

If you customize these placeholder values, the contexts will use your new values.
:::