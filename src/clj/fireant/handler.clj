(ns fireant.handler
  (:require [compojure.core :refer [GET POST defroutes]]
            [compojure.route :refer [not-found resources]]
            [hiccup.page :refer [include-js include-css html5]]
            [fireant.middleware :refer [wrap-middleware]]
            [config.core :refer [env]]
            [ring.util.anti-forgery :refer [anti-forgery-field]]))

(def mount-target
  [:div#app
      [:h3 "ClojureScript has not been compiled!"]
      [:p "please run "
       [:b "lein figwheel"]
       " in order to start the compiler"]])

(defn head []
  [:head
   [:meta {:charset "utf-8"}]
   [:meta {:name "viewport"
           :content "width=device-width, initial-scale=1"}]
   (include-css (if (env :dev) "/css/site.css" "/css/site.min.css"))])

(def loading-page
  (html5
    (head)
    [:body {:class "body-container"}
     mount-target
     (include-js "/js/app.js")]))


(defroutes routes
  (GET "/" [] loading-page)
  (GET "/draw" [] loading-page)

  (POST "/upload" {params :params}
        (println "IMAGE INCOMING: " params)
        {:status 200})

  (resources "/")
  (not-found "Not Found"))

(def app (wrap-middleware #'routes))
