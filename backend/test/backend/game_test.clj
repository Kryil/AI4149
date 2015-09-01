(ns backend.game_test
  (:require [midje.sweet :refer :all]
            [backend.messages :refer :all]
            [backend.game :as game]
            [backend.game-test-data :refer [simple-test-state]]))

(fact "turn counter is increased"
  (:turn (game/process-turn simple-test-state [])) => 1)

(facts "building a harvester"
  (let [command #backend.messages.PlayerCommand["player-1" "p1-b1" :build :harvester]
        updated-state (game/process-turn simple-test-state [command])
        p-state (game/find-player-state "player-1" updated-state)]

    (fact "factories can build harvesters"
      (let [b-state (game/find-building-state "p1-b1" p-state)]
        (:action b-state) => :constructing
        (:action-args b-state) => [:harvester 10]))

    (fact "player resources were substracted"
      (:resources p-state) => 1500)

    (fact "build command does not mess up the state"
      (count (:player-states updated-state)) => 2
      (map :player (:player-states updated-state)) => ["player-1" "player-2"])

    (fact "processing a turn decreases remaining build time"
      (let [updated-state (game/process-turn updated-state [])
            p-state (game/find-player-state "player-1" updated-state)
            b-state (game/find-building-state "p1-b1" p-state)]
        (:action b-state) => :constructing
        (:action-args b-state) => [:harvester 9]))

    (let [final-state (reduce (fn [state n] (game/process-turn state [])) updated-state (range 10))
          p-state (game/find-player-state "player-1" final-state)
          b-state (game/find-building-state "p1-b1" p-state)]
      (fact "unit is placed on player units when completed"
        (:action b-state) => :idle
          (:action-args b-state) => nil)
      (fact "new state is changed to idle on next turn"
        (let [next-state (game/process-turn final-state [])
              next-p-state (game/find-player-state "player-1" next-state)
              new-unit (some #(when (= (:action %) :new) %) (:unit-states p-state))
              next-new-unit (some #(when (= (:action %) :new) %) (:unit-states next-p-state))
              same-id-unit (some #(when (= (:id %) (:id new-unit)) %) (:unit-states next-p-state))]
          new-unit =not=> nil
          next-new-unit => nil
          same-id-unit =not=> nil
          (:action same-id-unit) => :idle))
      ; todo unit is placed next to factory
      ; todo units can not be placed on top of each other
      ; todo unit can only be placed to a vacant spot next to a building - otherwise halt factory
      )))



(facts "can not build if not enough resources"
  (let [command #backend.messages.PlayerCommand["player-1" "p1-b1" :build :harvester]
        reduced-resources-state (update-in simple-test-state 
                                           [:player-states] 
                                           (fn [l] (map (fn [ps] (assoc ps :resources 300)) l)))
        updated-state (game/process-turn reduced-resources-state [command])
        p-state (game/find-player-state "player-1" updated-state)]

    (fact "building did not start"
      (let [b-state (game/find-building-state "p1-b1" p-state)]
        (:action b-state) => :idle
        (:action-args b-state) => nil))

    (fact "player resources were not substracted"
      (:resources p-state) => 300)

    (fact "player-state has unable to comply notification"
      (count (:errors p-state)) => 1
      (second (first (:errors p-state))) => :no-resources)

    (fact "build command does not mess up the state"
      (count (:player-states updated-state)) => 2
      (map :player (:player-states updated-state)) => ["player-1" "player-2"])))

(facts "can not build units without cost"
  (let [command #backend.messages.PlayerCommand["player-1" "p1-b1" :build :commander]
        updated-state (game/process-turn simple-test-state [command])
        p-state (game/find-player-state "player-1" updated-state)]

    (fact "building did not start"
      (let [b-state (game/find-building-state "p1-b1" p-state)]
        (:action b-state) => :idle
        (:action-args b-state) => nil))

    (fact "player resources were not substracted"
      (:resources p-state) => 2000)

    (fact "player-state has unable to comply notification"
      (count (:errors p-state)) => 1
      (second (first (:errors p-state))) => :can-not-build)))


(facts "can not start a new build when another is in progress"
  (let [command #backend.messages.PlayerCommand["player-1" "p1-b1" :build :harvester]
        updated-state (game/process-turn simple-test-state [command])
        p-state (game/find-player-state "player-1" updated-state)]

    (fact "first build started"
      (let [b-state (game/find-building-state "p1-b1" p-state)]
        (:action b-state) => :constructing
        (:action-args b-state) => [:harvester 10]))

    (fact "player resources were substracted"
      (:resources p-state) => 1500)
    
    (let [tank-command #backend.messages.PlayerCommand["player-1" "p1-b1" :build :tank]
          tank-state (game/process-turn updated-state [tank-command])
          p-state (game/find-player-state "player-1" tank-state)
          b-state (game/find-building-state "p1-b1" p-state)]
      (fact "attempting a build again does not alter factory status"
          (:action b-state) => :constructing
          (:action-args b-state) => [:harvester 9])

      (fact "player resources were not substracted"
        (:resources p-state) => 1500)

      (fact "second build has an error message"
        (count (:errors p-state)) => 1
        (second (first (:errors p-state))) => :building-in-progress))))


