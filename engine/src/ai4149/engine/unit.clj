(ns ai4149.engine.unit
  (:require [ai4149.messages :refer :all] 
            [ai4149.engine.helpers :refer :all] 
            [ai4149.engine.collisions :refer :all]
            [ai4149.engine.vectors :as vectors])
  (:import [ai4149.messages Coordinates]))


(defn- calculate-next-position [speed coordinates position]
  (let [requested-move (vectors/distance position (first coordinates))
        requested-magnitude (vectors/magnitude requested-move)
        move (vectors/limit requested-move speed)
        next-position (vectors/add position move)]
    (if (< requested-magnitude speed)
      (calculate-next-position (- speed requested-magnitude)
                               (rest coordinates)
                               next-position)
      [next-position (if (= requested-magnitude speed) nil coordinates)])))
     

(defn move-unit 
  ([state unit] (move-unit state unit (:action-args unit)))
  ([state unit coordinates]
   (let [unit-rule (find-unit-rule (:type unit) (:rules state))
         speed (:speed unit-rule)
         position (:position unit)
         [new-position remaining-moves] (calculate-next-position speed coordinates position)
         new-area (shape->area (:shape unit-rule) new-position)]
     (cond
       (not (area-free? state new-area [(:id unit)])) (assoc (assoc unit :action :obstructed)
                                                             :action-args remaining-moves)
       :else (assoc (assoc (assoc unit :action (if (empty? remaining-moves) :idle :moving)) 
                           :position new-position)
                    :action-args remaining-moves)))))


(defn process-player-units [state player-state]
  (map-player-unit-states 
    (fn [u-state]
      (cond
        (action= u-state :new) (assoc u-state :action :idle)
        (action= u-state :moving) (move-unit state u-state)
        :else u-state))
    player-state))


(defn process-units [state]
  (map-player-states (partial process-player-units state) state))


(defn process-move-command [state command]
  (let [player (:player command)
        unit-id (:target-id command)
        coordinates (:action-args command)
        p-state (find-player-state player state)
        unit-state (find-unit-state unit-id p-state)
        unit-rule (find-unit-rule (:type unit-state) (:rules state))
        up-unit-state (move-unit state unit-state coordinates)
        up-p-state (assoc p-state :unit-states 
                          (cons up-unit-state 
                                (filter (fn [us] (not= (:id us) unit-id)) (:unit-states p-state))))]
    (assoc state
           :player-states
           (cons
             up-p-state
             (filter (fn [ps] (not= (:player ps) player)) (:player-states state))))))


(defn process-move-commands [state move-commands]
  (reduce process-move-command state move-commands))




