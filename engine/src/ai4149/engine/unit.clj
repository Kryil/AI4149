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

(defn process-collect-command [state player-state unit-state]
  (let [unit-rule (find-unit-rule (:type unit-state) (:rules state))
        collect-amount (get-in unit-rule [:actions :collect])
        unit-position (:position unit-state)
        resource (some #(when (= (:position %) unit-position) %) (get-in state [:map :resources]))
        subtracted-resource (update-in resource [:amount] #(- % collect-amount))
        collected-amount (if (< (:amount resource) collect-amount) (:amount resource) collect-amount)]
    (if (and (not (nil? resource)) (not (nil? collect-amount)))
      (assoc 
        (update-in state [:players (:player player-state) :resources] #(+ % collected-amount))
        :map
        (if (> (:amount subtracted-resource) 0)
          (update-list (:map state) :resources subtracted-resource :position)
          (remove-from-list (:map state) :resources :position (:position subtracted-resource))))
      state)))


(defn process-command-for-unit [state player-state unit-state command]
  (cond
    (action= command :collect) (process-collect-command state player-state unit-state)
    :else (assoc-in 
            state 
            [:players (:player player-state) :units (:id unit-state)]
            (cond
              (action= command :move) (process-unit-move-command state player-state unit-state command)
              :else unit-state))))

(defn process-player-units [state processed-units player-state]
  (map-player-unit-states 
    (fn [u-state]
      (if (some #(= (:id %) (:id u-state)) processed-units)
        u-state
        (cond
          (action= u-state :new) (assoc u-state :action :idle)
          (action= u-state :moving) (move-unit state u-state)
          (action= u-state :obstructed) (move-unit state u-state)
          (action= u-state :dead) nil
          :else u-state)))
    player-state))

(defn- process-commands [state commands]
  (reduce (fn [[game-state units] command]
            (let [player-state (find-player-state (:player command) state)
                  unit-state (find-unit-state (:target-id command) player-state)]
              (if (not (nil? unit-state)) ; (some #(action= unit-state %) [:move :collect]))
                [(process-command-for-unit game-state player-state unit-state command) (cons unit-state units)]
                [game-state units])))
          [state []] 
          commands))

(defn process-units [state commands]
  (let [[processed-state processed-units] (process-commands state commands)]
    (map-player-states (partial process-player-units processed-state processed-units) processed-state)))


