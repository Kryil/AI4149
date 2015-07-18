(ns webreplay.main
  (:use [org.httpkit.server :only [run-server]]
        webreplay.handler)
  (:require [ring.middleware.reload :as reload]
            [compojure.handler :refer [site]]))

(defn in-dev? [& args] true) ;; TODO read a config variable from command line, env, or file?

(defn -main [& args] ;; entry point, lein run will pick up and start from here
  (let [handler (if (in-dev? args)
                  (reload/wrap-reload (site #'app-routes)) ;; only reload when dev
                  (site app-routes))]
    (run-server (site #'app-routes) {:port 8080})))
