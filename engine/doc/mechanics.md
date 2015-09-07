# Game Mechanics

## Units

Unit locations are indexed by their center point.

TODO fix image.
![unitIndex]

### Commander

Commander represents the player. The game is won when the opposing Commander
is destroyed and lost when your Commander gets destroyed.

Commander can build and repair buildings and repair units, harvest resources 
and has a weapon. Commander has heavier armor than other units, meaning it 
will take less damage from a hit.

Commander moves a bit slower than other units, X points per tick.

Commander needs to stand still while building.

### Tank

The tank is currently the only battle unit. It can move and fire its weapon.
Tank speed is X points per tick.

Tanks can be built at unit factory. It will cost X resources and Y ticks to
complete.

### Harvester / Lieutenant Commander

Lt. Commanders can harvest new resources and stop to process it to be used by
factories. They can also build and repair buildings and repair units, just
like the commander, but they can not act as a commander in the event the
commander is destroyed. They do not have a weapon.

Lt. Commanders can be built at unit factory. It will cost X resources and Y 
ticks to complete.

## Buildings

### Unit Factories

Factories build new units on the adjacent slots of the factory, starting from
smallest index. The unit becomes visible immediately when constructing starts
but is not usable until construction is completed.

![factoryBuilding1]

Unit position needs to be calculated by unit size if/when we have new units.

Indexincould also start from top left corner like with units.

![factoryBuilding2]

TODO: Allow users to select the building slot by index themselves, to avoid
building on a wrong side of a wall in a situation like above.

## Weapons

In the minimum viable product every unit has the same weapon. Firing one bullet
takes one game tick and the bullet moves X distance in a tick. The bullet is a
lot faster than any other unit but still not instant, so the player must try to
analyze movement of the target in order to hit.

## Game Play

Game is turn based. Every turn takes a fixed amount of time when every 
participating player has the opportunity to analyze the current state and plot
the next moves for their units. The server then plays the turn for each player
simultaneously. For example when a player is firing weapons to a tank that was
stationary in the previous turn, it may not hit because the tank may have begun
moving.

### Commands

Players can send any number of commands during for every turn, but only last
command per unit or building is taken into action. Possible commands for
buildings are limited to one: `build`. The `build` command instructs a factory
to produce a new unit, determined by the command argument. Resources are
subtracted immediately and building of the unit starts.

Units can take five different commands: `move`, `fire`, `gather`, `build` and
`repair`.

The `move` command takes a list of coordinates as the argument and it
instructs unit to move along the given path. The unit state is changed to 
`moving` and the unit moves towards the next coordinate in the list, until it
reaches it or the path is obstructed. The state is changed to `idle` or 
`obstructed`, respectively. 

The `fire` command requires the weapon id and 
coordinates where to shoot. 

The `gather` command can be given to the commander
and harvester and it is executed when the unit is over a resource. 

Finally `build` and `repair` commands can be given to commander and harvester
and they instruct the unit either to construct a new building or repair the
targeted building or unit.

Every command cancels all previous ones. For example a moving unit will stop
when it is instructed to fire and commander will stop repairing when it is
ordered to build. An exception to this is the factory, which will not accept
a new command before previous one has been fully executed.

### Scores

The game score is always based on the player value, at least in two different
ways:

 1. Sum of collected unspend resources and total build cost of all units in
    the field (if the unit is destroyed, its value will be lost)
 2. Value of collected unspend resources

The scoring model is announced in the game start.

### Game End

The game will end after a fixed number of turns or when either one of the
commanders is destroyed. The winner will be the player who still has his
commander alive or the one with higher score based on the scoring rules.


[unitIndex]: unit_indexin.png
[factoryBuilding1]: factory_building1.png
[factoryBuilding2]: factory_building2.png
