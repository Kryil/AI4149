(ns ai4149.engine.helpers)

(defn find-player-state 
  "Search a PlayerState record from game state by player name"
  [player state]
  (first (filter (fn [ps] (= (:player ps) player)) (:player-states state))))

(defn find-building-state 
  "Search for a building state record from player state by building name"
  [building player-state]
  (first (filter (fn [bs] (= (:id bs) building)) (:building-states player-state))))

(defn find-unit-state
  "Search for an unit state from player state"
  [unit player-state]
  (first (filter (fn [us] (= (:id us) unit)) (:unit-states player-state))))

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

(defn update-state 
  "Replaces item in state in coll defined by k with new-state-val compared by key-fn."
  ([state k new-state-val] (update-state state k new-state-val :id))
  ([state k new-state-val key-fn]
    (assoc state k (cons 
                     new-state-val
                     (filter (fn [s] (not= (key-fn s) (key-fn new-state-val))) (k state))))))

(defn update-state 
  ([state k new-val] (update-state state k new-val (:id new-val)))
  ([state k new-val val-id]
   (assoc state 
          k 
          (cons new-val 
                (filter (fn [s] (not= (:id s) val-id)) (k state))))))
  #_(assoc up-p-state :building-states 
                          (cons up-b-state 
                                (filter (fn [bs] (not= (:id bs) building-id)) (:building-states up-p-state))))

(defn add-to-state 
  "Add new-val to state coll k."
  [state k new-val]
  (assoc state k (cons new-val (k state))))


(defn uuid [] (str (java.util.UUID/randomUUID)))


(defmacro action= [a-state action]
  `(= (:action ~a-state) ~action))

(defmacro on-action 
  "Tests if :action matches in given state. If true, evaluates and returns then expr,
  otherwise else expr, if supplied, else a-state."
  ([a-state action then] `(on-action ~a-state ~action ~then ~a-state))
  ([a-state action then else] `(if (action= ~a-state ~action) ~then ~else)))


