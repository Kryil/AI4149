# Game Mechanics

## Units

### Commander

Commander represents the player. The game is won when the opposing Commander
is destroyed and lost when your Commander gets destroyed.

Commander can build new buildings and has a weapon. Commander has heavier armor
than other units, meaning it will take less damage from a hit.

Commander moves a bit slower than other units, X points per tick.

### Tank

The tank is the only battle unit. It can move and fire its weapon. Tank speed
is X points per tick.

Tanks can be built at unit factory. It will cost X resources and Y ticks to
complete.

## Buildings

### Unit Factories

Factories build new units on the adjacent slots of the factory. The unit becomes
visible immediately when constructing starts but is not usable until
construction is completed.

## Weapons

In the minimum viable product every unit has the same weapon. Firing one bullet
takes one game tick and the bullet moves X distance in a tick. The bullet is a
lot faster than any other unit but still not instant, so the player must try to
analyze movement of the target in order to hit.
