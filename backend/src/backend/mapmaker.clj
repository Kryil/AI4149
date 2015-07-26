(ns backend.mapmaker
  (:require [clojure.data.json :as json]))

(def gamefield (atom {:size [5050 3530]}))

(def gamefield-coords (atom {}))

(def base-area {:x 1200 :y 800})

(defn- rand-gap [a b]
  (+ a (rand-int b)))

(defn- rand-1-to-10 []
  (* 10 (rand-gap 1 10)))

(defn- new-rand [coord max-size]
  (if (< coord max-size)
    (+ coord (rand-1-to-10))
    (- coord (rand-1-to-10))))

(defn- rand-boolean []
  (boolean (< 0.5 (rand))))

(defn- conj-two-tenfold! [target x y]
  (conj! (conj! target (* 10 x)) (* 10 y)))

(defn- a-wall []
  (loop [n (rand-gap 2 5)
         points (transient [])
         x (rand-int 505)
         y (rand-int 353)
         elector (rand-boolean)]
    (if (zero? n) {:path (persistent! points) :type :Wall}
      (let [new-x (if elector x (new-rand x 225))
            new-y (if elector (new-rand y 175) y)]
        (recur (dec n)
               (conj-two-tenfold! points new-x new-y)
               new-x
               new-y 
               (boolean (not elector)))))))

(defn- obstacle-corners []
  (loop [n (rand-gap 1 8) obstacles (transient [])]
    (if (zero? n)
      (persistent! obstacles)
      (recur (dec n) (conj! obstacles (a-wall))))))

(defn empty-gamefield []
  (swap! gamefield assoc :obstacles (obstacle-corners))
  (swap! gamefield assoc :territory [])
  {:gamefield @gamefield :units [] :enemyUnits []})

(defn gamefield-as-json
  "for debugging purposes"
  [gamefield-name]
  (spit (str "../webreplay/resources/public/gamefields/" gamefield-name ".json")
        (json/write-str (empty-gamefield))))
