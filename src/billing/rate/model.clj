(ns billing.rate.model
  (:require [schema.core :as s]))

(def default-rate "Others")
(def Rate {:name  s/Str
           :price BigDecimal
           :type  s/Str})
(def Rates [Rate])
