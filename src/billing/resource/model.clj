(ns billing.resource.model
  (:require [schema.core :as s]))

(def PosInt (s/pred pos-int? 'inteiro-positivo))

(def ResourceConsumption {:name  s/Str
                          :count PosInt})

(def Consumption {:contractId s/Str
                  :resources [ResourceConsumption]})

(def Consumptions [Consumption])

(def ChargeResources {:customerName s/Str
                      :consumptions Consumptions})

