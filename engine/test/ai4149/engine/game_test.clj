(ns ai4149.engine.game_test
  (:require [midje.sweet :refer :all]
            [ai4149.messages :refer :all]
            [ai4149.engine.helpers :refer :all]
            [ai4149.engine.collisions :as collisions]
            [ai4149.engine.game :as game]
            [ai4149.game-test-data :refer [simple-test-state]])
  (:import [ai4149.messages PlayerCommand]
           [ai4149.messages Coordinates]))

(fact "turn counter is increased"
  (:turn (game/process-turn simple-test-state [])) => 1
  (:turn (reduce (fn [state n] (game/process-turn state [])) simple-test-state (range 3))) => 3)


(facts "game end situations"
  (fact "turns run out"
    (let [reduced-turns-state (assoc simple-test-state :turns 10)
          end-state (reduce (fn [state _] (game/process-turn state [])) reduced-turns-state (range 11))]
      (:winner end-state) =not=> nil
      (:winner end-state) => ["player-2" :value 4100 {"player-1" 4000}]))
  (fact "a commander dies"
    (let [nearly-dead-commander-state (assoc-in simple-test-state [:players "player-1" :units "p1-commander" :health] 10)
          fire-command (PlayerCommand. "player-2" "p2-tank-1" :fire [:main (Coordinates. 30 12)])
          end-state (game/process-turn nearly-dead-commander-state [fire-command])]
      (:winner end-state) => ["player-2" :last-man-standing])))
