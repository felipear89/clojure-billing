(ns billing.utils
  (:import (java.math RoundingMode)
           (java.time ZoneOffset)
           (java.util Date)))

(defn bigdec? [v]
  (= BigDecimal (class v)))

(defn round [s]
  (fn [n]
    (assert (bigdec? n))
    (.setScale n s RoundingMode/HALF_EVEN)))

(def round4 (round 4))

(defn utc-date-time [^Date date]
  (.atZone (.toInstant date) ZoneOffset/UTC))
