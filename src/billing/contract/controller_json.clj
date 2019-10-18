(ns billing.contract.controller-json
  (:require [schema.core :as s]
            [schema.utils :as s-utils]
            [schema.coerce :as coerce])
  (:import (java.time Instant)))

(def PosInt (s/pred pos-int? 'inteiro-positivo))

(def Rate {:resourceName s/Str
           :price        BigDecimal
           })

(def Rates [Rate])

(def ContractRequest {:customerName  s/Str
                      :startDate     Instant
                      :endDate       Instant
                      :rates         Rates
                      :defaultRateId s/Str
                      })

(def ResourceConsumption {:name  s/Str
                          :count PosInt})

(def Consumption {:contractId s/Str
                  :resources  [ResourceConsumption]})

(def Consumptions [Consumption])

(def ChargeResourcesRequest {:customerName s/Str
                             :consumptions Consumptions})

(def datetime-regex #"\d{4}-\d{2}-\d{2}T\d{2}:\d{2}:\d{2}\.\d{3}Z")

(defn datetime-matcher [schema]
  (when (= Instant schema)
    (coerce/safe
      (fn [x]
        (if (and (string? x) (re-matches datetime-regex x))
          (Instant/parse x)
          x)))))

(def contract-matcher
  (coerce/first-matcher [datetime-matcher coerce/json-coercion-matcher]))
