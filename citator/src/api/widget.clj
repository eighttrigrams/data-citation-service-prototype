(ns api.widget
  (:require [ring.util.response :as response]
            [api.scraping :as scraping]))

;; TODO use cljstache

(defn get-form [req]
  (let [referrer (get (:query-params req) "referrer")]
    (str "<div style=\"background-color: darkred; color: white\">
      <h1>Citator Widget</h1>
      <p>Take a snapshot and generate a DOI for this site</p>
      <form method=\"get\" action=\"/widget/request-archival\">
       <input id=\"hidden-field\"
              type=\"hidden\" 
              name=\"" referrer "\">
       <input type=\"submit\" value=\"submit\"/>
      </form>
    </div>")))

(defn submit-handler [req]
  (let [referrer (first (keys (:query-params req)))
        path     (str "/widget?referrer=" (java.net.URLEncoder/encode referrer))]
    (scraping/archive! referrer)
    (response/redirect path)))
