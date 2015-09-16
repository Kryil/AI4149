(ns webreplay.communication
  (:require [clojure-watch.core :refer [start-watch]]
            [org.httpkit.server :refer [send!]]
            [clojure.data.json :as json]
            [ai4149.messages :refer :all]
            [clojure.tools.logging :refer [info debug spyf]]))

(def unit-type-map
  {:commander "Commander"
   :harvester "Harvester"
   :tank "Squaddy"
   :factory "Stronghold"})

(def game-channels (atom {}))
(def game-ids (atom {}))


(defn- loop-while [loop-fn]
  (let [lh (fn lh [iteration]
             (debug "loop iteration" iteration)
              (when (loop-fn iteration)
                (debug "loop-fn returned truthy on iteration" iteration)
                (Thread/sleep 1000)
                (future (lh (inc iteration)))
              nil))]
    (future (lh 0)))
  nil)

(defn- dummydata [i]
  (try
    (slurp (str "resources/public/gamefields/dummydata" i ".json"))
    (catch Exception e nil)))

(defn- live-game-from-file-system [game-id]
  (start-watch [{:path (str "c:/temp/ai4149/" game-id)
                 :event-types [:create]
                 :bootstrap (fn [path] (println "Starting to watch " path))
                 :callback (fn [event filename] (println event filename))}]))

(defn- replay-from-file-system [game-id iteration]
  (let [filename (str "c:/temp/ai4149/" game-id "/turn-" iteration ".clj")
        state-file (clojure.java.io/file filename)]
    (info "replaying game from file" filename)
    (if (.exists state-file)
      (let [state (load-file filename)]
        (info "found replay state from " filename state)
        (spyf "state json %s" (json/write-str {:gamefield {:size [(get-in state [:map :width]) (get-in state [:map :height])]
                                     :obstacles [] ;todo
                                     :territory [] ;todo
                                     }
                         :units (map (fn [[uid u]] {:type ((:type u) unit-type-map)
                                                    :location [(:x (:position u)) (:y (:position u))]
                                                    :health (:health u)})
                                     (:units (second (first (:players state)))))
                         :enemyUnits (map (fn [[uid u]] {:type ((:type u) unit-type-map)
                                                         :location [(:x (:position u)) (:y (:position u))]
                                                         :health (:health u)})
                                     (:units (second (second (:players state)))))})))
      (do (info "Game does not exist!") nil))))

(defn- replay-exists? [game-id]
  (let [dir (clojure.java.io/file (str "c:/temp/ai4149/" game-id))]
    (and (.exists dir) (.isDirectory dir))))


(defn- replay-iteration-data [game-id iteration]
  (cond 
    (= game-id "dummygame") (dummydata iteration)
    (replay-exists? game-id) (replay-from-file-system game-id iteration)
    :else (do (info "game not found" game-id) nil))) ; todo get replay from storage

(defn- replay [channel game-id] 
  (info (str "replaying game " game-id))
  (loop-while #(when-let [data (replay-iteration-data game-id %)]
                 (send! channel data))))

(defn unsubscribe [channel status]
  (let [game-id (get @game-ids channel)]
    (swap! game-ids dissoc channel)
    (info channel "closed, status" status)))

(defn subscribe [channel game-id]
  (let [id (keyword game-id)]
    (swap! game-ids assoc channel id)
    (replay channel game-id)))

