(ns billing.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults api-defaults]]
            [billing.json-utils :refer :all]
            [billing.db :as db]))

(defroutes app-routes
  (GET "/" [] "Hello World")
  (GET "/customers" [] (response-json (db/get-customers)))
  (route/not-found "Not Found"))

(def app
  (wrap-defaults app-routes api-defaults))
