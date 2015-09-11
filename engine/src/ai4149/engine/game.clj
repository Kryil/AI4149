(ns ai4149.engine.game
  (:require [ai4149.engine.factory :refer :all]
            [ai4149.engine.unit :refer :all]
            [ai4149.engine.weapons :refer :all]
            [ai4149.engine.helpers :refer :all]))

(defn increase-turn-counter [state]
  (update-in state [:turn] inc))


(defn process-turn 
  "Apply all player actions into given state and return updated state."
  [state player-commands]
  (let [next-state (->
                     state
                     (process-units (filter (fn [cmd] (not (action= cmd :fire))) player-commands))
                     (process-factories)
                     (process-fire-commands (filter (fn [cmd] (action= cmd :fire)) player-commands))
                     (move-projectiles)
                     (process-build-commands (filter (fn [cmd] (= (:action cmd) :build)) player-commands))
                     increase-turn-counter)]
    next-state))
