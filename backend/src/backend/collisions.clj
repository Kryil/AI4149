(ns backend.collisions
  (:use [backend.messages]
        [backend.helpers])
  (:import [backend.messages Coordinates]))

(defn avg [coll]
  (/ (apply + coll) (count coll)))

(defn shape->area [shape origo]
  (mapv (fn [[x y]] [(+ (:x origo) x) (+ (:y origo) y)]) shape))

(defn get-unit-area [unit unit-rules]
  (let [unit-rule (find-unit-rule (:type unit) unit-rules)]
    (shape->area (:shape unit-rule) (:position unit))))

(defn scale-area 
  "Scales area r amount. Currently supports only rectangles."
  [area r]
  (let [avg-x (avg (map first area))
        avg-y (avg (map second area))]
    (mapv (fn [[x y]] [(+ x (if (< x avg-x) (* -1 r) r))
                       (+ y (if (< y avg-y) (* -1 r) r))])
          area)))

(defn point-intersects? 
  "Tests is the point inside area. Currently supports only rectangular
  areas."
  [area point]
  (let [sorted-area (sort area)
        min-point (first sorted-area)
        max-point (last sorted-area)
        x (first point)
        y (second point)]
    (and (>= x (first min-point))
         (>= y (second min-point))
         (<= x (first max-point))
         (<= y (second max-point)))))

(defn coord-intersects?
  "Tests are coordinates inside area. Currently supports only rectangular
  areas."
  [area coords]
  (point-intersects? area [(:x coords) (:y coords)]))
 
(defn area-intersects?
  "Tests does the two areas intersect each other. Currently supports only 
  rectangular areas."
  [area-a area-b]
  (let [sorted-area-a (sort area-a)
        sorted-area-b (sort area-b)
        min-point-a (first sorted-area-a)
        max-point-a (last sorted-area-a)
        min-point-b (first sorted-area-b)
        max-point-b (last sorted-area-b)]
    (or (point-intersects? sorted-area-a min-point-b)
        (point-intersects? sorted-area-a max-point-b)
        (point-intersects? sorted-area-b min-point-a)
        (point-intersects? sorted-area-b max-point-a))))

(defn all-areas [state]
  (let [rules (:rules state)
        player-states (:player-states state)
        units (apply concat (map :unit-states player-states))
        buildings (apply concat (map :building-states player-states))]
    (concat (map #(get-unit-area % rules) units) 
            (map #(get-unit-area % rules) buildings))))
   

(defn area-free? [state area]
  (let [areas (all-areas state)]
    (not-any? (partial area-intersects? area) areas)))


