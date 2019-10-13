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
  (:import (org.bson.types ObjectId)
           (java.time LocalDate LocalDateTime)))

(defroutes app-routes
           (GET "/" [] "Hello World")
           (GET "/contracts" [] {:body (db/get-contracts)})
           (GET "/default_rates" [] {:body (db/get-default-rates)})
           (POST "/charge_resources" request {:body (post-charge-resources (:body request))})
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

(def app
  (-> (wrap-defaults app-routes api-defaults)
      (wrap-json-body {:keywords? true})
      (wrap-json-data)
      (wrap-json-response)
      (wrap-content-json)
      (ring-logger/wrap-with-logger)))
