(ns double-booked.core
  (:require [clojure.string :as str])
  (:import [java.time LocalDateTime]
           [java.time.format DateTimeFormatter]))

(def formatter (DateTimeFormatter/ofPattern "yyyy-MM-dd'T'HH:mm:ss"))

(defn parse-date [date-str]
  (cond
    (nil? date-str) (throw (IllegalArgumentException. "Date string is null"))
    (str/blank? date-str) (throw (IllegalArgumentException. "Date string empty"))
    :else
    (try
      (LocalDateTime/parse date-str formatter)
      (catch Exception e
        (throw (IllegalArgumentException. (str "Invalid date format: " date-str)))))))



(defn overlap? [event1 event2]
  (let [start1 (parse-date (:start-book-time event1))
        end1 (parse-date (:end-book-date event1))
        start2 (parse-date (:start-book-time event2))
        end2 (parse-date (:end-book-date event2))]
    (or (and (not (.isAfter start1 end2)) (not (.isBefore end1 start2)))
        (and (not (.isAfter start2 end1)) (not (.isBefore end2 start1))))))

(defn find-overlapping-events [events]
  (let [valid-events (filter (fn [event]
                               (try
                                 (parse-date (:start-book-time event))
                                 (parse-date (:end-book-date event))
                                 true
                                 (catch IllegalArgumentException e
                                   (println "Skipping invalid event:" (.getMessage e))
                                   false)))
                             events)
        events-with-times (map #(assoc % :start (parse-date (:start-book-time %))
                                       :end (parse-date (:end-book-date %)))
                               valid-events)
        sorted-events (sort-by :start events-with-times)]
    (loop [current-event (first sorted-events)
           remaining-events (rest sorted-events)
           overlaps []]
      (if (empty? remaining-events)
        overlaps
        (let [overlapping-events (filter #(overlap? current-event %) remaining-events)
              new-overlaps (map #(vector (:id current-event) (:id %)) overlapping-events)]
          (recur (first remaining-events) (rest remaining-events) (concat overlaps new-overlaps)))))))

(def events
  [{:id "1xc423", :start-book-time "2024-06-07T04:00:00", :end-book-date "2024-06-07T08:00:00"}
   {:id "2bv212", :start-book-time "2024-06-07T04:00:00", :end-book-date "2024-06-07T08:00:00"}
   {:id "3bd534", :start-book-time "2024-06-07T06:00:00", :end-book-date "2024-06-07T09:00:00"}
   {:id "4as213", :start-book-time "2024-06-07T01:00:00", :end-book-date "2024-06-07T05:00:00"}
   {:id "invalid1", :start-book-time "", :end-book-date "2024-06-07T05:00:00"}
   {:id "invalid2", :start-book-time "2024-06-07T01:00:00", :end-book-date ""}
   {:id "invalid3", :start-book-time "2024-06-07", :end-book-date "2024-06-07T05:00:00"}
   {:id "invalid4", :start-book-time nil, :end-book-date "2024-06-07T05:00:00"}])

(defn -main []
  (let [overlapping-events (find-overlapping-events events)]
    (println "Overlapping events: " overlapping-events)
    (println "Count of overlapping events: " (count overlapping-events))))
