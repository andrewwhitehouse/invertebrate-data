(ns invertebrate-data.core
  (:require [invertebrate-data.load :as load]
            [java-time.format :as fmt]
            [java-time :refer [zoned-date-time]]
            [clojure.java.io :as io]
            [clojure.data.csv :as csv]))

(defn -main [& args]
  (let [data (load/load-data)
        header ["Category" "Linnean Name", "English Name", "Most Recent Date", "Notes"]
        out-file (str (fmt/format "yyyyMMddHHmmss" (zoned-date-time)) ".csv")]
    (println "Writing" out-file)
    (with-open [writer (io/writer out-file)]
      (csv/write-csv writer
        (concat
          [header]
          (map (juxt :category :linnean-name :english-name :most-recent-date :notes) data))))))
