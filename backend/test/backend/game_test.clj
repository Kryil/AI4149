(ns backend.game_test
  (:require [midje.sweet :refer :all]
            [backend.messages :refer :all]
            [backend.game :refer :all]))

(def test-initial-state
  #backend.messages.GameState{
                              :turn 0 
                              :turns 100
                              :unit-states [#backend.messages.UnitState{:position [1 2]
                                                                        :action "idle"
                                                                        :action-coordinates nil}]
                              :building-states [#backend.messages.BuildingState{:action "idle"}]
                              :things []})

(facts "turns process as planned"
  (fact "turn counter is increased"
    (:turn (process-turn test-initial-state [])) => 1))


