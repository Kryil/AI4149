(ns webreplay.communication
  (use [org.httpkit.server :only [send!]]))

(def game-channels (atom {}))
(def game-ids (atom {}))


(defn- loop-while [loop-fn]
  (let [lh (fn lh [iteration]
              (when (loop-fn iteration)
                (println (str "loop-fn returned truthy on iteration " iteration))
                (future (lh (inc iteration)))
              nil))]
    (lh 0)))

(defn- dummydata [i]
  (try
    (slurp (str "resources/public/script/dummydata" i ".json"))
    (catch Exception e nil)))

(defn- replay [channel game-id] 
  (println (str "replaying game " game-id))
  (loop-while (fn [iteration]
                (println (str game-id " iteration " iteration))
                (if (= game-id "dummygame")
                  (when-let [data (dummydata iteration)]
                    (do 
                      (send! channel data)
                      (Thread/sleep 1000)
                      true))
                  nil)))) ; todo get replay from storage

(defn unsubscribe [channel status]
  (let [game-id (get @game-ids channel)]
    (swap! game-ids dissoc channel)))

(defn subscribe [channel game-id]
  (let [id (keyword game-id)]
    (swap! game-ids assoc channel id)
    (replay channel game-id)))


