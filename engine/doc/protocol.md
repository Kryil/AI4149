# Game Communication Protocol

## Messages

### Player starts a new game

 - Sent by the player
   - request for new game with player name
 - Sent by the server
   - game id

### Player joins the Game

 - Sent by the player
   - game id
   - player name
 - Sent by the server
   - Game Started message to both players

### Game Started (sent by the server)

 - Current player name
 - Opponent name
 - Map size
 - Player HQ position on the Map
 - Building and unit costs
 - Building and unit field of vision (as in how far units see)
 - Unit moving speed, fire rate and bullet speed
 - Positions of player owned buildings and units
 - Scoring rule (how is the score calculated, see [Mechanics](mechanics.md))
 - Total number of turns in the game

### Game Status (sent by the server)

  - Turn number
  - Turns remaining
  - Available resources
  - Player unit Status
    - Position
    - Current state
      - moving to coordinates
      - firing at coordinates
      - gathering resources
      - idle
      - under construction
      - new (on the tick when the unit becomes ready, idle on next tick)
  - Player building status
    - building unit
    - constructing a new building
    - idle
    - new (same as with units)
  - Things in the player field of vision
    - Walls
    - Resources
    - Enemy units
  - List of unprocessable commands?
    - Build site obstructed?
    - Not enough resources to build?
    - Moving not possible?

### Player initiated commands (sent by players)

  - Build a new building at given coordinates
  - Build a new unit from given factory
  - Move unit to coordinates
  - Order unit to fire to given coordinates
  - Gather resources with the unit at its current position

Everything is a list, meaning player can instruct units to move in a path, fire
to multiple different targets, build multiple units and construct multiple
buildings. The server will cache the commands and process them one by one.

### Ping (sent by the server and client)

If the player has not sent anything in some amount of turns, the server will
ask for a ping. The game is paused (see Error conditions) and the player is
given some time to reply to the ping. If there is no reply, the game is
terminated.

The client must respond with a ping before the grace period is over.

### Game End (sent by the server)

  - You have won/lost

### Error conditions

  - Client disconnected
    - Game paused, give other player a few seconds to connect
    - Game End if player does not reconnect on grace period
  - Server error
    - Game over, sorry

## Future Ideas

### Game Started

 - Game Speed (i.e. how many milliseconds per tick)
 - Player and Opponent Factions (if we decide to implement more than one)

### Player commands

  - Select where the factory builds the new unit
