(ns billing.compute-resource
  (:import (java.math RoundingMode)))

(defn bigdec? [v]
  (= BigDecimal (class v)))

(defn round [s]
  (fn [n]
    (assert (bigdec? n))
    (.setScale n s RoundingMode/HALF_EVEN)))

(def round4 (round 4))

(defn get-price [resource-price name]
  (if-let [price (get resource-price name)]
    price
    (get resource-price "Others")))

(defn get-price-to-pay [resource-price resource]
  (let [resource-name (get resource "name")
        counter (get resource "count")
        unit-price (get-price resource-price resource-name)]
    (* counter unit-price))
  )

(defn assoc-resource-price [result rate]
  (assoc result (:name rate) (:price rate)))

(defn assoc-total-to-pay [resource resource-price]
  (let
    [total-to-pay (get-price-to-pay resource-price resource)]
    (assoc resource :totalToPay (str (round4 total-to-pay)))))

(defn assoc-unit-price [resource resource-price]
  (let
    [unit-price (get-price resource-price (get resource "name"))]
    (assoc resource :unitPrice (str (round4 unit-price)))))

(defn map-resource-price [resource-price]
  (fn [resource] (-> resource
                     (assoc-total-to-pay resource-price)
                     (assoc-unit-price resource-price))))

(defn compute [resources rates]
  (let [resource-price (reduce assoc-resource-price {} rates)]
    (map (map-resource-price resource-price) resources))
  )
