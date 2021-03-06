(ns ai4149.engine.vectors 
  (:require [ai4149.messages :refer :all] 
            [clojure.math.numeric-tower :as math])
  (:import [ai4149.messages Coordinates]))

(defn add [a b]
  (let [ax (:x a)
        ay (:y a)
        bx (:x b)
        by (:y b)]
    (Coordinates. (+ ax bx) (+ ay by))))
   

(defn magnitude-between [a b]
  (let [ax (:x a)
        ay (:y a)
        bx (:x b)
        by (:y b)
        x (- bx ax)
        y (- by ay)]
    (math/sqrt (+ (* x x) (* y y)))))

(defn distance [a b]
  (Coordinates. (- (:x b) (:x a)) (- (:y b) (:y a))))

(defn magnitude [v]
  (let [x (:x v)
        y (:y v)]
    (math/sqrt (+ (* x x) (* y y)))))

(defn limit [v n]
  (let [m (magnitude v)]
    (if (> m n)
      (let [x (:x v)
            y (:y v)
            r (/ n m)]
        (Coordinates. (Math/round (double (* x r))) (Math/round (double (* y r)))))
      v)))


(defn calculate-next-position [velocity coordinates position]
  (let [requested-move (distance position (first coordinates))
        requested-magnitude (magnitude requested-move)
        move (limit requested-move velocity)
        next-position (add position move)]
    (cond (zero? requested-magnitude) [next-position nil]
          (< requested-magnitude velocity) (calculate-next-position (- velocity requested-magnitude)
                                                                    (rest coordinates)
                                                                    next-position)
          :else [next-position (if (= requested-magnitude velocity) nil coordinates)])))
 


