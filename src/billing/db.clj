(ns billing.db
  (:require [monger.core :as mg]
            [monger.collection :as mc]
            [monger.conversion :refer :all])
  (:import (java.time LocalDateTime ZoneOffset ZonedDateTime ZoneId)
           (java.util Date)
           (org.bson.types ObjectId)))

; Convert to Date from LocalDateTime
(extend-protocol ConvertToDBObject
  LocalDateTime
  (to-db-object [^LocalDateTime input]
    (to-db-object (Date/from (.toInstant input ZoneOffset/UTC))))
  ZonedDateTime
  (to-db-object [^ZonedDateTime input]
    (to-db-object (Date/from (.toInstant input)))))

; Convert to LocalDateTime from Date
(extend-protocol ConvertFromDBObject
  Date
  (from-db-object [^Date input keywordize]
    (LocalDateTime/ofInstant (.toInstant input) ZoneOffset/UTC)))

(def now (LocalDateTime/now ZoneOffset/UTC))
(def database-name "billing")
(def connection (mg/connect))
(def db (mg/get-db connection database-name))

(def coll-contracts "contracts")
(def coll-default-rates "default_rates")

(defn save-contract [contract]
  (mc/insert-and-return db coll-contracts contract))

; Queries
(defn get-contracts []
  (mc/find-maps db coll-contracts))

(defn get-default-rates []
  (mc/find-maps db coll-default-rates))

(defn get-default-rates-by-id [id]
  (mc/find-one-as-map db coll-default-rates {:_id (ObjectId. id)}))

(defn get-contracts-by-customer [^String name]
  (mc/find-maps db coll-contracts {:customerName name}))
