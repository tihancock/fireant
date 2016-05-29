(ns fireant.middleware
  (:require [ring.middleware.defaults :refer [site-defaults wrap-defaults]
             ring.middelware.session :refer [wrap-session]
             ring.middleware.params :refer [wrap-params]]))

(defn wrap-middleware [handler]
  (-> handler
      (wrap-defaults (assoc-in site-defaults [:security :anti-forgery] false))
      wrap-params))
