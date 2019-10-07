(ns billing.resource.controller
  (:use clojure.pprint)
  (:require [billing.db :as db]
            [billing.resource.model :refer [ResourceConsumptionList]]))

(defn- rates-by-name [rates]
  (let [group-by-resource (fn [result rate] (assoc result (:resourceName rate) rate))]
    (reduce group-by-resource {} rates)))

(defn- get-default-rates [id]
  (let [default-rate-list (get (db/get-default-rates-by-id id) :rates)]
    (rates-by-name default-rate-list)))

(defn contracts-by-id [constracts]
  (let [fn-group-by-date (fn [result contract]
                           (assoc result (str (:_id contract)) contract))]
    (reduce fn-group-by-date {} constracts)))

(defn get-contracts [customer-name]
  (let [constracts (db/get-contracts-by-customer customer-name)
        contracts (contracts-by-id constracts)]
    (first (map (fn [[k v]] {k (assoc v :rateByResource (rates-by-name (:rates v)))}) contracts))))

(defn find-price [resource-name contract resource-price-default]
  (cond
    (get-in contract [:rateByResource resource-name]) (get-in contract [:rateByResource resource-name :price])
    (get resource-price-default resource-name) (get-in resource-price-default [resource-name :price])
    :else (get-in resource-price-default ["Others" :price])))

(defn assoc-cost [contracts]
  (fn [consumption]
    (let [contractId (:contractId consumption)
          contract (get contracts contractId)
          default-rate-resource (get-default-rates (:defaultRateId contract))
          fn-find-price #(find-price % contract default-rate-resource)
          fn-assoc-unit-price #(assoc % :unitPrice (fn-find-price (:name %)))
          fn-assoc-cost #(assoc % :cost (* (:unitPrice %) (:count %)))]
      (-> consumption
        (#(assoc % :resources (map fn-assoc-unit-price (:resources %))))
        (#(assoc % :resources (map fn-assoc-cost (:resources %))))))))

(defn post-charge-resources [request]
  (let [consumptions (get-in request [:body :consumptions])
        customer-name (get-in request [:body :customerName])
        contracts (get-contracts customer-name)]
    {
     :customerName customer-name
     :consumptions (map (assoc-cost contracts) consumptions)}))
