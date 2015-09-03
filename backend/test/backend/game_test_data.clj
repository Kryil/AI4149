(ns backend.game-test-data 
  (:require [midje.sweet :refer :all]
            [backend.messages :refer :all]))
 

(def simple-test-state
  #backend.messages.FullGameState
  {:turn 0 
   :turns 100
   :rules [#backend.messages.UnitRule
           {:name "Commander"
            :type :commander
            :speed 5
            :armor 10
            :cost nil
            :build-time nil
            :built-by nil
            :shape [[-5 -5] [5 -5] [5 5] [-5 5]]}
           #backend.messages.UnitRule
           {:name "Lt. Commander"
            :type :harvester
            :speed 10
            :armor 1
            :cost 500
            :build-time 10
            :built-by :factory
            :shape [[-4 -4] [4 -4] [4 4] [-4 4]]}
           #backend.messages.UnitRule
           {:name "Tank"
            :type :tank
            :speed 10
            :armor 1
            :cost 100
            :build-time 5
            :built-by :factory
            :shape [[-2 -2] [2 -2] [2 2] [-2 2]]}
           #backend.messages.UnitRule
           {:name "Factory"
            :type :factory
            :speed 0
            :armor 10
            :cost 2000
            :build-time 5
            :built-by :commander
            :shape [[-10 -10] [10 -10] [10 10] [-10 10]]}]
   :player-states
   [#backend.messages.PlayerState
    {:player "player-1"
     :resources 2000
     :unit-states [#backend.messages.UnitState
                   {:id "p1-commander"
                    :type :commander
                    :position #backend.messages.Coordinates[20 12]
                    :action :idle
                    :action-coordinates nil}]
     :building-states [#backend.messages.BuildingState{:id "p1-b1" 
                                                       :type :factory
                                                       :position #backend.messages.Coordinates[10 15]
                                                       :action :idle
                                                       :action-args nil}]}
    #backend.messages.PlayerState
    {:player "player-2"
     :resources 1500
     :unit-states [#backend.messages.UnitState
                   {:id "p2-u1"
                    :type :commander
                    :position #backend.messages.Coordinates[160 200]
                    :action :idle
                    :action-coordinates nil}]
     :building-states [#backend.messages.BuildingState{:id "p2-b1" 
                                                       :type :factory
                                                       :position #backend.messages.Coordinates[100 150]
                                                       :action :idle
                                                       :action-args nil}]}]})


