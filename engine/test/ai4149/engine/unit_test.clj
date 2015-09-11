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
        (:position commander) => (Coordinates. 35 17)))))

(facts "moving error conditions"
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
      (let [p-state-without-factory (update-in p-state [:units] #(dissoc % "p1-b1"))
            state-without-factory (assoc-in updated-state [:players "player-1"] p-state-without-factory)
            next-state (game/process-turn state-without-factory [])
            next-p-state (find-player-state "player-1" next-state)
            next-commander (find-unit-state "p1-commander" next-p-state)]
        (fact "unit continues moving if obstruction is removed"
          (:action next-commander) => :moving
          (:position next-commander) => (Coordinates. 25 13))))))

(facts "collecting resources"
  (let [command (PlayerCommand. "player-2" "p2-harvester-1" :collect nil)
        collect-state (game/process-turn simple-test-state [command])
        p-state (find-player-state "player-2" collect-state)
        u-state (find-unit-state "p2-harvester-1" p-state)]
    (fact "resources were added into player state"
      (:resources p-state) => 2500)
    (fact "resouces where substracted from map"
      (let [resource (first (filter (fn [r] (= (:position r) (Coordinates. 200 200))) 
                                    (get-in collect-state [:map :resources])))]
        (:amount resource) => 500))
    (let [recollect-state (game/process-turn collect-state [command])
          rep-state (find-player-state "player-2" recollect-state)]
      (fact "when collecting more than the field has, only what field has is returned"
        (:resources rep-state) => 3000)
      (fact "resource was removed from map"
        (count (get-in recollect-state [:map :resources])) => 0))))

; todo errors



; todo test obstructed movement against map
; todo dead units can not be ordered around


