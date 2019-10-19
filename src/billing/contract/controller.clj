(ns billing.contract.controller
  (:require [schema.core :as s]
            [schema-tools.core :as st]
            [billing.utils :refer [coerce-and-validate]]
            [billing.db :as db]
            [billing.contract.logic :refer [group-contracts-by-id group-rates-by-resource-name find-price assoc-costs]]
            [billing.contract.controller-json :refer [ChargeResourcesRequest ContractRequest contract-matcher
                                                      DefaultRates default-rate-matcher]]))

(defn- get-contracts-by-id [customer-name]
  (let [constracts (db/get-contracts-by-customer customer-name)]
    (group-contracts-by-id constracts)))

(defn- fn-charge-consumption [customer-name]
  (fn [consumption]
    (let [contracts (get-contracts-by-id customer-name)
          contract (get contracts (:contractId consumption))
          contract-resources-rate (group-rates-by-resource-name (:rates contract))
          default-rate (db/get-default-rates-by-id (:defaultRateId contract))
          default-resources-rate (group-rates-by-resource-name (:rates default-rate))
          fn-find-price (partial find-price contract-resources-rate default-resources-rate)]
      (update consumption :resources #(assoc-costs % fn-find-price)))))

(defn post-charge-resources [charge-resources]
  (let [charge-resources (st/select-schema charge-resources ChargeResourcesRequest)
        charge-resources (s/validate ChargeResourcesRequest charge-resources)
        consumptions (get charge-resources :consumptions)
        customer-name (get charge-resources :customerName)]
    {
     :customerName customer-name
     :consumptions (map (fn-charge-consumption customer-name) consumptions)}))

(defn assoc-id [id model]
  (if id (assoc model :_id id) model))

(defn save-contract [contract & [id]]
  (let [contract (coerce-and-validate ContractRequest contract-matcher contract)
        contract (assoc-id id contract)]
    (db/save-contract contract)))

(defn save-default-rate [default-rates & [id]]
  (let [default-rates (coerce-and-validate DefaultRates default-rate-matcher default-rates)
        default-rates (assoc-id id default-rates)]
    (db/save-default-rates default-rates)))

