(defproject overpitch "0.1.0-SNAPSHOT"
  :description "Pitch-scaling library for musical audio based on OverTone."
  :dependencies [
    [org.clojure/clojure "1.8.0"]
    [overtone "0.10.1"]
    [org.clojure/math.numeric-tower "0.0.4"]
  ]
  :main ^:skip-aot overpitch.core
  :jvm-opts ^:replace []
)
