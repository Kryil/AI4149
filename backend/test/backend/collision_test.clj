(ns backend.collision-test
  (:require [midje.sweet :refer :all]
            [backend.messages :refer :all]
            [backend.game :as game]
            [backend.collisions :as collisions]))

(facts "shape conversion"
  (let [origo #backend.messages.Coordinates[50 50]
        rectangle-shape [[10 -5] [10 5] [-10 5] [-10 -5]]]
    (fact "coordinates and shape is converted to area"
      (collisions/shape->area rectangle-shape origo) => [[60 45] [60 55] [40 55] [40 45]])))

(facts "intersects"
  (fact "coordinates intersect with object"
    (collisions/intersects? [[60 45] [60 55] [40 55] [40 45]] #backend.messages.Coordinates[50 50]) => truthy
    (collisions/intersects? [[60 45] [60 55] [40 55] [40 45]] #backend.messages.Coordinates[60 55]) => truthy
    (collisions/intersects? [[60 45] [60 55] [40 55] [40 45]] #backend.messages.Coordinates[60 50]) => truthy)
  (fact "coordinates do not intersect with object"
    (collisions/intersects? [[60 45] [60 55] [40 55] [40 45]] #backend.messages.Coordinates[10 10]) => falsey
    (collisions/intersects? [[60 45] [60 55] [40 55] [40 45]] #backend.messages.Coordinates[60 0]) => falsey
    (collisions/intersects? [[60 45] [60 55] [40 55] [40 45]] #backend.messages.Coordinates[61 50]) => falsey))
 

