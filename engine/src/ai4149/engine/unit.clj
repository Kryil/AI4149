(ns ai4149.engine.unit
  (:require [ai4149.messages :refer :all] 
            [ai4149.engine.helpers :refer :all] 
            [ai4149.engine.collisions :refer :all]
            [ai4149.engine.vectors :as vectors])
  (:import [ai4149.messages Coordinates]))


(defn move-unit 
  ([state unit] (move-unit state unit (:action-args unit)))
  ([state unit coordinates]
   (let [unit-rule (find-unit-rule (:type unit) (:rules state))
         speed (:speed unit-rule)
         position (:position unit)
         [new-position remaining-moves] (vectors/calculate-next-position speed coordinates position)
         new-area (shape->area (:shape unit-rule) new-position)]
     (cond
       (not (area-free? state new-area [(:id unit)])) (assoc (assoc unit :action :obstructed)
                                                             :action-args remaining-moves)
       :else (assoc (assoc (assoc unit :action (if (empty? remaining-moves) :idle :moving)) 
                           :position new-position)
                    :action-args remaining-moves)))))

(defn process-unit-move-command [state p-state unit-state command]
  (let [unit-id (:id unit-state)
        coordinates (:action-args command)]
    (move-unit state unit-state coordinates)))

(defn process-commands-for-unit [state player-state unit-state commands]
  (reduce (fn [u-state cmd] (process-unit-move-command state player-state u-state cmd))
          unit-state
          (filter (fn [cmd] (= (:action cmd) :move)) commands)))

(defn process-player-units [state commands player-state]
  (let [p-commands (filter (fn [cmd] (= (:player cmd) (:player player-state))) commands)]
    (map-player-unit-states 
      (fn [u-state]
        (let [u-commands (filter (fn [cmd] (= (:target-id cmd) (:id u-state))) p-commands)]
          (cond
            (not-empty u-commands) (process-commands-for-unit state player-state u-state u-commands)
            (action= u-state :new) (assoc u-state :action :idle)
            (action= u-state :moving) (move-unit state u-state)
            (action= u-state :obstructed) (move-unit state u-state)
            (action= u-state :dead) nil
            :else u-state)))
      player-state)))


(defn process-units [state commands]
  (map-player-states (partial process-player-units state commands) state))

