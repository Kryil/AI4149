(ns webreplay.communication
  (use [org.httpkit.server :only [send!]]))


(def game-channels (atom {}))
(def game-ids (atom {}))

(defn dummydata [i]
  (slurp (str "resources/public/script/dummydata" i ".json")))

(defn unsubscribe [channel status]
  (let [game-id (get @game-ids channel)]
    (swap! game-ids dissoc channel)))

(defn subscribe [channel game-id]
  (let [id (keyword game-id)]
    (swap! game-ids assoc channel id)
    (send! channel (dummydata 0))))

