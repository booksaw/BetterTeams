All `/team` commands can have a cost or cooldown associated with them, below is described how each works and how to
configure it.

Whenever this page talks about the config file, it is referencing
the [team.yml file](https://github.com/booksaw/BetterTeams/blob/master/team.yml) which will be generated in the folder
`/plugins/BetterTeams/` when betterteams is first loaded.

### Command Cooldowns

Command cooldowns require a wait between specific commands. For example, you can limit how regularly a player can
teleport to their team home.
This is useful for stopping players from abusing features or to try and add more of a weight to command so players think
twice before running it. This could also be used along with the Command Costs to stop players from spamming commands
which cost the team money.

The permission node to bypass all cooldowns set is `betterTeams.cooldown.bypass`

Config section about command cooldowns:

```YAML
# COOLDOWNS - Only let a player run a command every set number of seconds. 
# To bypass the cooldown have the permission node betterTeams.cooldown.bypass
# To add a cooldown to this file, an example is shown below
cooldowns: 
- 'home:60'
## The line above will add a 60-second cooldown to the command /team home
```

It is important when adding cooldowns to surround the entire line in `''` else there can be problems loading the
details. Any `/team` cooldown command

### Command Cost

Command cost means that when a player runs a /team command they are charged for it, this can be used to discourage using
`/team sethome` to tp players together and try and encourage a permanent home for the team.
It is important to note that if the command fails (ie for `/team home` there is not a home set for that team), the cost
of the command will not be charged.
Along with command cost, it is configurable if the price is taken from the team balance before the players' balance.
This is useful as it provides another motivation to use the team balance feature. It allows the richer members of a team
to support the poorer members.

* If the team balance is not enough to run the command, any excess will be charged from the specific player's balance.

This feature requires [Vault](https://www.spigotmc.org/resources/vault.34315/) to be installed.
The permission node to bypass all cooldowns set is `betterTeams.cost.bypass`

The section of the configuration file relating to command cost

```YAML
# COSTS - Make take a certain amount of money from the players balance when they run a command
# To bypass the cost have the permission node betterteams.cost.bypass
costs:
- 'home:9.99'

# This option determines if the command cost is taken from the team balance first or the individual player
#
# Possible values: [true, false]
costFromTeam: true
```

Just like command cooldowns, it is important when adding cooldowns to surround the entire line in `''` else there can be
problems loading the details. Any `/team` cooldown command
