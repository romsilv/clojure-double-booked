(ns helpers.data-generator 
  (:import [java.time LocalDateTime ZoneOffset ZonedDateTime]
           [java.time.format DateTimeFormatter]
           [java.util Random]))

(def formatter (DateTimeFormatter/ofPattern "yyyy-MM-dd'T'HH:mm:ss"))

(defrecord Event [id start-time end-time])

(defn generate-random-date [start end]
  (let [start-epoch (.toEpochSecond (ZonedDateTime/of start ZoneOffset/UTC))
        end-epoch (.toEpochSecond (ZonedDateTime/of end ZoneOffset/UTC))
        random-epoch (+ start-epoch (long (* (Math/random) (- end-epoch start-epoch))))]
    (LocalDateTime/ofEpochSecond (Math/min random-epoch end-epoch) 0 ZoneOffset/UTC)))

(defn format-date [date]
  (.format date formatter))

(defn generate-random-events [n start-date end-date]
  (let [start (LocalDateTime/parse start-date formatter)
        end (LocalDateTime/parse end-date formatter)
        random (Random.)]
    (for [i (range n)]
      (let [random-start (generate-random-date start end)
            duration (+ 3600 (long (* 7200 (.nextDouble random))))
            random-end (.plusSeconds random-start duration)]
        (->Event (str (java.util.UUID/randomUUID))
                 (format-date random-start)
                 (format-date random-end))))))