(ns ai4149.engine.factory_test
  (:require [midje.sweet :refer :all]
            [ai4149.messages :refer :all]
            [ai4149.engine.helpers :refer :all]
            [ai4149.engine.collisions :as collisions]
            [ai4149.engine.game :as game]
            [ai4149.game-test-data :refer [simple-test-state]]))

(facts "building a harvester"
  (let [command #ai4149.messages.PlayerCommand["player-1" "p1-b1" :build :harvester]
        updated-state (game/process-turn simple-test-state [command])
        p-state (find-player-state "player-1" updated-state)]

    (fact "factories can build harvesters"
      (let [b-state (find-unit-state "p1-b1" p-state)]
        (:action b-state) => :constructing
        (:action-args b-state) => [:harvester 10]))

    (fact "player resources were substracted"
      (:resources p-state) => 1500)

    (fact "build command does not mess up the state"
      (count (:players updated-state)) => 2
      (keys (:players updated-state)) => ["player-1" "player-2"])

    (fact "processing a turn decreases remaining build time"
      (let [updated-state (game/process-turn updated-state [])
            p-state (find-player-state "player-1" updated-state)
            b-state (find-unit-state "p1-b1" p-state)]
        (:action b-state) => :constructing
        (:action-args b-state) => [:harvester 9]))

    (let [completed-state (reduce (fn [state n] (game/process-turn state [])) updated-state (range 10))
          p-state (find-player-state "player-1" completed-state)
          b-state (find-unit-state "p1-b1" p-state)
          new-unit (some #(when (= (:action %) :new) %) (vals (:units p-state)))]
      (fact "unit is placed on player units when completed"
        (:action b-state) => :idle
          (:action-args b-state) => nil)
      (fact "new state is changed to idle on next turn"
        (let [next-state (game/process-turn completed-state [])
              next-p-state (find-player-state "player-1" next-state)
              next-new-unit (some #(when (= (:action %) :new) %) (vals (:units next-p-state)))
              same-id-unit (some #(when (= (:id %) (:id new-unit)) %) (vals (:units next-p-state)))]
          new-unit =not=> nil
          next-new-unit => nil
          same-id-unit =not=> nil
          (:action same-id-unit) => :idle))
      (fact "unit is placed next to factory"
        (let [unit-area (collisions/get-unit-area new-unit (:rules completed-state))
              factory-area (collisions/get-unit-area b-state (:rules completed-state))]
          (collisions/area-intersects? factory-area unit-area) => falsey
          (collisions/area-intersects? (collisions/scale-area factory-area 1) unit-area) => truthy))
      (fact "unit is inside map"
        (let [unit-area (collisions/get-unit-area new-unit (:rules completed-state))]
          (every? #(>= % 0) (flatten unit-area)) => true
          (every? #(and (< (first %) (get-in completed-state [:map :width])) 
                        (< (second %) (get-in completed-state [:map :height])))
                  unit-area) => true
          ; todo check that unit is inside map when factory is at the corner
          ))

      ; todo units can not be placed on top of each other
      ; todo unit can only be placed to a vacant spot next to a building - otherwise halt factory
      ; todo unit should be on the list immediately when construction starts
      )))



(facts "can not build if not enough resources"
  (let [command #ai4149.messages.PlayerCommand["player-1" "p1-b1" :build :harvester]
        reduced-resources-state (update-in simple-test-state 
                                           [:players "player-1"] 
                                           (fn [ps] (assoc ps :resources 300)))
        updated-state (game/process-turn reduced-resources-state [command])
        p-state (find-player-state "player-1" updated-state)]

    (fact "building did not start"
      (let [b-state (find-unit-state "p1-b1" p-state)]
        (:action b-state) => :idle
        (:action-args b-state) => nil))

    (fact "player resources were not substracted"
      (:resources p-state) => 300)

    (fact "player-state has unable to comply notification"
      (count (:errors p-state)) => 1
      (second (first (:errors p-state))) => :no-resources)

    (fact "build command does not mess up the state"
      (count (:players updated-state)) => 2
      (keys (:players updated-state)) => ["player-1" "player-2"])))

(facts "can not build units without cost"
  (let [command #ai4149.messages.PlayerCommand["player-1" "p1-b1" :build :commander]
        updated-state (game/process-turn simple-test-state [command])
        p-state (find-player-state "player-1" updated-state)]

    (fact "building did not start"
      (let [b-state (find-unit-state "p1-b1" p-state)]
        (:action b-state) => :idle
        (:action-args b-state) => nil))

    (fact "player resources were not substracted"
      (:resources p-state) => 2000)

    (fact "player-state has unable to comply notification"
      (count (:errors p-state)) => 1
      (second (first (:errors p-state))) => :can-not-build)))

(facts "can not build units from wrong builder"
  (let [command #ai4149.messages.PlayerCommand["player-1" "p1-b1" :build :factory]
        updated-state (game/process-turn simple-test-state [command])
        p-state (find-player-state "player-1" updated-state)]

    (fact "building did not start"
      (let [b-state (find-unit-state "p1-b1" p-state)]
        (:action b-state) => :idle
        (:action-args b-state) => nil))

    (fact "player resources were not substracted"
      (:resources p-state) => 2000)

    (fact "player-state has unable to comply notification"
      (count (:errors p-state)) => 1
      (second (first (:errors p-state))) => :can-not-build)))

(facts "can not build units without built-by"
  (let [command #ai4149.messages.PlayerCommand["player-1" "p1-b1" :build :commander]
        updated-state (game/process-turn simple-test-state [command])
        p-state (find-player-state "player-1" updated-state)]

    (fact "building did not start"
      (let [b-state (find-unit-state "p1-b1" p-state)]
        (:action b-state) => :idle
        (:action-args b-state) => nil))

    (fact "player resources were not substracted"
      (:resources p-state) => 2000)

    (fact "player-state has unable to comply notification"
      (count (:errors p-state)) => 1
      (second (first (:errors p-state))) => :can-not-build)))


(facts "can not start a new build when another is in progress"
  (let [command #ai4149.messages.PlayerCommand["player-1" "p1-b1" :build :harvester]
        updated-state (game/process-turn simple-test-state [command])
        p-state (find-player-state "player-1" updated-state)]

    (fact "first build started"
      (let [b-state (find-unit-state "p1-b1" p-state)]
        (:action b-state) => :constructing
        (:action-args b-state) => [:harvester 10]))

    (fact "player resources were substracted"
      (:resources p-state) => 1500)
    
    (let [tank-command #ai4149.messages.PlayerCommand["player-1" "p1-b1" :build :tank]
          tank-state (game/process-turn updated-state [tank-command])
          p-state (find-player-state "player-1" tank-state)
          b-state (find-unit-state "p1-b1" p-state)]
      (fact "attempting a build again does not alter factory status"
          (:action b-state) => :constructing
          (:action-args b-state) => [:harvester 9])

      (fact "player resources were not substracted"
        (:resources p-state) => 1500)

      (fact "second build has an error message"
        (count (:errors p-state)) => 1
        (second (first (:errors p-state))) => :building-in-progress))))



