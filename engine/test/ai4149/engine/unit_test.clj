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
  (let [command (PlayerCommand. "player-1" "p1-commander" :move [(Coordinates. 40 12)])
        updated-state (game/process-turn simple-test-state [command])
        p-state (find-player-state "player-1" updated-state)]

    (fact "commander moved"
      (let [commander (find-unit-state "p1-commander" p-state)]
        (:action commander) => :moving
        (:position commander) => (Coordinates. 35 12)
        (:action-args commander) => [(Coordinates. 40 12)]))
    (fact "commander moves more on next turn"
      (let [next-state (game/process-turn updated-state [])
            p-state (find-player-state "player-1" next-state)
            commander (find-unit-state "p1-commander" p-state)]
        (:action commander) => :idle
        (:position commander) => (Coordinates. 40 12)
        (:action-args commander) => nil))
    (let [new-command (PlayerCommand. "player-1" "p1-commander" :move [(Coordinates. 35 30)])
          new-state (game/process-turn updated-state [new-command])
          p-state (find-player-state "player-1" new-state)
          commander (find-unit-state "p1-commander" p-state)]
      (fact "new move command overrides previous command"
        (:action commander) => :moving
        (:action-args commander) => [(Coordinates. 35 30)]
        (:position commander) => (Coordinates. 35 17))))
       
  (let [command (PlayerCommand. "player-1" "p1-commander" :move [(Coordinates. 11 16)])
        updated-state (game/process-turn simple-test-state [command])
        p-state (find-player-state "player-1" updated-state)
        commander (find-unit-state "p1-commander" p-state)]
    (facts "commander will not move over factory"
      (fact "state is obstructed"
          (:action commander) => :obstructed)
      (fact "unit did not move" 
        (:position commander) => (Coordinates. 30 12))
      (fact "unit has the coordinates in action args"
        (:action-args commander) => [(Coordinates. 11 16)])
      (fact "unit does not move on next turn"
        (let [next-turn (game/process-turn updated-state [])
              n-p-state (find-player-state "player-1" next-turn)
              n-commander (find-unit-state "p1-commander" n-p-state)]
          (:action n-commander) => :obstructed
          (:position n-commander) => (Coordinates. 30 12)
          (:action-args n-commander) => [(Coordinates. 11 16)]))
      (let [p-state-without-factory (remove-from-state p-state :building-states :id "p1-b1")
            state-without-factory (update-state updated-state :player-states p-state-without-factory :player)
            next-state (game/process-turn state-without-factory [])
            next-p-state (find-player-state "player-1" next-state)
            next-commander (find-unit-state "p1-commander" next-p-state)]
        (fact "unit continues moving if obstruction is removed"
          (:action next-commander) => :moving
          (:position next-commander) => (Coordinates. 25 13))))))

; todo test obstructed movement against map
; todo dead units can not be ordered around


