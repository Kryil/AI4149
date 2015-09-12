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

(defn- commander-dead [state]
  (let [alive-player (first (filter (fn [[pid ps]] (some #(and (type= % :commander) 
                                                               (not (action= % :dead)))
                                                         (vals (:units ps))))
                                    (:players state)))]
  (assoc state :winner [(first alive-player) :last-man-standing])))

(defn- is-a-commander-dead? [state]
  (let [players (vals (:players state))
        units (apply concat (map #(vals (:units %)) players))
        commanders (filter #(type= % :commander) units)]
    (some #(action= % :dead) commanders)))
    

(defn check-game-end [state]
  (cond 
    (> (:turn state) (:turns state)) (turns-over state)
    (is-a-commander-dead? state) (commander-dead state)
    :else state))


(defn process-turn 
  "Apply all player actions into given state and return updated state."
  [state player-commands]
  (if (not (nil? (:winner state)))
    state
    (let [next-state (->
                       state
                       (process-units (filter (fn [cmd] (not (action= cmd :fire))) player-commands))
                       (process-factories)
                       (process-fire-commands (filter (fn [cmd] (action= cmd :fire)) player-commands))
                       (move-projectiles)
                       (process-build-commands (filter (fn [cmd] (= (:action cmd) :build)) player-commands))
                       increase-turn-counter
                       check-game-end)]
      next-state)))
