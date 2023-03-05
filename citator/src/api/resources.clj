(ns api.resources
  (:require [clojure.set :as s]
            [service.datastore :as datastore]
            [api.scraping :as scraping]))

(defn- convert [resource]
  (-> resource
      (s/rename-keys {:version :doi
                      :xt/id   :url})
      (dissoc :type)))

#_{:clj-kondo/ignore [:unresolved-var]}
(defn get-handler [_]
  {:body {:resources (map convert (datastore/get-all))}})

#_{:clj-kondo/ignore [:unresolved-var]}
(defn submit-handler [notify-listeners]
  (fn [{{url :url} :body}]
    (future (scraping/archive! url)
            (notify-listeners))
    {:body {:status :ok}}))

(defn get-resource-handler
  [req]
  (let [doi      (:doi (:route-params req))]
    {:body (-> (datastore/get-version doi)
               convert
               (update :versions #(map convert %)))}))
