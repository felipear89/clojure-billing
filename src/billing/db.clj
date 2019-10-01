(ns billing.db
  (:require [monger.core :as mg]
            [monger.collection :as mc])
  (:import (java.time LocalDateTime ZoneOffset)))

(def now (LocalDateTime/now ZoneOffset/UTC))

(def database-name "billing")
(def connection (mg/connect))
(def db (mg/get-db connection database-name))

(def coll_customers "customers")
(def coll_default_rates "default_rates")

; Queries
(defn get-customers []
  (mc/find-maps db coll_customers))

(defn get-default-rates []
  (mc/find-maps db coll_default_rates))

(defn get-default-rates-now []
  (first (mc/find-maps db coll_default_rates {:start_date {"$lte" now}
                                              :end_date   {"$gt" now}
                                              })))
