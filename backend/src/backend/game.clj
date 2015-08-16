(ns backend.game
  (:require [backend.messages :as msgs]))

(defn process-turn 
  "Apply all player actions into given state and return updated state."
  [state player-actions]
  (update-in state [:turn] inc))
