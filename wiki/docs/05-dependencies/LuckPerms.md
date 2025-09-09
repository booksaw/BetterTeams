# LuckPerms Integration

BetterTeams can hook into the [LuckPerms](https://luckperms.net/) permission plugin to provide dynamic **contexts**.  
This is an extremely powerful feature that allows you to grant permissions to players based on their live team data.

---

## What are Contexts?

Contexts are extra conditions that must be met for a permission to apply.  
For example, you could create a permission that only applies if a player is the `owner` of their team, or if their team's `level` is 5.

---

## Available Contexts

BetterTeams provides the following contexts (all keys start with `bt_`):

| Context Key                   | Description                                                                 | Example Value(s)                         |
|-------------------------------|-----------------------------------------------------------------------------|------------------------------------------|
| `bt_inteam`                   | Checks if a player is in any team.                                          | `true`, `false`                          |
| `bt_rank`                     | The player's rank within their team.                                        | `owner`, `admin`, `default`              |
| `bt_teamchat`                 | Current chat mode the player is using.                                      | `Team Chat`, `Ally Chat`, `Global Chat`  |
| `bt_level`                    | The current level of the player's team.                                     | `5`                                      |
| `bt_pvp`                      | Checks if the team has friendly-fire enabled.                               | `true`, `false`                          |
| `bt_open`                     | Checks if the team is open for anyone to join.                              | `true`, `false`                          |
| `bt_hashome`                  | Checks if the team has set a home.                                          | `true`, `false`                          |
| `bt_positionscore`            | The team's rank on the score leaderboard.                                   | `1`, `2`, `3`, …                         |
| `bt_positionbal`              | The team's rank on the balance leaderboard.                                 | `1`, `2`, `3`, …                         |
| `bt_positionmembers`          | The team's rank on the member-count leaderboard.                            | `1`, `2`, `3`, …                         |

---

## Usage Examples

### Team Membership Permission

To give the `kits.member` permission only if the player is in any team:

```bash
/lp group default permission set kits.member true bt_inteam=true
```
:::note
The values for the `bt_teamchat` context are determined by the return values of chat placeholders in your `messages.yml` file. By default, these are:
- `placeholder.teamChat`: 'Team Chat'
- `placeholder.globalChat`: 'Global Chat'
- `placeholder.allyChat`: 'Ally Chat'

If you customize these placeholder values, the `bt_teamchat` context will use your new values.
:::
