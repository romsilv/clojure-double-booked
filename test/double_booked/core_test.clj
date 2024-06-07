(ns double-booked.core-test
  (:require [clojure.test :refer :all]
            [double-booked.core :refer :all])
  (:import [java.time LocalDateTime]))

(deftest test-parse-date
  (testing "Valid date string"
    (let [date-str "2024-06-07T04:00:00"
          expected (LocalDateTime/parse date-str formatter)]
      (is (= expected (parse-date date-str)))))

  (testing "Null date string"
    (let [date-str nil]
      (is (thrown-with-msg? IllegalArgumentException #"Date string is null or empty"
                            (parse-date date-str)))))

  (testing "Empty date string"
    (let [date-str ""]
      (is (thrown-with-msg? IllegalArgumentException #"Date string is null or empty"
                            (parse-date date-str)))))

  (testing "Invalid date format"
    (let [date-str "2024-06-07"]
      (is (thrown-with-msg? IllegalArgumentException #"Invalid date format: 2024-06-07"
                            (parse-date date-str))))))


(deftest test-overlap
  (let [event1 {:start-book-time "2024-06-07T04:00:00" :end-book-date "2024-06-07T08:00:00"}
        event2 {:start-book-time "2024-06-07T06:00:00" :end-book-date "2024-06-07T09:00:00"}
        event3 {:start-book-time "2024-06-07T09:00:00" :end-book-date "2024-06-07T10:00:00"}]
    (testing "Events overlapping"
      (is (overlap? event1 event2)))
    (testing "Events not overlapping"
      (is (not (overlap? event1 event3))))))

(deftest test-find-overlapping-events
    (let [events [{:id "1", :start-book-time "2024-06-07T02:00:00", :end-book-date "2024-06-07T03:00:00"}
                  {:id "2", :start-book-time "2024-06-07T05:00:00", :end-book-date "2024-06-07T06:00:00"}
                  {:id "3", :start-book-time "2024-06-08T01:00:00", :end-book-date "2024-06-08T02:00:00"}
                  {:id "4", :start-book-time "2024-06-08T03:00:00", :end-book-date "2024-06-08T04:00:00"}
                  {:id "5", :start-book-time "2024-06-07T07:00:00", :end-book-date "2024-06-07T08:00:00"}]]
      (testing "Find overlapping events"
        (let [overlapping-events (find-overlapping-events events)]
          (is (= 0 (count overlapping-events)))))))

  (let [events-with-invalid [{:id "1", :start-book-time "2024-06-07T02:00:00", :end-book-date "2024-06-07T03:00:00"}
                             {:id "invalid1", :start-book-time "", :end-book-date "2024-06-07T05:00:00"}
                             {:id "invalid2", :start-book-time "2024-06-07T01:00:00", :end-book-date ""}
                             {:id "invalid3", :start-book-time "2024-06-07", :end-book-date "2024-06-07T05:00:00"}
                             {:id "invalid4", :start-book-time nil, :end-book-date "2024-06-07T05:00:00"}]]
    (testing "Skip invalid events"
      (let [overlapping-events (find-overlapping-events events-with-invalid)]
        (is (= 0 (count overlapping-events))))))
