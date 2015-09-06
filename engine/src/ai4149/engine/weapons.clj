(ns ai4149.engine.weapons
  (:require [ai4149.messages :refer :all] 
            [ai4149.engine.helpers :refer :all] 
            [ai4149.engine.collisions :refer :all]
            [ai4149.engine.vectors :as vectors])
  (:import [ai4149.messages Coordinates]
           [ai4149.messages Projectile]))

(defn move-projectile [])

(defn process-fire-command [state command]
  (let [player (:player command)
        unit-id (:target-id command)
        [weapon target-coords] (:action-args command)
        player-state (find-player-state player state)
        unit (find-unit-state unit-id player-state)
        weapon-rule (find-unit-weapon-rule (:type unit) weapon (:rules state))]
    ; todo if weapon rule is nil then error
    (let [projectile (Projectile. (:range weapon-rule)
                                  (:velocity weapon-rule)
                                  (:damage weapon-rule)
                                  (:position unit)
                                  target-coords)
          up-player-state (assoc player-state :projectiles (cons projectile (:projectiles player-state)))]
      (update-state state :player-states up-player-state :player))))

(defn process-fire-commands [state commands]
  (reduce process-fire-command state commands))