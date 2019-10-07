(ns billing.contract.logic)

(defn contracts-by-id [constracts]
  (let [fn-group-by-id (fn [result contract]
                           (assoc result (str (:_id contract)) contract))]
    (reduce fn-group-by-id {} constracts)))

