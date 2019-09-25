(ns billing.json-utils
  (:require [cheshire.core :refer :all])
  (:import (org.bson.types ObjectId)))

(defn convert-ObjectId-to-string [[k v]]
  (if (= ObjectId (class v))
    [k (str v)]
    [k v]))

(defn convert-all-ObjectId [m]
  (clojure.walk/postwalk
    (fn [m]
      (if (map? m)
        (into {} (map convert-ObjectId-to-string m))
        m))
    m))

(defn generate-json [m]
  (-> m
      convert-all-ObjectId
      generate-string))
