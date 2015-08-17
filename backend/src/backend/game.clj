(ns backend.game
  (:require [backend.messages :as msgs]))

(defn find-player-state 
  "Search a PlayerState record from game state by player name"
  [player state]
  (first (filter (fn [ps] (= (:player ps) player)) (:player-states state))))

(defn find-building-state 
  "Search for a building state record from player state by building name"
  [building player-state]
  (first (filter (fn [bs] (= (:id bs) building)) (:building-states player-state))))

(defn find-unit-rule
  "Search for unit rule"
  [unit-type rules]
  (first (filter (fn [rule] (= (:type rule) unit-type)) rules)))

(defn find-unit-cost
  "Search for the cost for given unit type"
  [unit-type rules]
  (:cost (find-unit-rule unit-type rules)))


(defn process-build-command [state command]
  (let [player (:player command)
        building-id (:target-id command)
        action (:action command)
        args (:action-args command)
        p-state (find-player-state player state)
        up-p-state (update-in p-state [:resources] (fn [r] (- r (find-unit-cost args (:rules state)))))
        b-state (find-building-state building-id up-p-state)
        up-b-state (assoc (assoc b-state :action :constructing) :action-args args)
        up-p-state (assoc up-p-state :building-states 
                          (cons up-b-state 
                                (filter (fn [bs] (not (:id bs) building-id)) (:building-states up-p-state))))
        up-state (assoc state :player-states 
                        (cons up-p-state 
                              (filter (fn [ps] (= (:player ps) player)) (:player-states state))))]
    up-state))




(defn process-build-commands [commands state]
  (reduce process-build-command state commands))

(defn increase-turn-counter [state]
  (update-in state [:turn] inc))


(defn process-turn 
  "Apply all player actions into given state and return updated state."
  [state player-commands]
;  (increase-turn-counter
;    (process-build-commands (filter (fn [cmd] (= (:action cmd) :build)) player-commands)
;                            state)))
  (->>
    state
    (process-build-commands (filter (fn [cmd] (= (:action cmd) :build)) player-commands))
    increase-turn-counter))
