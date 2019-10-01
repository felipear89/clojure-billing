(ns billing.utils
  (:import (java.math RoundingMode)))

(defn bigdec? [v]
  (= BigDecimal (class v)))

(defn round [s]
  (fn [n]
    (assert (bigdec? n))
    (.setScale n s RoundingMode/HALF_EVEN)))

(def round4 (round 4))
