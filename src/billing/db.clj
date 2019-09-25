(ns billing.db
  (:require [monger.core :as mg]
            [monger.collection :as mc]))

(def connection (mg/connect))
(def db (mg/get-db connection "billing"))

(defn get-customers
  []
  (mc/find-maps db "customers"))
