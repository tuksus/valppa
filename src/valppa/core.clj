(ns valppa.core
   (:require
            [clojure-csv.core :as csv]
            [semantic-csv.core :as sc :refer :all]))

(defn remove-blank-values-from-map [m]
  (into {} (filter (comp not clojure.string/blank? second) m)))

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
        converted (map #(convert-row schema %1) mappifyed)
        header (map #(keyword (:column %1)) schema)
        vectorised (vectorize {:header header} converted )
        new-csv (csv/write-csv vectorised :delimiter \;)]
    
      (spit target-file new-csv) 
    ))
(defn -main [& args]
  (convert "source.csv" "schema.csv" "target.csv")
  )

