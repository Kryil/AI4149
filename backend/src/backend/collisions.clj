(ns backend.collisions
  (:use [backend.messages])
  (:import [backend.messages Coordinates]))

(defn shape->area [shape origo]
  (mapv (fn [[x y]] [(+ (:x origo) x) (+ (:y origo) y)]) shape))

(defn intersects? 
  "Tests are coordinates inside area. Currently supports only rectangular areas."
  [area coords]
  (let [sorted-area (sort area)
        min-point (first sorted-area)
        max-point (last sorted-area)
        x (:x coords)
        y (:y coords)]
    (and (>= x (first min-point))
         (>= y (second min-point))
         (<= x (first max-point))
         (<= y (second max-point)))))
