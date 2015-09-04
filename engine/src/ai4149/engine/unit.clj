(ns ai4149.engine.unit
  (:require [ai4149.messages :refer :all] 
            [ai4149.engine.helpers :refer :all] 
            [ai4149.engine.collisions :refer :all]))

(defn process-player-units [player-state]
  (map-player-unit-states 
      #(on-action % :new (assoc % :action :idle))
      player-state))




(defn process-units [state]
  (map-player-states process-player-units state))



 
