(ns billing.routes
  (:require [compojure.core :refer [defroutes GET POST PUT]]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults api-defaults]]
            [ring.middleware.json :refer [wrap-json-body wrap-json-response]]
            [ring.logger :as ring-logger]
            [cheshire.generate :refer [add-encoder encode-str]]
            [billing.db :as db]
            [billing.contract.controller :refer [post-charge-resources save-contract save-default-rate]])
  (:import (org.bson.types ObjectId)
           (java.time LocalDateTime Instant)))

(defroutes app-routes
           (GET "/" [] "Hello World")
           (GET "/default_rates" [] {:body (db/get-default-rates)})
           (POST "/default_rates" request {:body (save-default-rate (:body request))})
           (PUT "/default_rates/:id" request
             {:body (save-default-rate (:body request) (get-in request [:params :id]))})
           (GET "/contracts" [] {:body (db/get-contracts)})
           (POST "/contracts" request {:body (save-contract (:body request))})
           (PUT "/contracts/:id" request
             {:body (save-contract (:body request) (get-in request [:params :id]))})
           (POST "/contracts/charge_resources" request {:body (post-charge-resources (:body request))})
           (route/not-found "Not Found"))

(defn wrap-content-json [h]
  (fn [req] (assoc-in (h req) [:headers "Content-Type"] "application/json; charset=utf-8")))

(defn wrap-json-data [h]
  (fn [req]
    (-> (h req)
        (update-in [:body] (fn [b] {"data" b}))
        )))

(add-encoder ObjectId encode-str)
(add-encoder LocalDateTime
             (fn [c jsonGenerator]
               (.writeString jsonGenerator (.toString c))))
(add-encoder Instant
             (fn [c jsonGenerator]
               (.writeString jsonGenerator (.toString c))))
(add-encoder BigDecimal
             (fn [c jsonGenerator]
               (.writeString jsonGenerator (.toString c))))

(def app
  (-> (wrap-defaults app-routes api-defaults)
      (wrap-json-body {:keywords? true :bigdecimals? true})
      (wrap-json-data)
      (wrap-json-response)
      (wrap-content-json)
      (ring-logger/wrap-with-logger)))
