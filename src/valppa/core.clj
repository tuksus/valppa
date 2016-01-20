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
  (let [asiakkaat (slurp "resources/test.csv" )
          csv (csv/parse-csv asiakkaat :delimiter \;)
          mappifyed (sc/mappify csv)
          renamed (map #(clojure.set/rename-keys %1 {(keyword "Käyttöönottokoulutus pvm") :kasg}) mappifyed)
          ;empty-vals-removed (map remove-blank-values-from-map renamed)
          merged (map #(merge defaults %1) renamed)
          vectorised (vectorize merged)
          new-csv (csv/write-csv vectorised :delimiter \;)
          ]
    (clojure.pprint/pprint new-csv)
      ))
