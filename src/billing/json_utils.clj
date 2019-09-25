(ns billing.json-utils
  (:require [cheshire.core :refer [generate-string]]
            [cheshire.generate :refer [add-encoder encode-str]])
  (:import (org.bson.types ObjectId)))

(add-encoder ObjectId encode-str)

(defn- response-content-json
  [content]
   	{
     :headers	{"Content-Type" "application/json;	charset=utf-8"}
      :body	content
     }
  )

(defn response-json
  [param]
  (-> param
      generate-string
      response-content-json))
