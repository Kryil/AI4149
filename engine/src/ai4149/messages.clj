(ns ai4149.messages)

(defrecord Coordinates [x y])

(defrecord UnitState
  [id
   type
   ^Coordinates position
   action
   ^Coordinates action-coordinates])

(defrecord BuildingState
  [id
   type
   ^Coordinates position
   ^String action
   action-args])

(defrecord Thing
  [id
   ^String thing-type
   ^Coordinates position])

(defrecord GameState
  [^Integer turn
   ^Integer turns
   resources
   ^"[Lai4149.messages.UnitState;" unit-states
   ^"[Lai4149.messages.BuildingState;" building-states
   ^"[Lai4149.messages.Thing;" things])

(defrecord PlayerCommand
  [player
   target-id
   action
   action-args])

(defrecord PlayerState
  [player
   resources
   ^"[Lai4149.messages.UnitState;" unit-states
   ^"[Lai4149.messages.BuildingState;" building-states
   errors])

(defrecord UnitRule
  [name
   type
   speed
   armor
   cost
   build-time
   built-by
   shape])
   

(defrecord FullGameState
  [^Integer turn
   ^Integer turns
   rules
   ; todo map
   ^"[Lai4149.messages.PlayerState;" player-states])
