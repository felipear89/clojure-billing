(ns billing.utils
  (:require [schema.utils :as s-utils]
            [schema.coerce :as coerce])
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

(defn coerce-and-validate [schema matcher data]
  (let [coercer (coerce/coercer schema matcher)
        result  (coercer data)]
    (if (s-utils/error? result)
      (throw (Exception. (format "Value does not match schema: %s"
                                 (s-utils/error-val result))))
      result)))
