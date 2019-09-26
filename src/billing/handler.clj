(ns billing.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults api-defaults]]
            [ring.middleware.json	:refer	[wrap-json-body]]
            [billing.json-utils :refer :all]
            [billing.db :as db]))

(defroutes app-routes
  (GET "/" [] "Hello World")
  (GET "/customers" [] (response-json (db/get-customers)))
  (GET "/default_rates" [] (response-json (db/get-default-rates)))
  (POST "/calc" request (str (:body request)))
  (route/not-found "Not Found"))

(def app
  (->	(wrap-defaults	app-routes	api-defaults)
      (wrap-json-body)))
