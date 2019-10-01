(ns billing.routes
  (:use clojure.pprint)
  (:require [compojure.core :refer [defroutes GET POST]]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults api-defaults]]
            [ring.middleware.json :refer [wrap-json-body wrap-json-response]]
            [ring.logger :as ring-logger]
            [cheshire.generate :refer [add-encoder encode-str]]
            [billing.db :as db]
            [billing.resource.controller :refer [post-charge-resources]])
  (:import (org.bson.types ObjectId)))

(defroutes app-routes
           (GET "/" [] "Hello World")
           (GET "/customers" [] {:body (db/get-customers)})
           (GET "/default_rates" [] {:body (db/get-default-rates)})
           (GET "/default_rates_now" [] {:body (db/get-default-rates-now)})
           (POST "/charge_resources" request (post-charge-resources request))
           (route/not-found "Not Found"))

(defn wrap-content-json [h]
  (fn [req] (assoc-in (h req) [:headers "Content-Type"] "application/json; charset=utf-8")))

(defn wrap-json-data [h]
  (fn [req]
    (-> (h req)
        (update-in [:body] (fn [b] {"data" b}))
        )))

(add-encoder ObjectId encode-str)

(def app
  (-> (wrap-defaults app-routes api-defaults)
      (wrap-json-body {:keywords? true})
      (wrap-json-data)
      (wrap-json-response)
      (wrap-content-json)
      (ring-logger/wrap-with-logger)))
