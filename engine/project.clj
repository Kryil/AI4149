(defproject ai4149.engine "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [org.clojure/data.json "0.2.6"]
                 [org.clojure/math.numeric-tower "0.0.4"]]
  :main ^:skip-aot ai4149.engine.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}
             :dev {:dependencies [[midje "1.7.0"]]
                   :plugins [[lein-midje "3.1.3"]]}}
  :global-vars {*warn-on-reflection* true})
             
