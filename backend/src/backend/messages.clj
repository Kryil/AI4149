(ns backend.messages)

(defrecord Coordinates [x y])

(defrecord UnitState
  [id
   ^Coordinates position
   ^String action
   ^Coordinates action-coordinates])

(defrecord BuildingState
  [id
   ^String action])

(defrecord Thing
  [id
   ^String thing-type
   ^Coordinates position])

(defrecord GameState
  [^Integer turn
   ^Integer turns
   ^"[Lbackend.messages.UnitState;" unit-states
   ^"[Lbackend.messages.BuildingState;" building-states
   ^"[Lbackend.messages.Thing;" things])

(defrecord PlayerCommand
  [target-id
   action
   action-args])

