(ns backend.messages)

(defrecord Coordinates [x y])

(defrecord UnitState
  [id
   type
   ^Coordinates position
   ^String action
   ^Coordinates action-coordinates])

(defrecord BuildingState
  [id
   type
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
   ^"[Lbackend.messages.UnitState;" unit-states
   ^"[Lbackend.messages.BuildingState;" building-states
   ^"[Lbackend.messages.Thing;" things])

(defrecord PlayerCommand
  [player
   target-id
   action
   action-args])

(defrecord PlayerState
  [player
   resources
   ^"[Lbackend.messages.UnitState;" unit-states
   ^"[Lbackend.messages.BuildingState;" building-states])

(defrecord UnitRule
  [name
   type
   speed
   armor
   cost])
   

(defrecord FullGameState
  [^Integer turn
   ^Integer turns
   rules
   ; todo map
   ^"[Lbackend.messages.PlayerState;" player-states])

