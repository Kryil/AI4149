(ns webreplay.communication
  (use [org.httpkit.server :only [send!]]))

(def game-channels (atom {}))
(def game-ids (atom {}))


(defn- loop-while [loop-fn]
  (let [lh (fn lh [iteration]
              (when (loop-fn iteration)
                (println (str "loop-fn returned truthy on iteration " iteration))
                (Thread/sleep 1000)
                (future (lh (inc iteration)))
              nil))]
    (future (lh 0)))
  nil)

(defn- dummydata [i]
  (try
    (slurp (str "resources/public/script/dummydata" i ".json"))
    (catch Exception e nil)))

(defn- replay-iteration-data [game-id iteration]
  (if (= game-id "dummygame")
    (dummydata iteration)
    nil)) ; todo get replay from storage


(defn- replay [channel game-id] 
  (println (str "replaying game " game-id))
  (loop-while (fn [iteration]
                (println (str game-id " iteration " iteration))
                (when-let [data (replay-iteration-data game-id iteration)]
                  (do 
                    (println "data ok")
                    (send! channel data))))))

(defn unsubscribe [channel status]
  (let [game-id (get @game-ids channel)]
    (swap! game-ids dissoc channel)))

(defn subscribe [channel game-id]
  (let [id (keyword game-id)]
    (swap! game-ids assoc channel id)
    (replay channel game-id)))


