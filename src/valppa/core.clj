(ns valppa.core
   (:require
            [clojure-csv.core :as csv]
            [semantic-csv.core :as sc :refer :all]))

(defn remove-blank-values-from-map [m]
  (filter (comp not clojure.string/blank? second) m))
(def defaults
  {:Tuotannossa "0"
   :hoponlopo "ASDFA"})



(defn foo
  "aloittelua"
  [x]
  (let [source (slurp "source.csv" )
        csv (csv/parse-csv source :delimiter \;)
        mappifyed (sc/mappify csv)
        _ (clojure.pprint/pprint mappifyed)
        renamed (map #(clojure.set/rename-keys %1 {(keyword "paiva") :date}) mappifyed)
        ;empty-vals-removed (map remove-blank-values-from-map renamed)
        merged (map #(merge defaults %1) renamed)
        vectorised (vectorize merged)
        new-csv (csv/write-csv vectorised :delimiter \;)
        ]
    (print new-csv)
    ))

(foo 1)