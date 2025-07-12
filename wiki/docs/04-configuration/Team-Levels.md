# Team Levels

## What are Levels?

All teams have a level associated with them, this level dictates the permissions for the team. Per level permissions
include:

* Maximum number of players in their team
* Maximum number of chests allowed to be claimed

Teams can level up either with team score, or with their team balance. This can be determined in the config.

## Configuration

All set up for levels is available in the config

_the config exert:_

```YAML
levels:
   l1:
      # This is used set a maximum size for a team.
      # - If this feature is unwanted, set the teamLimit to -1 and there will be unlimited places in each team
      # - Keep this value reasonable, as Java cannot cope with larger numbers (over 2 billion), so if you want the team to be limitless, set the value to -1 instead of something large
      #
      # Possible values: [-1, any positive whole number]
      teamLimit: 10
      # This is used to determine the maximum number of chests that a team can claim
      # - If this is set to 0 teams will not be allowed any claims (though it is recommended you do that through permissions instead of the config option)
      # - If this is set to -1 there will be no limit on the number of chests
      #
      # Possible values: [Any positive whole number, 0, -1]
      maxChests: 2
      # This is used to determine the maximum number of warps that a team can set
      # - If this is set to 0 teams will not be allowed any warps (though it is recommended you do that through permissions instead of the config option)
      # - If this is set to -1 there will be no limit on the number of warps
      #
      # Possible values: [Any positive whole number, 0, -1]
      maxWarps: 2
      # Used to limit the balance the team can have in their team bank
      # - If this is set to -1 there will be no limit on team balance
      # - If this is set to 0, teams of that level will not have a team bank (to completely disable use teampermissions.yml)
      #
      # Possible values [-1, 0, any positive number]
      maxBal: -1
      # The maximum number of admins a team is allowed while is has this rank
      # - If this is set to -1 there will not be a limit
      #
      # Possible values [-1, any positive number]
      maxAdmins: 5
      # The maximum number of owners a team is allowed at this rank
      # - If singleOwner is set to true this value will be ignored
      #
      # Possible values [any positive whole number]
      maxOwners: 2
      
      # This is a list of commands that are run when a team
      # Any player which contains %player% will be run for every player on the team
      # Placeholders:
      # %team% is replaced with the team name
      # %player% is replaced with all players in the team
      # %level% is replaced with the level that the team is becomming
      endCommands:
      # - say %player% is no longer level 1

      # This message is included in /team rank command to give more details about the rank
      # this message, for example could include the perks of this team rank along with the perks of the next rank
      rankLore:
      - '&7The first level'
   l2:
      # Price is determined for all levels aside level 1,
      # - If this value ends with 's' it will take score from the team
      # - If this value ends with 'm' it will take money from the team balance
      price: 100s
      teamLimit: 20
      maxChests: 2
      maxWarps: 2
      maxBal: -1
      maxAdmins: 5
      maxOwners: 1
      rankLore:
      - '&7The second level'
```

note at the start of each individual level number there is an l (This is caused by YAML formatting rules)

### A visual representation

As the config above is hard to understand, below is a visual representation of what it is signifying

| Level | Price     | teamLimit | maxChests | maxWarps |
|-------|-----------|-----------|-----------|----------|
| l1    | Free      | 10        | 2         | 2        |
| l2    | 100 score | 20        | 2         | 2        |

Notice how the max chests are declared for both the first level and the second level thought it has not changed. This is
currently required for the plugin to work, else the maxChests will be assumed to be 0.

### Setting the price

It is also important to determine the price for each subsequent level after the first level. As if the price is not
correctly defined the rank will not exist.
The price of a rank can either be determined in score:

```YAML
price: 100s
```

Or in money (this will be taken out of the team balance)

```YAML
price: 100m
```

A level cannot have both a score and a monetary cost, it can only have one or the other.

### Adding new levels

Adding new levels is as simple as copying the template for the previous level and increasing the level number by 1. An
example of level 3 is shown below:

```YAML
   l3:
      # Price is determined for all levels aside level 1,
      # - If this value ends with 's' it will take score from the team
      # - If this value ends with 'm' it will take money from the team balance
      price: 500s
      teamLimit: 25
      maxChests: 5
      maxWarps: 5
```
