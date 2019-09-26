(ns billing.db
  (:require [monger.core :as mg]
            [monger.collection :as mc])
  (:import (java.time LocalDateTime ZoneOffset)))

(def connection (mg/connect))
(def db (mg/get-db connection "billing"))

(def now (LocalDateTime/now ZoneOffset/UTC))

(defn get-customers
  []
  (mc/find-maps db "customers"))

(defn get-default-rates
  []
  (mc/find-maps db "default_rates"))

(defn get-default-rates-now
  []
  (first (mc/find-maps db "default_rates" {:start_date { "$lte" now }
                                    :end_date   { "$gt" now }
                                    })))
