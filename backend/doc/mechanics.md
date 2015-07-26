# Game Mechanics

## Units

Unit locations are indexed by their top left corner.

![index position of 2x2 unit][unitIndex]

### Commander

Commander represents the player. The game is won when the opposing Commander
is destroyed and lost when your Commander gets destroyed.

Commander can build new buildings, harvest energy and has a weapon. Commander
has heavier armor than other units, meaning it will take less damage from a hit.

Commander moves a bit slower than other units, X points per tick.

Commander needs to stand still while building.

### Tank

The tank is currently the only battle unit. It can move and fire its weapon.
Tank speed is X points per tick.

Tanks can be built at unit factory. It will cost X resources and Y ticks to
complete.

### Harvester

Harvester can harvest new energy and stop to process it to be used by factories.

Harvesters can be built at unit factory. It will cost X resources and Y ticks to
complete.

## Buildings

### Unit Factories

Factories build new units on the adjacent slots of the factory, starting from
smallest index. The unit becomes visible immediately when constructing starts
but is not usable until construction is completed.

![Building slots][factoryBuilding1]

Unit position needs to be calculated by unit size if/when we have new units.

Indexincould also start from top left corner like with units.

![Building slots][factoryBuilding2]

TODO: Allow users to select the building slot by index themselves, to avoid
building on a wrong side of a wall in a situation like above.

## Weapons

In the minimum viable product every unit has the same weapon. Firing one bullet
takes one game tick and the bullet moves X distance in a tick. The bullet is a
lot faster than any other unit but still not instant, so the player must try to
analyze movement of the target in order to hit.

[unitIndex]: https://github.com/Kryil/AI4149/tree/master/backend/doc/unit_indexin.png
[factoryBuilding1]: https://github.com/Kryil/AI4149/tree/master/backend/doc/factory_building1.png
[factoryBuilding2]: https://github.com/Kryil/AI4149/tree/master/backend/doc/factory_building2.png
