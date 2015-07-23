(ns webreplay.communication
  (use [org.httpkit.server :only [send!]])
  (require [clojure.data.json :as json]))


(def game-channels (atom {}))
(def game-ids (atom {}))

(defn dummydata [i]
  (slurp (str "resources/public/script/dummydata" i ".json")))

(defn close [channel status]
  (let [game-id (get @game-ids channel)]
    (swap! game-ids dissoc channel)))

(defn open [channel data]
  (let [game-id (keyword (get (json/read-str data) "gameId"))]
    (swap! game-ids assoc channel game-id)
    (send! channel (dummydata 0))))

