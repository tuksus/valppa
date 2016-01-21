(ns valppa.core
   (:require
            [clojure-csv.core :as csv]
            [semantic-csv.core :as sc :refer :all]))

(defn remove-blank-values-from-map [m]
  (into {} (filter (comp not clojure.string/blank? second) m)))

(def defaults
  {:Tuotannossa "0"
   :hoponlopo "ASDFA"})

(defn get-schema [file]
  (let [schema (rest (csv/parse-csv (slurp file) :delimiter \;))]
    (map #(remove-blank-values-from-map (zipmap [:column :source-column :default :type] %1)) schema))
  )

(defn convert-column [col source-r] 
  {(keyword (:column col))
   (or (when (:source-column col) 
         ((keyword(:source-column col)) source-r)) (:default col))} )

(defn convert-row [schema r]
  (let [result (map #(convert-column %1 r) schema)]
    (into {} result)))

(defn convert [source-file schema-file target-file]
  (let [source (slurp source-file)
        csv (csv/parse-csv source :delimiter \;)
        mappifyed (sc/mappify csv)
        schema (get-schema schema-file)
        converted (map #(convert-row schema %1) mappifyed)]
    converted
    ))


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

(convert "source.csv" "schema.csv" "target.csv")