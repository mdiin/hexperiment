(ns hexperiment.core
  (:require
   [hexperiment.list :as list]
   [hexperiment.state :as state]

   [hoplon.core :as h]
   [javelin.core :as j :refer [cell cell=]]
   [hoplon.jquery]))

(defn init
  []
  (h/html
    (h/body
      (h/h1 "Hexperiment away!")
      (h/p "This is what it feels like...")
      (h/p "yep, it is.")
      (h/p (get-in @state/app-state [:messages :initial]))
      (list/shopping-list
       (get-in @state/app-state [:items]))
      (list/add-item))))

(defn reload
  []
  (.log js/console "[RELOAD]")
  (set! (.-innerHTML js/document.documentElement) "")
  (init))

(defn ^:export main
  []
  (.log js/console "[MAIN]")
  (h/with-init! (init)))

