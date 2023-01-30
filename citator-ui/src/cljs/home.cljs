(ns home
  (:require [reagent.core :as r]
            [ajax.core :refer [GET POST]]
            [home.resources :as resources]))

(defn atom-input [value]
  [:input.text {:type      "text"
                :value     @value
                :on-change #(reset! value (-> % .-target .-value))}])

(defn archive-url! [url generated-handle resources]
  (POST "/api" {:body (.stringify js/JSON (clj->js {:url url}))
                :headers {"Content-Type" "application/json"}
                :handler (fn [resp] 
                           (let [doi (get resp "doi")
                                 resources* (get resp "resources")]
                             (reset! generated-handle doi)
                             (reset! resources resources*)))
                :error-handler (fn [resp] (prn "Error response:" resp))}))

(defn fetch-resources [resources]
  (GET "/api" {:headers       {"Content-Type" "application/json"}
               :handler       (fn [resp]
                                (let [resources* (get resp "resources")]
                                  (reset! resources resources*)))
               :error-handler (fn [resp] (prn "Error response:" resp))}))

(defn- create-ws [resources]
  (let [ws  (js/WebSocket. "ws://localhost:3005/ws")]
    (set! (.-onopen ws)
          (fn [a] (prn "onopen" (js/console.log a))))
    (set! (.-onerror ws)
          (fn [a] (prn "onerror" (js/console.log a))))
    (set! (.-onclose ws)
          (fn [a] (prn "onclose" (js/console.log a))))
    (set! (.-onmessage ws)
          (fn [_a]
            #_(prn ".." (.-data a))
            (js/setTimeout 
             #(fetch-resources resources) 
             ;; TODO review, this is necessary because of asynchronicity between citator backend and scraper              
             15000)))))

;; TODO use r/let to make a fetch call
(defn component []
  (let [url (r/atom "")
        generated-handle (r/atom "")
        resources (r/atom '())]
    (fetch-resources resources)
    (create-ws resources)
    (fn []
      [:<>
       [:h1 "Citator"]
       [:p "Insert the URL of a site you want to archive here and click submit."]
       [atom-input url]
       [:input {:type     :button
                :on-click #(archive-url! @url generated-handle resources)
                :value    "submit"}]
       [resources/component resources]])))
