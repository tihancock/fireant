(ns fireant.draw
  (:require-macros [cljs.core.async.macros :refer [go-loop]])
  (:require [goog.dom :as dom]
            [goog.events :as events]
            [cljs.core.async :refer [put! chan <!]]))

(defn listen [out-chan el type]
  (events/listen el type (fn [e] (put! out-chan e))))

(defn setup-canvas [c]
  (events/removeAll c)
  (set! (.-width c) (.-offsetWidth c))
  (set! (.-height c) (.-offsetHeight c)))

(defn setup-drawing! []
  (let [mouse-chan (chan)
        draw (dom/getElement "draw")
        context (.getContext draw "2d")]
    (setup-canvas draw)
    (doseq [e ["mousedown" "mouseup" "mousemove"]]
      (listen mouse-chan draw e))
    (go-loop [mousedown false]
      (let [msg (<! mouse-chan)
            type (.-type msg)
            start-mousedown (and (not mousedown) (= type "mousedown"))]
        (recur
         (cond
           start-mousedown
           (do (.beginPath context)
               true)

           (and mousedown (= type "mousemove"))
           (do (.lineTo context (.-offsetX msg) (.-offsetY msg))
               (.stroke context)
               true)

           (= type "mouseup") false

           :else false))))))
