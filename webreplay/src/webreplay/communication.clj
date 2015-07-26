(ns webreplay.communication
  (use [org.httpkit.server :only [send!]]
       [clojure.tools.logging :only [info debug]]))

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

(defn- load-scene [game-id i]
  (try
    (slurp (str "resources/public/gamefields/" game-id i ".json"))
    (catch Exception e nil)))

(defn- replay-iteration-data [game-id iteration]
  (if (not (empty? game-id))
    (load-scene game-id iteration)
    nil)) ; todo get replay from storage

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

