(ns hexperiment.core
  (:require
    [hoplon.core :as h]
    [javelin.core :as j :refer [cell cell=]]
    [hoplon.goog]))

(defn init
  []
  (h/html
    (h/body
      (h/h1 "Hexperiment away!")
      (h/p "This is what it feels like..."))))

(defn reload
  []
  (.log js/console "[RELOAD]")
  (set! (.-innerHTML js/document.documentElement) "")
  (init))

(defn ^:export main
  []
  (.log js/console "[MAIN]")
  (init))

