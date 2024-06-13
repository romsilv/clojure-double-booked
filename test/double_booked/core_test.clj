(ns double-booked.core-test
  (:require [clojure.test :refer :all]
            [double-booked.core :refer [find-overlapping-events]]
            [double-booked.core :refer [parse-date]]
           ))

(deftest test-parse-date-invalid
  (testing "Parsing invalid date strings"
    (is (= (parse-date "2024-01-01T12:00:00") (java.time.LocalDateTime/of 2024 1 1 12 0 0 0)))))

(def mock-events
  [{:id 1 :start-time "2024-01-01T09:00:00" :end-time "2024-01-01T10:00:00"}
   {:id 2 :start-time "2024-01-01T09:30:00" :end-time "2024-01-01T11:00:00"}
   {:id 3 :start-time "2024-01-01T10:30:00" :end-time "2024-01-01T12:00:00"}
   {:id 4 :start-time "2024-01-01T13:00:00" :end-time "2024-01-01T14:00:00"}
   {:id 5 :start-time "2024-01-01T14:30:00" :end-time "2024-01-01T15:30:00"}])

(deftest test-find-overlapping-events
  (testing "Finding overlapping events"
    (let [overlapping (find-overlapping-events mock-events 10)]
      (is (= 2 (count overlapping)))
      (is (every? #(and (contains? (set (map :id mock-events)) (first %))
                        (contains? (set (map :id mock-events)) (second %)))
                  overlapping)))))

(deftest test-find-overlapping-events-empty
  (testing "No overlapping events in empty input"
    (is (empty? (find-overlapping-events [] 10)))))

(deftest test-find-overlapping-events-no-overlap
  (testing "No overlapping events when none exist"
    (let [no-overlap-events [{:id 1 :start-time "2024-01-01T09:00:00" :end-time "2024-01-01T10:00:00"}
                             {:id 2 :start-time "2024-01-01T11:00:00" :end-time "2024-01-01T12:00:00"}]]
      (is (empty? (find-overlapping-events no-overlap-events 10))))))

(deftest test-find-overlapping-events-single-event
  (testing "No overlapping events with single event"
    (let [single-event [{:id 1 :start-time "2024-01-01T09:00:00" :end-time "2024-01-01T10:00:00"}]]
      (is (empty? (find-overlapping-events single-event 10))))))
