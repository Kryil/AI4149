(ns ai4149.engine.collisions
  (:use [ai4149.messages]
        [ai4149.engine.helpers])
  (:require [ai4149.engine.vectors :as vectors])
  (:import [ai4149.messages Coordinates]))

(defn avg [coll]
  (/ (apply + coll) (count coll)))

(defn shape->area [shape origo]
  (mapv (fn [[x y]] [(+ (:x origo) x) (+ (:y origo) y)]) shape))

(defn get-unit-area [unit unit-rules]
  (let [unit-rule (find-unit-rule (:type unit) unit-rules)]
    (shape->area (:shape unit-rule) (:position unit))))

(defn get-normalized-unit-shape [unit unit-rules]
  (let [unit-shape (:shape (find-unit-rule (:type unit) unit-rules))
        sorted-shape (sort unit-shape)
        min-point (first sorted-shape)
        normalize-factor-x (if (neg? (first min-point)) (- (first min-point)) 0)
        normalize-factor-y (if (neg? (second min-point)) (- (second min-point)) 0)]
    (mapv (fn [[x y]] [(+ x normalize-factor-x)
                       (+ y normalize-factor-y)])
          sorted-shape)))

(defn get-unit-size [unit unit-rules]
  (let [shape (get-normalized-unit-shape unit unit-rules)
        min-point (first shape)
        max-point (last shape)]
    [(- (first max-point) (first min-point) -1)
     (- (second max-point) (second min-point) -1)]))

(defn get-unit-coordinates [area unit-rule]
  (let [unit-shape (:shape unit-rule)
        sorted-area (sort area)
        sorted-shape (sort unit-shape)
        min-point (first sorted-area)
        min-shape-point (first sorted-shape)]
    (Coordinates. (- (first min-point) (first min-shape-point))
                  (- (second min-point) (second min-shape-point)))))

(defn get-center-coordinates [area]
  (let [sorted-area (sort area)
        min-point (first sorted-area)
        max-point (last sorted-area)]
    (Coordinates. (+ (first min-point) (/ (- (first max-point) (first min-point)) 2))
                  (+ (second min-point) (/ (- (second max-point) (second min-point)) 2)))))


(defn scale-area 
  "Scales area r amount. Currently supports only rectangles."
  [area r]
  (let [avg-x (avg (map first area))
        avg-y (avg (map second area))]
    (mapv (fn [[x y]] [(+ x (if (< x avg-x) (* -1 r) r))
                       (+ y (if (< y avg-y) (* -1 r) r))])
          area)))

(defn- make-rect [[top-x left-y] [w h]]
  (let [bottom-x (+ top-x w -1)
        right-y (+ left-y h -1)]
  [[top-x left-y]
   [top-x right-y]
   [bottom-x left-y]
   [bottom-x right-y]]))

(defn bordering-areas [area [width height]]
  (let [sorted-area (sort area)
        top-left (first sorted-area)
        bottom-left (second sorted-area)
        top-right (nth sorted-area 2)
        bottom-right (last sorted-area)
        min-x (- (first top-left) width)
        min-y (- (second top-left) height)
        max-x (+ (first top-right) width)
        max-y (+ (first bottom-right) height)
        items-in-x (quot max-x width)
        items-in-y (quot max-y height)]
            ; top row
    (filter (fn [a] (not-any? neg? (flatten a)))
            (concat (mapv (fn [n] (make-rect [(+ min-x (* width n))
                                              min-y] 
                                             [width height])) 
                          (range items-in-x))
                    ; right column
                    (mapv (fn [n] (make-rect [(inc (first top-right))
                                              (+ min-y (* height n))] 
                                             [width height])) 
                          (range 1 items-in-y))
                    ; bottom row
                    (mapv (fn [n] (make-rect [(+ min-x (* width n))
                                              (inc (second bottom-left))]
                                             [width height])) 
                          (range (- items-in-x 2) 0 -1))
                    ; left column
                    (mapv (fn [n] (make-rect [min-x 
                                              (+ min-y (* height n))]
                                             [width height])) 
                          (range (dec items-in-y) 0 -1))))))

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

(defn all-areas 
  ([state] (all-areas state []))
  ([state not-ids]
    (let [rules (:rules state)
          player-states (:player-states state)
          units (filter (fn [us] (every? #(not= (:id us) %) not-ids)) (apply concat (map :unit-states player-states)))
          buildings (filter (fn [bs] (every? #(not= (:id bs) %) not-ids)) (apply concat (map :building-states player-states)))]
      (concat (map #(get-unit-area % rules) units) 
              (map #(get-unit-area % rules) buildings)))))
     

(defn area-free? 
  ([state area] (area-free? state area []))
  ([state area not-ids]
    (let [areas (all-areas state not-ids)]
      (not-any? (partial area-intersects? area) areas))))

(defn in-area [state area]
  (let [rules (:rules state)
        pred (partial area-intersects? area)]
    (map (fn [ps] [(:player ps) 
                   (concat
                     (filter #(pred (get-unit-area % rules)) (:unit-states ps))
                     (filter #(pred (get-unit-area % rules)) (:building-states ps)))])
         (:player-states state))))

(defn nearest-in-area 
  ([state area] (nearest-in-area state area (get-center-coordinates area)))
  ([state area center-coords]
   (let [units-in-area (in-area state area)
         unit-list (apply concat (map (fn [[p units]] (map (fn [u] [p (:id u) (:position u)]) units)) units-in-area))
         nearest (first (sort-by
                          (fn [coll] 
                            (vectors/magnitude-between center-coords (nth coll 2)))
                          <
                          unit-list))
         match (some #(when (= (first %) (first nearest)) %) units-in-area)]
     [(first match) (some #(when (= (:id %) (second nearest)) %) (second match))])))
 

(defn- coords->area [a b]
  [[(:x a) (:y a)] [(:x b) (:y a)] [(:x b) (:y b)] [(:x a) (:y b)]])

(defn- coords-between [a b]
  (loop [memo []
         [next-pos remaining-moves] [a [b]]]
    (if (nil? remaining-moves)
      (conj memo next-pos)
      (recur (conj memo next-pos) (vectors/calculate-next-position 1 remaining-moves next-pos)))))

(defn between-coords [state a b]
  (let [area (coords->area a b)
        coords (coords-between a b)
        units-in-area (in-area state area)]
    (map (fn [[player units]]
           [player (filter (fn [u] (some #(coord-intersects? (get-unit-area u (:rules state)) %) coords)) units)])
         units-in-area)))

(defn nearest-between-coords 
  ([state a b] (nearest-between-coords state a b []))
  ([state a b not-ids]
   (let [units (between-coords state a b)
         unit-list (filter (fn [u] (every? #(not= (second u) %) not-ids))
                           (apply concat (map (fn [[p p-units]] 
                                                (map (fn [u] [p (:id u) (:position u)]) 
                                                     p-units)) 
                                              units)))
         nearest (first 
                   (sort-by
                     (fn [coll] 
                       (vectors/magnitude-between a (nth coll 2)))
                     <
                     unit-list))
         match (some #(when (= (first %) (first nearest)) %) units)]
     [(first match) (some #(when (= (:id %) (second nearest)) %) (second match))])))


