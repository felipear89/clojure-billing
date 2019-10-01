(ns billing.resource.controller
  (:require [billing.db :as db]
            [billing.resource.logic :refer [charge-resources]]
            [billing.resource.model :refer [ResourceConsumptionList]]
            [schema-tools.core :as st]))

(defn post-charge-resources [request]
  (let [resource-consumption-list (get-in request [:body :resources])
        resource-consumption-list (st/select-schema resource-consumption-list ResourceConsumptionList)
        rates (get (db/get-default-rates-now) :rates)]
    (charge-resources resource-consumption-list rates)))
