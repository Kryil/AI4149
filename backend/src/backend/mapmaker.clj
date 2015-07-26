(ns backend.mapmaker
  (:require [clojure.data.json :as json]))

(def gamefield (atom {:size [5050 3530]}))

(def gamefield-coords (atom {}))

(def base-area {:x 1200 :y 800})

(defn- rand-gap [a b]
  (+ a (rand-int b)))

(defn- a-wall []
  (loop [i 0
         n (rand-gap 2 5)
         points (transient [])
         x (rand-int 505)
         y (rand-int 353)
         elector (boolean (< 0.5 (rand)))]
    (if (= i n)
      {:path (persistent! points) :type :Wall}
      (let [new-x (if elector x (if (< x 225)
                                  (+ x (* 10 (rand-gap 1 10)))
                                  (- x (* 10 (rand-gap 1 10)))))
            new-y (if elector (if (< y 175)
                                (+ y (* 10 (rand-gap 1 10)))
                                (- y (* 10 (rand-gap 1 10)))) y)]
        (recur (inc i)
               n
               (conj! (conj! points (* 10 new-x)) (* 10 new-y))
               new-x
               new-y 
               (boolean (not elector)))))))

(defn- obstacle-corners []
  (loop [i 0 n (rand-gap 1 8) obstacles (transient [])]
    (if (= i n)
      (persistent! obstacles)
      (recur (inc i) n (conj! obstacles (a-wall))))))

(defn empty-gamefield []
  (swap! gamefield assoc :obstacles (obstacle-corners))
  (swap! gamefield assoc :territory [])
  {:gamefield @gamefield :units [] :enemyUnits []})

(defn gamefield-as-json
  "for debugging purposes"
  []
  (spit "/tmp/dummyfield.json" (json/write-str (empty-gamefield))))
