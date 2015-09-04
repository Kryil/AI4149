(ns ai4149.engine.game
  (:require [ai4149.engine.factory :refer :all]
            [ai4149.engine.unit :refer :all]))

(defn increase-turn-counter [state]
  (update-in state [:turn] inc))


(defn process-turn 
  "Apply all player actions into given state and return updated state."
  [state player-commands]
  (->
    state
    (process-units)
    (process-factories)
    (process-build-commands (filter (fn [cmd] (= (:action cmd) :build)) player-commands))
    (process-move-commands (filter (fn [cmd] (= (:action cmd) :move)) player-commands))
    increase-turn-counter))
