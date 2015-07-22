(ns webreplay.handler
  (:use [compojure.handler :only [site]]
        [compojure.core :only [defroutes GET POST DELETE ANY context]]
        org.httpkit.server)
  (:require [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [ring.util.response :as response]
            [compojure.route :as route]
            [clojure.data.json :as json]))

(def game-channels (atom {}))
(def game-ids (atom {}))

(defn dummydata []
  (slurp "resources/public/script/dummydata.json"))

(defn clean-up [channel status]
  (let [game-id (get @game-ids channel)]
    (swap! game-ids dissoc channel)))

(defn open [channel data]
  (let [game-id (keyword (get (json/read-str data) "gameId"))]
    (swap! game-ids assoc channel game-id)
    (send! channel (dummydata))))

(defn websocket-handler [request]
  (with-channel request channel
                (on-receive channel (partial open channel))
                (on-close channel (partial clean-up channel))))

(defroutes app-routes
  (GET "/" []
       (response/file-response "html/main.html" {:root "resources/public"}))
  (GET "/game" []
       (response/file-response "html/game.html" {:root "resources/public"}))
  (GET "/ws" []
       websocket-handler)
  (route/resources "/")
  (route/not-found "Not Found"))

(def app
  (wrap-defaults app-routes site-defaults))

