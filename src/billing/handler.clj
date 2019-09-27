(ns billing.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults api-defaults]]
            [ring.middleware.json :refer [wrap-json-body wrap-json-response]]
            [cheshire.generate :refer [add-encoder encode-str]]
            [billing.db :as db])
  (:import (org.bson.types ObjectId)))

;(defn find-first
;  [f coll]
;  (first (filter f coll)))
;
;;(defn find-rate
;;  [rates name]
;;  (if-let [rate (find-first #(= name (:name %)) rates)]
;;    (:price rate)
;;    )
;;  )


(defn get-price
  [rate-price name]
  (if-let [price (get rate-price name)]
    price
    (get rate-price "Others")
    )
  )

; essa funcao tem que ficar pura
(defn calc
  [resources]
  (let [default-rates-info (db/get-default-rates-now)
        rates (get default-rates-info :rates)
        rate-price (reduce (fn [final r] (assoc final (:name r) (:price r))) {} rates)
        ]
    (println (get-price rate-price "BigBoostx"))
    ;(->> rates
    ;    (map #())
    ;    )
    ))

(defroutes app-routes
           (GET "/" [] "Hello World")
           (GET "/customers" [] {:body (db/get-customers)})
           (GET "/default_rates" [] {:body (db/get-default-rates)})
           (GET "/default_rates_now" [] {:body (db/get-default-rates-now)})
           (POST "/calc" request {:body (calc (get-in request [:body "resources"]))})
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
      ))
