(ns backend.game
  (:use [backend.messages])
  (:import [backend.messages Coordinates]))

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

(defn map-state 
  "Apply function f to each item in state selected by k."
  [f state k]
  (assoc state k (map f (k state))))

(defn map-player-states 
  "Apply a function to every player state and return updated state"
  [f state]
  (map-state f state :player-states))

(defn map-player-building-states 
  "Apply a function to every building state in given player state and return updated player state"
  [f player-state]
  (map-state f player-state :building-states))

(defn map-player-unit-states 
  "Apply a function to every units state in given player state and return updated player state"
  [f player-state]
  (map-state f player-state :unit-states))


(defmacro on-action 
  "Tests if :action matches in given state. If true, evaluates and returns then expr,
  otherwise else expr, if supplied, else a-state."
  ([a-state action then] `(on-action ~a-state ~action ~then ~a-state))
  ([a-state action then else] `(if (= (:action ~a-state) ~action) ~then ~else)))


(defn process-factory [building-state]
  (if (= (:action building-state) :constructing)
    (let [up-state (update-in building-state [:action-args 1] dec)]
      (if (<= (second (:action-args up-state)) 0)
        [(assoc (assoc up-state :action-args nil) :action :idle) (first (:action-args up-state))]
        [up-state nil]))
    [building-state nil]))

(defn process-player-factories [player-state]
  (let [b-states (:building-states player-state)
        units (:unit-states player-state)
        updated-b-states (map process-factory b-states)
        updated-state (assoc player-state :building-states (map first updated-b-states))
        updated-state (assoc updated-state 
                             :unit-states 
                             (apply conj 
                                    units
                                    (filter (complement nil?)
                                            (map (fn [[_ u-type]]
                                                   (when-not (nil? u-type)
                                                     (map->UnitState {:id "todo"
                                                                      :type u-type
                                                                      :position #backend.messages.Coordinates[0 0]
                                                                      :action :new
                                                                      :action-args nil})))
                                                 updated-b-states))))]
    updated-state))


(defn process-player-units [player-state]
  (map-player-unit-states 
      #(on-action % :new (assoc % :action :idle))
      player-state))



(defn process-factories [state]
  (map-player-states process-player-factories state))

(defn process-units [state]
  (map-player-states process-player-units state))

(defn process-build-command [state command]
  (let [player (:player command)
        building-id (:target-id command)
        action (:action command)
        unit-type (:action-args command)
        p-state (find-player-state player state)
        unit-rule (find-unit-rule unit-type (:rules state))
        unit-cost (:cost unit-rule)
        unit-build-time (:build-time unit-rule)
        resources-left (if (nil? unit-cost) 0 (- (:resources p-state) unit-cost))
        up-p-state (assoc p-state :resources resources-left)
        b-state (find-building-state building-id up-p-state)
        up-b-state (assoc (assoc b-state :action :constructing) :action-args [unit-type unit-build-time])
        up-p-state (assoc up-p-state :building-states 
                          (cons up-b-state 
                                (filter (fn [bs] (not= (:id bs) building-id)) (:building-states up-p-state))))]
    (assoc state :player-states 
           (cons (cond
                   (or (nil? (:built-by unit-rule)) 
                       (nil? unit-cost)
                       (not= (:type b-state) (:built-by unit-rule)))
                     (assoc p-state
                            :errors
                            (cons [command :can-not-build]
                                  (:errors p-state)))
                   (not= (:action b-state) :idle) (assoc p-state 
                                                         :errors 
                                                         (cons [command :building-in-progress] 
                                                               (:errors p-state)))
                   (< (:resources up-p-state) 0) (assoc p-state 
                                                        :errors 
                                                        (cons [command :no-resources] 
                                                              (:errors p-state)))
                   :else up-p-state)
                 (filter (fn [ps] (not= (:player ps) player)) (:player-states state))))))




(defn process-build-commands [state commands]
  (reduce process-build-command state commands))

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
    increase-turn-counter))
