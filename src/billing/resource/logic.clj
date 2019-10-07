(ns billing.resource.logic)

(defn contracts-by-id [constracts]
  (let [fn-group-by-date (fn [result contract]
                           (assoc result (str (:_id contract)) contract))]
    (reduce fn-group-by-date {} constracts)))

