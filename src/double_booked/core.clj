(ns double-booked.core
  (:require [clojure.string :as str]
            [helpers.data-generator :as dg])
  (:import [java.time LocalDateTime ZoneOffset ZonedDateTime]
           [java.time.format DateTimeFormatter]))

(def formatter (DateTimeFormatter/ofPattern "yyyy-MM-dd'T'HH:mm:ss"))

(defn parse-date [date-str]
  (cond
    (nil? date-str) (throw (IllegalArgumentException. "Date string is null"))
    (str/blank? date-str) (throw (IllegalArgumentException. "Date string is empty"))
    :else
    (try
      (LocalDateTime/parse date-str formatter)
      (catch Exception e
        (throw (IllegalArgumentException. (str "Invalid date format: " date-str)))))))

(defn overlap? [start1 end1 start2 end2]
  (or (and (not (.isAfter start1 end2)) (not (.isBefore end1 start2)))
      (and (not (.isAfter start2 end1)) (not (.isBefore end2 start1)))))

(defn find-overlapping-events [events n]
  (let [valid-events (filter (fn [event]
                               (try
                                 (parse-date (:start-time event))
                                 (parse-date (:end-time event))
                                 true
                                 (catch IllegalArgumentException e
                                   (println "Skipping invalid event:" (.getMessage e))
                                   false)))
                             events)
        events-with-times (map #(assoc % :start (parse-date (:start-time %))
                                       :end (parse-date (:end-time %)))
                               valid-events)
        sorted-events (sort-by :start events-with-times)
        overlaps (for [i (range (count sorted-events))
                       j (range (inc i) (count sorted-events))
                       :when (and (not= i j)
                                  (overlap? (:start (nth sorted-events i)) (:end (nth sorted-events i))
                                            (:start (nth sorted-events j)) (:end (nth sorted-events j))))]
                   [(-> (nth sorted-events i) :id) (-> (nth sorted-events j) :id)])]
    (take n overlaps)))

(defn -main []
  (let [events (dg/generate-random-events 100000 "2024-01-01T00:00:00" "2024-01-01T23:59:59")
        overlapping-events (find-overlapping-events events 10)]
    (println "Generated Overlapping events: " overlapping-events)
    (println "Count of Generated overlapping events: " (count overlapping-events))))