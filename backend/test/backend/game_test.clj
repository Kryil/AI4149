(ns backend.game_test
  (:require [midje.sweet :refer :all]
            [backend.messages :refer :all]
            [backend.game :as game]))

(def test-state
  #backend.messages.FullGameState
  {:turn 0 
   :turns 100
   :rules [#backend.messages.UnitRule
           {:name "Commander"
            :type :commander
            :speed 5
            :armor 10
            :cost nil}
           #backend.messages.UnitRule
           {:name "Lt. Commander"
            :type :harvester
            :speed 10
            :armor 1
            :cost 500}
           #backend.messages.UnitRule
           {:name "Tank"
            :type :tank
            :speed 10
            :armor 1
            :cost 100}]
   :player-states
   [#backend.messages.PlayerState
    {:player "player-1"
     :resources 2000
     :unit-states [#backend.messages.UnitState
                   {:id "p1-commander"
                    :type :commander
                    :position [1 2]
                    :action :idle
                    :action-coordinates nil}]
     :building-states [#backend.messages.BuildingState{:id "p1-b1" 
                                                       :type :factory
                                                       :action :idle
                                                       :action-args nil}]
     :things []}
    #backend.messages.PlayerState
    {:player "player-2"
     :resources 1500
     :unit-states [#backend.messages.UnitState
                   {:id "p2-u1"
                    :position [110 200]
                    :action "idle"
                    :action-coordinates nil}]
     :building-states [#backend.messages.BuildingState{:id "p2-b1" 
                                                       :type :factory
                                                       :action :idle
                                                       :action-args nil}]
     :things []}]})

(fact "turn counter is increased"
  (:turn (game/process-turn test-state [])) => 1)

(facts "building a harvester"
  (let [command #backend.messages.PlayerCommand["player-1" "p1-b1" :build :harvester]
        updated-state (game/process-turn test-state [command])
        p-state (game/find-player-state "player-1" updated-state)]

    (fact "factories can build harvesters"
      (let [b-state (game/find-building-state "p1-b1" p-state)]
        (:action b-state) => :constructing
        (:action-args b-state) => :harvester))

    (fact "player resources were substracted"
      (:resources p-state) => 1500)

    (fact "build command does not mess up the state"
      (count (:player-states updated-state)) => 2
      (map :player (:player-states updated-state)) => ["player-1" "player-2"])))

(facts "can not build if not enough resources"
  (let [command #backend.messages.PlayerCommand["player-1" "p1-b1" :build :harvester]
        reduced-resources-state (update-in test-state [:player-states] (fn [l] (map (fn [ps] (assoc ps :resources 300)) l)))
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
        updated-state (game/process-turn test-state [command])
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
        updated-state (game/process-turn test-state [command])
        p-state (game/find-player-state "player-1" updated-state)]

    (fact "first build started"
      (let [b-state (game/find-building-state "p1-b1" p-state)]
        (:action b-state) => :constructing
        (:action-args b-state) => :harvester))

    (fact "player resources were substracted"
      (:resources p-state) => 1500)
    
    (let [tank-command #backend.messages.PlayerCommand["player-1" "p1-b1" :build :tank]
          tank-state (game/process-turn updated-state [tank-command])
          p-state (game/find-player-state "player-1" tank-state)
          b-state (game/find-building-state "p1-b1" p-state)]
      (fact "attempting a build again does not alter factory status"
          (:action b-state) => :constructing
          (:action-args b-state) => :harvester)

      (fact "player resources were not substracted"
        (:resources p-state) => 1500)

      (fact "second build has an error message"
        (count (:errors p-state)) => 1
        (second (first (:errors p-state))) => :building-in-progress))))


