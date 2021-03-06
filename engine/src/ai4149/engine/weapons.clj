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
        upd-p-state (assoc-in p-state [:units (:id unit)] (if (> (:health upd-u-state) 0) 
                                                         upd-u-state
                                                         (assoc (assoc upd-u-state :action :dead)
                                                                :action-args (:shooter projectile))))]
      (assoc-in state [:players player-id] upd-p-state)))

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
  (reduce move-projectile (assoc state :projectiles []) (:projectiles state)))

(defn process-fire-command [state command]
  (let [player (:player command)
        unit-id (:target-id command)
        [weapon target-coords] (:action-args command)
        player-state (find-player-state player state)
        unit (find-unit-state unit-id player-state)]
    (cond 
      (nil? unit) (add-player-error state player command :unit-not-found) 
      (action= unit :dead) (add-player-error state player command :unit-dead)
      :else
      (let [weapon-rule (find-unit-weapon-rule (:type unit) weapon (:rules state))]
        (if (nil? weapon-rule)
          (add-player-error state player command :no-weapon)
          (let [projectile (Projectile. (:range weapon-rule)
                                        (:velocity weapon-rule)
                                        (:damage weapon-rule)
                                        (:position unit)
                                        target-coords
                                        (:id unit))]
            (add-to-list state :projectiles projectile)))))))

(defn process-fire-commands [state commands]
  (reduce process-fire-command state commands))
