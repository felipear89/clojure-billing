(ns billing.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults api-defaults]]
            [ring.middleware.json :refer [wrap-json-body wrap-json-response]]
            [cheshire.generate :refer [add-encoder encode-str]]
            [billing.db :as db]
            [billing.compute-resource :refer [compute]]
            [clojure.tools.logging :as log]
            [ring.logger :as ring-logger])
  (:import (org.bson.types ObjectId)))

(defn compute-resources [request]
  (let [resources (get-in request [:body "resources"])
        rates (get (db/get-default-rates-now) :rates)
        resources-charges (compute resources rates)
        reduce-total-to-pay (fn [total r] (+ total (BigDecimal. (:totalToPay r))))
        total-to-pay (reduce reduce-total-to-pay 0 resources-charges)]
    (log/info "Resources computed" resources-charges)
    {:body {
            :resources  resources-charges
            :totalToPay (str total-to-pay)
            }}
    ))

(defroutes app-routes
           (GET "/" [] "Hello World")
           (GET "/customers" [] {:body (db/get-customers)})
           (GET "/default_rates" [] {:body (db/get-default-rates)})
           (GET "/default_rates_now" [] {:body (db/get-default-rates-now)})
           (POST "/compute_resources" request (compute-resources request))
           (route/not-found "Not Found"))

(defn wrap-content-json [h]
  (fn [req] (assoc-in (h req) [:headers "Content-Type"] "application/json; charset=utf-8")))

(defn wrap-json-data [h]
  (fn [req]
    (-> (h req)
        (update-in [:body] (fn [b] {"data" b}))
        )
    ))

(add-encoder ObjectId encode-str)

(def app
  (-> (wrap-defaults app-routes api-defaults)
      (wrap-json-body)
      (wrap-json-data)
      (wrap-json-response)
      (wrap-content-json)
      (ring-logger/wrap-with-logger)))
