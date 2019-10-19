(ns billing.contract.controller-json
  (:require [schema.core :as s]
            [schema.coerce :as coerce])
  (:import (java.time Instant)))

(def PosInt (s/pred pos-int? 'integer-positivo))
(def Money (s/pred decimal? 'money))

(def Rate {:resourceName s/Str
           :price        Money
           })

(def Rates [Rate])

(def DefaultRates {:name s/Str
                   :rates Rates})

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
(def money-regex #"\d*\.\d{4}")

(defn datetime-matcher [schema]
  (when (= Instant schema)
    (coerce/safe
      (fn [x]
        (if (and (string? x) (re-matches datetime-regex x))
          (Instant/parse x)
          x)))))

(defn decimal-matcher [schema]
  (when (= Money schema)
    (coerce/safe
      (fn [x]
        (if (and (string? x) (re-matches money-regex x))
          (BigDecimal. (str x))
          x)))))

(def matchers [decimal-matcher datetime-matcher coerce/json-coercion-matcher])

(def contract-matcher
  (coerce/first-matcher matchers))

(def default-rate-matcher
  (coerce/first-matcher matchers))
