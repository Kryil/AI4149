(ns webreplay.handler
  (:use [compojure.handler :only [site]]
        [compojure.core :only [defroutes GET POST DELETE ANY context]]
        org.httpkit.server)
  (:require [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [ring.util.response :as response]
            [compojure.route :as route]))


(defn websocket-handler [request]
  (with-channel request channel
                (on-close channel (fn [status] (println "channel closed: " status)))
                (on-receive channel (fn [data] ;; echo it back
                                      (println "data: " data)
                                      (send! channel data)))))

(defroutes app-routes
  (GET "/" [] (response/file-response "html/main.html" {:root "resources/public"}))
  (GET "/game" [] (response/file-response "html/game.html" {:root "resources/public"}))
  (GET "/ws" [] websocket-handler)
  (route/resources "/")
  (route/not-found "Not Found"))

(def app
  (wrap-defaults app-routes site-defaults))

