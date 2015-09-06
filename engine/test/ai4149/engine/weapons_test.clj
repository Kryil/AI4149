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
  (let [command (PlayerCommand. "player-1" "p1-commander" :fire [:main (Coordinates. 40 15)])
        updated-state (game/process-turn simple-test-state [command])
        p-state (find-player-state "player-1" updated-state)]
    (fact "projectile is listed in player-state"
      (:projectiles p-state) =not=> nil
      (count (:projectiles p-state)) => 1)))
; todo weapon projectile moves like units
; todo projectiles hit on any object in the path, including walls on the map
; todo damage is caused
; todo armor effects are included in damage calculation
; todo units are destroyed when damage is greater than remaining health (health is always 100 on healthy units)
; todo units without weapon can not shoot
