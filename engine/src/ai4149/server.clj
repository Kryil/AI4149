(ns ai4149.server
  (:require [clojure.data.json :as json]
            [clojure.java.io :as io])
  (:import [java.net ServerSocket]))

(defn receive-str
  "Read a line of textual data from the given socket"
  [socket]
  (.readLine (io/reader socket)))

(defn send-str
  "Send the given string message out over the given socket"
  [socket msg]
  (let [writer (io/writer socket)]
    (.write writer msg)
    (.flush writer)))

(defn start-server [port handler]
  (let [running (atom true)]
    (future
      (with-open [server-sock (ServerSocket. port)]
        (while @running
          (with-open [sock (.accept server-sock)]
            (let [msg-in (receive-str sock)
                  msg-out (handler msg-in)]
              (send-str sock msg-out))))))
    (fn [] 
      (reset! running false))))

