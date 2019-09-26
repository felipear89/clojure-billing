(ns billing.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults api-defaults]]
            [ring.middleware.json	:refer	[wrap-json-body wrap-json-response]]
            [billing.db :as db]))

(defroutes app-routes
  (GET "/" [] "Hello World")
  (GET "/customers" []  {:body (db/get-customers)})
  (GET "/default_rates" [] {:body (db/get-default-rates)})
  (POST "/calc" request (str (:body request)))
  (route/not-found "Not Found"))

(defn wrap-content-json [h]
  (fn [req] (assoc-in (h req) [:headers "Content-Type"] "application/json")))

(def app
  (->	(wrap-defaults	app-routes	api-defaults)
      (wrap-json-body)
       (wrap-json-response)
       (wrap-content-json)
       ))
