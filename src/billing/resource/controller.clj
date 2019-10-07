(ns billing.resource.controller
  (:use clojure.pprint)
  (:require [billing.db :as db]
            [billing.contract.logic :refer [contracts-by-id]]
            [billing.rate.logic :refer [rates-by-resource-name find-price assoc-costs]]))

(defn- get-contracts-by-id [customer-name]
  (let [constracts (db/get-contracts-by-customer customer-name)]
    (contracts-by-id constracts)))

(defn- fn-charge-consumption [customer-name]
  (fn [consumption]
    (let [contracts (get-contracts-by-id customer-name)
          contract (get contracts (:contractId consumption))
          contract-rate (rates-by-resource-name (:rates contract))
          default-rate (db/get-default-rates-by-id (:defaultRateId contract))
          default-rate-resource (rates-by-resource-name (:rates default-rate))
          fn-find-price (partial find-price contract-rate default-rate-resource)]
      (update consumption :resources #(assoc-costs % fn-find-price)))))

(defn post-charge-resources [request]
  (let [consumptions (get-in request [:body :consumptions])
        customer-name (get-in request [:body :customerName])]
    {
     :customerName customer-name
     :consumptions (map (fn-charge-consumption customer-name) consumptions)}))
