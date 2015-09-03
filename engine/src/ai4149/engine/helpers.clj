(ns ai4149.engine.helpers)

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

(defn uuid [] (str (java.util.UUID/randomUUID)))

(defmacro on-action 
  "Tests if :action matches in given state. If true, evaluates and returns then expr,
  otherwise else expr, if supplied, else a-state."
  ([a-state action then] `(on-action ~a-state ~action ~then ~a-state))
  ([a-state action then else] `(if (= (:action ~a-state) ~action) ~then ~else)))


