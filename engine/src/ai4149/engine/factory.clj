(ns ai4149.engine.factory
  (:require [ai4149.messages :refer :all] 
            [ai4149.engine.helpers :refer :all] 
            [ai4149.engine.collisions :refer :all])
  (:import [ai4149.messages Coordinates]))

(defn process-factory [building-state]
  (if (and (type= building-state :factory) (action= building-state :constructing))
    (let [up-state (update-in building-state [:action-args 1] dec)]
      (if (<= (second (:action-args up-state)) 0)
        [(assoc (assoc up-state :action-args nil) :action :idle) (first (:action-args up-state))]
        [up-state nil]))
    [building-state nil]))

(defn process-player-factories [state player-state]
  (let [b-states (vals (:units player-state))
        units (:units player-state)
        updated-b-states (map process-factory b-states)
        updated-state (assoc player-state 
                             :units 
                             (into (sorted-map) 
                                   (map (fn [s] [(:id s) s]) 
                                        (map first updated-b-states))))]
    (assoc updated-state 
           :units 
           (into (:units updated-state)
                 (filter (complement nil?)
                          (map (fn [[b-state u-type]]
                                 (when-not (nil? u-type)
                                   (let [unit-rule (find-unit-rule u-type (:rules state))
                                         factory-area (get-unit-area b-state (:rules state))
                                         unit-size (get-unit-size {:type u-type} (:rules state))
                                         unit-place (first (filter (partial area-free? state) 
                                                                   (bordering-areas factory-area unit-size)))
                                         unit-coords (get-unit-coordinates unit-place unit-rule)
                                         uid (uuid)]
                                     [uid 
                                      (map->UnitState {:id uid
                                                       :type u-type
                                                       :position unit-coords
                                                       :action :new
                                                       :action-args nil})])))
                               updated-b-states))))))




(defn process-factories [state]
  (map-player-states (partial process-player-factories state) state))

(defn process-build-command [state command]
  (let [player (:player command)
        building-id (:target-id command)
        action (:action command)
        unit-type (:action-args command)
        p-state (find-player-state player state)
        unit-rule (find-unit-rule unit-type (:rules state))
        unit-cost (:cost unit-rule)
        unit-build-time (:build-time unit-rule)
        resources-left (if (nil? unit-cost) 0 (- (:resources p-state) unit-cost))
        up-p-state (assoc p-state :resources resources-left)
        b-state (find-unit-state building-id up-p-state)
        up-b-state (assoc (assoc b-state :action :constructing) :action-args [unit-type unit-build-time])
        up-p-state (assoc-in up-p-state [:units building-id] up-b-state)]
    (assoc-in state [:players player]
              (cond
                (or (nil? (:built-by unit-rule)) 
                    (nil? unit-cost)
                    (not= (:type b-state) (:built-by unit-rule)))
                (assoc p-state
                       :errors
                       (cons [command :can-not-build]
                             (:errors p-state)))
                (not= (:action b-state) :idle) (assoc p-state 
                                                      :errors 
                                                      (cons [command :building-in-progress] 
                                                            (:errors p-state)))
                (< (:resources up-p-state) 0) (assoc p-state 
                                                     :errors 
                                                     (cons [command :no-resources] 
                                                           (:errors p-state)))
                :else up-p-state))))



(defn process-build-commands [state commands]
  (reduce process-build-command state commands))



