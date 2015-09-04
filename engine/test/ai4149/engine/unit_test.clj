(ns ai4149.engine.unit_test
  (:require [midje.sweet :refer :all]
            [ai4149.messages :refer :all]
            [ai4149.engine.helpers :refer :all]
            [ai4149.engine.collisions :as collisions]
            [ai4149.engine.game :as game]
            [ai4149.game-test-data :refer [simple-test-state]])
  (:import [ai4149.messages Coordinates]
           [ai4149.messages PlayerCommand]))

(facts "moving the commander"
  (let [command (PlayerCommand. "player-1" "p1-commander" :move [(Coordinates. 30 12)])
        updated-state (game/process-turn simple-test-state [command])
        p-state (find-player-state "player-1" updated-state)]

    (fact "commander moved"
      (let [commander (find-unit-state "p1-commander" p-state)]
        (:action commander) => :moving
        (:position commander) => (Coordinates. 25 12)
        (:action-args commander) => [(Coordinates. 30 12)]))
    (fact "commander moves more on next turn"
      (let [next-state (game/process-turn updated-state [])
            p-state (find-player-state "player-1" next-state)
            commander (find-unit-state "p1-commander" p-state)]
        (:action commander) => :idle
        (:position commander) => (Coordinates. 30 12)
        (:action-args commander) => nil))))


