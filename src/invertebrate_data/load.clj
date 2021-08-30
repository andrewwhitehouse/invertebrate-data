(ns invertebrate-data.load
  (:require [clojure.java.io :as io]
            [dk.ative.docjure.spreadsheet :as sheet]
            [clojure.string :as str]
            [java-time.format :as fmt]
            [java-time :refer [local-date time-between]]
            [clojure.pprint :refer [pprint]]))

(defn load-sheet []
  (let [input-file (-> "invertebrates.xlsx" io/resource io/file)]
    (->> (.getPath input-file)
        sheet/load-workbook
        sheet/sheet-seq
        first
        (map sheet/cell-seq)
         (map #(map sheet/read-cell %)))))

(defn parse-date-and-note [content]
  (let [matched (re-matches
                           #"(\d{4}) (Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Sep|Oct|Nov|Dec).*? (\d+)(th|nd|rd|st)*.*"
                           (.toString content))]
    (when (nil? matched) (println content "not matched !"))
    (if matched
      (let [year (get matched 1)
            month (get matched 2)
            day (get matched 3)
            suffix (get matched 4)
            after-day (str/split content (re-pattern (str day suffix)))
            after-separator (when (> (count after-day) 1)
                              (re-matches #"[\.,]*\s*(.+)\s*" (second after-day)))]
        (merge
          (when after-separator
            (let [note (str/trim (get after-separator 1))]
              {:notes note}))
          {:most-recent-date
           (local-date "yyyy-MMM-d"
                       (str (get matched 1) "-" (get matched 2) "-" (get matched 3)))}))
      {:notes content})))

;; (parse-date-and-note "2003 August 23rd.  Common, hawks for food far from water")

(defn parse-row [row category]
  (let [v (vec row)]
  (apply merge
         {:linnean-name (str/trim (get v 1))}
         (when (get v 2) {:english-name (get v 2)})
         (when (get v 5) (parse-date-and-note (get v 5)))
         {:category category})))

(defn normalise [data]
  (loop [category ""
         remaining (rest data)
         collected []]
    (if-let [row (first remaining)]
      (if (and (nil? (first row)) (nil? (second row)))
        (recur category (rest remaining) collected)
        (if-let [possible-category (some->> (first row)
                                            (#(when (and (string? %) (not (str/blank? %))) %)))]
          (recur possible-category (rest remaining) collected)
          (if-let [parsed (parse-row row category)]
            (recur category (rest remaining) (conj collected parsed))
            (recur category (rest remaining) collected))))
      collected)))

(defn date-comparator [d1 d2]
    (cond
      (nil? d1) (if (nil? d2) 0 (time-between (local-date 1970 1 1) d2 :days))
      (nil? d2) (if (nil? d1) 0 (time-between d1 (local-date 1970 1 1) :days))
      :else (time-between d1 d2 :days)))

(defn load-data []
  (->> (load-sheet) normalise (sort-by :most-recent-date date-comparator)))

(defn -main [& args]
  (pprint (load-data)))
