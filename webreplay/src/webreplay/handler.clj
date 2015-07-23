(ns webreplay.handler
  (:use [compojure.handler :only [site]]
        [compojure.core :only [defroutes GET POST DELETE ANY context]]
        org.httpkit.server)
  (:require [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [ring.util.response :as response]
            [compojure.route :as route]
            [clojure.data.json :as json]
            [webreplay.communication :as comm]))


(defn websocket-handler [request]
  (with-channel request channel
                (on-receive channel #(comm/subscribe channel (get (json/read-str %) "gameId")))
                (on-close channel #(comm/unsubscribe channel %))))

(defroutes app-routes
  (GET "/" []
       (response/file-response "html/main.html" {:root "resources/public"}))
  (GET "/game" []
       (response/file-response "html/game.html" {:root "resources/public"}))
  (GET "/ws" [] websocket-handler)
  (route/resources "/")
  (route/not-found "Not Found"))

(def app
  (wrap-defaults app-routes site-defaults))

