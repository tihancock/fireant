(ns fireant.draw
  (:require-macros [cljs.core.async.macros :refer [go-loop]])
  (:require [goog.dom :as dom]
            [goog.events :as events]
            [cljs.core.async :refer [put! chan <!]]
            [cljs-http.client :as http]))

(defn listen [out-chan el type]
  (events/listen el type (fn [e] (put! out-chan e))))

(defn setup-canvas [c]
  (events/removeAll c)
  (set! (.-width c) (.-offsetWidth c))
  (set! (.-height c) (.-offsetHeight c)))

(defn setup-drawing! []
  (let [mouse-chan (chan)
        canvas (dom/getElement "draw")
        context (.getContext canvas "2d")]
    (setup-canvas canvas)
    (doseq [e ["mousedown" "mouseup" "mousemove" "mouseout"]]
      (listen mouse-chan canvas e))
    (go-loop [mousedown false]
      (let [msg (<! mouse-chan)
            type (.-type msg)
            left-button (= 0 (.-button msg))
            start-mousedown (and (not mousedown) (= type "mousedown") left-button)]
        (recur
         (cond
           start-mousedown
           (do (.beginPath context)
               true)

           (and (> mousedown 0) (= type "mousemove"))
           (do (.lineTo context (.-offsetX msg) (.-offsetY msg))
               (.stroke context)
               true)

           (and (= type "mouseup") left-button) false

           (= type "mouseout") false

           :else mousedown))))))

(defn upload-drawing! []
  (let [image (.toDataURL (dom/getElement "draw"))
        canvas (dom/getElement "draw")]
    (http/post "/upload" {:form-params {:image image}})
    (.clearRect (.getContext canvas "2d") 0 0 (.-width canvas) (.-height canvas))))
