(ns ai4149.engine.helpers)

(defn find-player-state 
  "Search a PlayerState record from game state by player name"
  [player state]
  (get (:players state) player))

(defn find-unit-state
  "Search for an unit state from player state"
  [unit player-state]
  (get (:units player-state) unit))

(defn find-unit-rule
  "Search for unit rule"
  [unit-type rules]
  (first (filter (fn [rule] (= (:type rule) unit-type)) rules)))

(defn find-unit-cost
  "Search for the cost for given unit type"
  [unit-type rules]
  (:cost (find-unit-rule unit-type rules)))

(defn find-unit-weapon-rule [unit-type weapon rules]
  (weapon (:weapons (find-unit-rule unit-type rules))))

(defn map-state 
  "Apply function f to each item in state selected by k.
  Selected value is expected to be a map.
  Keys are preserved as is and only value is passed to f. 
  If f returns nil, key is removed from the map."
  [f state k]
  (assoc state k (into (sorted-map)
                       (filter (fn [[sk sv]] (not (nil? sv))) 
                               (map (fn [[sk sv]] [sk (f sv)]) 
                                    (k state))))))

(defn map-player-states 
  "Apply a function to every player state and return updated state"
  [f state]
  (map-state f state :players))

(defn map-player-building-states 
  "Apply a function to every building state in given player state and return updated player state"
  [f player-state]
  (map-state f player-state :units))

(defn map-player-unit-states 
  "Apply a function to every units state in given player state and return updated player state"
  [f player-state]
  (map-state f player-state :units))

(defn update-list 
  "Replaces item in state in coll defined by k with new-state-val compared by key-fn."
  ([state k new-val] (update-list state k new-val :id))
  ([state k new-val val-fn]
   (assoc state 
          k 
          (cons new-val 
                (filter (fn [s] (not= (val-fn s) (val-fn new-val))) (k state))))))

(defn add-to-list 
  "Add new-val to state coll k."
  [state k new-val]
  (assoc state k (cons new-val (k state))))

(defn remove-from-list
  [state k id-fn val-id]
  (assoc state k (filter (fn [s] (not= (id-fn s) val-id)) (k state))))

(defn add-player-error [state player command error]
  (update-in state [:players player :errors] (fn [ers] (cons [command error] ers))))


(defn uuid [] (str (java.util.UUID/randomUUID)))


(defmacro action= [a-state action]
  `(= (:action ~a-state) ~action))

(defmacro type= [a-state t]
  `(= (:type ~a-state) ~t))


(defmacro on-action 
  "Tests if :action matches in given state. If true, evaluates and returns then expr,
  otherwise else expr, if supplied, else a-state."
  ([a-state action then] `(on-action ~a-state ~action ~then ~a-state))
  ([a-state action then else] `(if (action= ~a-state ~action) ~then ~else)))


