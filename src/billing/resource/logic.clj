(s/defn charge-resources [resource-consumption-list :- ResourceConsumptionList
                          default-resource-rate :- Rates
                          customer-constracts]

  )

;(defn- get-price [resource-price name]
;  (if-let [price (get resource-price name)]
;    price
;    (get resource-price r.model/default-rate)))
;
;(defn- get-price-to-pay [resource-price resource]
;  (let [resource-name (get resource :name)
;        counter (get resource :count)
;        unit-price (get-price resource-price resource-name)]
;    (* counter unit-price)))
;
;(defn- assoc-resource-price [result rate]
;  (assoc result (:name rate) (:price rate)))
;
;(defn- assoc-cost [resource resource-price]
;  (let [total-to-pay (get-price-to-pay resource-price resource)]
;    (assoc resource :cost (str (round4 total-to-pay)))))
;
;(defn- assoc-unit-price [resource resource-price]
;  (let [unit-price (get-price resource-price (get resource :name))]
;    (assoc resource :unitPrice (str (round4 unit-price)))))
;
;(defn- calculate-cost [resource-price resource-consumption-list]
;  (map (fn [r] (-> r
;                   (assoc-unit-price resource-price)))
;       resource-consumption-list))
;                   (assoc-cost resource-price)

(ns billing.resource.logic
  (:require [schema.core :as s]
            [billing.rate.model :as r.model]
            [billing.resource.model :refer [ResourceConsumptionList]]
            [clojure.tools.logging :as log]
            [billing.utils :refer [round4]]
            [billing.rate.model :refer [Rates]]))


  ;(let [resource-price (reduce assoc-resource-price {} rates)
  ;      charged-resources (calculate-cost resource-price resource-consumption-list)
  ;      fn-total-to-pay (fn [total r] (+ total (BigDecimal. (:cost r))))
  ;      total-to-pay (reduce fn-total-to-pay 0 charged-resources)]
  ;  (log/info "Resources computed" charged-resources)
  ;  {:body {:chargedResources charged-resources
  ;          :totalToPay       (str total-to-pay)}}))
