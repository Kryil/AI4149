(ns ai4149.engine.game
  (:require [ai4149.engine.factory :refer :all]
            [ai4149.engine.unit :refer :all]
            [ai4149.engine.weapons :refer :all]
            [ai4149.engine.helpers :refer :all]))

(defn increase-turn-counter [state]
  (update-in state [:turn] inc))

(defn- turns-over [state]
  (let [player-values (sort-by second > 
                               (map (fn [[pid ps]] 
                                      [pid (+ (:resources ps)
                                              (apply + (map (fn [[_ us]] 
                                                              (let [v (:cost (find-unit-rule (:type us) (:rules state)))]
                                                                (if (nil? v) 0 v)))
                                                            (:units ps))))])
                                    (:players state)))
        [winner & others] player-values]
  (assoc state :winner [(first winner) :value (second winner) (into {} others)])))

(defn check-game-end [state]
  (cond 
    (> (:turn state) (:turns state)) (turns-over state)
    :else state))


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
                     increase-turn-counter
                     check-game-end)]
    next-state))
