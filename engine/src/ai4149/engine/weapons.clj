(ns ai4149.engine.weapons
  (:require [ai4149.messages :refer :all] 
            [ai4149.engine.helpers :refer :all] 
            [ai4149.engine.collisions :refer :all]
            [ai4149.engine.vectors :as vectors])
  (:import [ai4149.messages Coordinates]
           [ai4149.messages Projectile]))

(defn hit-damage [state [player-id unit] projectile]
  (let [p-state (find-player-state player-id state)
        upd-u-state (update-in unit [:health] #(- % (:damage projectile)))
        upd-p-state (update-state p-state :unit-states (if (> (:health upd-u-state) 0) 
                                                         upd-u-state
                                                         (assoc (assoc upd-u-state :action :dead)
                                                                :action-args (:shooter projectile))))]
      (update-state state :player-states upd-p-state :player)))

(defn move-projectile [state projectile]
  (let [[new-position remaining-moves] (vectors/calculate-next-position (:velocity projectile)
                                                                        [(:target projectile)]
                                                                        (:position projectile))
        new-range (- (:range projectile) (:velocity projectile))
        unit-hit (nearest-between-coords state (:position projectile) new-position [(:shooter projectile)])]

    (cond (not (nil? (first unit-hit))) (hit-damage state unit-hit projectile)
          (> new-range 0) (update-in state [:projectiles] 
                                     (partial cons 
                                              (assoc (assoc projectile :position new-position)
                                                     :range new-range)))
          :else state)))

(defn move-projectiles [state]
  (reduce move-projectile (assoc state :projectiles []) (:projectiles state))

  #_(let [moved-projectiles (filter (complement nil?) (map (partial move-projectile state) (:projectiles state)))]
    (assoc state :projectiles moved-projectiles)))

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
                                  target-coords
                                  (:id unit))]
      (add-to-state state :projectiles projectile))))

(defn process-fire-commands [state commands]
  (reduce process-fire-command state commands))
