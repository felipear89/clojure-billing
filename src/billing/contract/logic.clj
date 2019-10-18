(ns billing.contract.logic)

(defn group-contracts-by-id [constracts]
  (let [fn-group-by-id (fn [result contract]
                           (assoc result (str (:_id contract)) contract))]
    (reduce fn-group-by-id {} constracts)))

(defn group-rates-by-resource-name [rates]
  (let [fn-group-by-name (fn [result rate]
                           (assoc result (str (:resourceName rate)) rate))]
    (reduce fn-group-by-name {} rates)))

(defn find-price [contract-rate default-rate-resource resource-name]
  (cond
    (get contract-rate resource-name) (get-in contract-rate [resource-name :price])
    (get default-rate-resource resource-name) (get-in default-rate-resource [resource-name :price])
    :else (get-in default-rate-resource ["Others" :price])))

(defn assoc-costs [resource fn-find-price]
  (let [fn-assoc-unit-price #(assoc % :unitPrice (fn-find-price (:name %)))
        fn-assoc-cost #(assoc % :cost (* (:unitPrice %) (:count %)))]
    (->> resource
         (map fn-assoc-unit-price)
         (map fn-assoc-cost))))
