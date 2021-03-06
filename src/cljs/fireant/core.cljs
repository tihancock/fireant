(ns fireant.core
    (:require [reagent.core :as reagent :refer [atom]]
              [reagent.session :as session]
              [secretary.core :as secretary :include-macros true]
              [accountant.core :as accountant]
              [fireant.draw :as draw]))

;; -------------------------
;; Views

(defn home-page []
  [:div [:h2 "Welcome to fireant"]
   [:div [:a {:href "draw"} "Draw!"]]])

(defn draw []
  (reagent/create-class
   {:component-did-mount draw/setup-drawing!

    :reagent-render (fn [_]
                      [:div
                       [:input {:type :button
                                :value "Submit"
                                :on-click #(do (draw/upload-drawing!)
                                               (session/put! :current-page #'home-page))}]
                       [:div {:id :draw-container}
                        [:canvas {:id :draw}]]])}))

(defn current-page []
  [:div [(session/get :current-page)]])

;; -------------------------
;; Routes

(secretary/defroute "/" []
  (session/put! :current-page #'home-page))

(secretary/defroute "/draw" []
  (session/put! :current-page #'draw))

;; -------------------------
;; Initialize app

(defn mount-root []
  (reagent/render [current-page] (.getElementById js/document "app")))

(defn init! []
  (accountant/configure-navigation!
    {:nav-handler
     (fn [path]
       (secretary/dispatch! path))
     :path-exists?
     (fn [path]
       (secretary/locate-route path))})
  (accountant/dispatch-current!)
  (mount-root))
