(ns ai4149.engine.weapons_test
  (:require [midje.sweet :refer :all]
            [ai4149.messages :refer :all]
            [ai4149.engine.helpers :refer :all]
            [ai4149.engine.collisions :as collisions]
            [ai4149.engine.game :as game]
            [ai4149.game-test-data :refer [simple-test-state]])
  (:import [ai4149.messages Coordinates]
           [ai4149.messages PlayerCommand]))

(facts "firing a weapon"
  (let [command (PlayerCommand. "player-1" "p1-commander" :fire [:main (Coordinates. 90 12)])
        updated-state (game/process-turn simple-test-state [command])]
    (fact "projectile is listed in player-state"
      (:projectiles updated-state) =not=> nil
      (count (:projectiles updated-state)) => 1)
    (fact "projectile moves"
      (:position (first (:projectiles updated-state))) => (Coordinates. 55 12)
      (:target (first (:projectiles updated-state))) => (Coordinates. 90 12)
      (:range (first (:projectiles updated-state))) => 25)
    (let [next-state (game/process-turn updated-state [])]
      (fact "projectile was removed from the list when range was reached"
        (count (:projectiles next-state)) => 0))))

(facts "hitting a target" 
  (let [hit-command (PlayerCommand. "player-1" "p1-commander" :fire [:main (Coordinates. 49 36)])
        updated-state (game/process-turn simple-test-state [hit-command])
        p2-state (find-player-state "player-2" updated-state)
        p2-tank-state (find-unit-state "p2-tank-1" p2-state)]

    (fact "projectile hits the tank"
      (:health p2-tank-state) => 80)

    (let [repeated-fire (reduce (fn [state _] (game/process-turn state [hit-command])) simple-test-state (range 5))
          p2-state (find-player-state "player-2" repeated-fire)]
      (fact "unit is marked as dead on the turn it dies"
        (let [unit (find-unit-state "p2-tank-1" p2-state)]
          (:action unit) => :dead
          (:action-args unit) => "p1-commander"))
      (fact "dead units are removed from the field"
        (let [next-state (game/process-turn repeated-fire [])
              p2-state (find-player-state "player-2" next-state)]
          (find-unit-state "p2-tank-1" p2-state) => nil
          (count (:unit-states p2-state)) => 2)))))


; todo projectiles hit on any object in the path, including walls on the map
; todo armor effects are included in damage calculation
; todo units without weapon can not shoot
; todo test that buildings can die
