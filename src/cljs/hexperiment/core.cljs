(ns hexperiment.core
  (:require
   [cljs-time.core :as time]
   [hoplon.core :as h]
   [hoplon.jquery]
   [javelin.core :as j :refer [cell cell= defc=]]
   [goog.object :as object]
   ))

(enable-console-print!)

;; === State management ===

(defonce app-state (cell {}))

(cell= (pr :app-state app-state))

;; === Components ===

(defc= shopping-list-items
  (get-in app-state [:items])
  (partial swap! app-state assoc-in [:items]))

(defn make-item
  [name]
  {:category :items/misc
   :name name
   :added (time/time-now)})

(h/defelem add-item
  [attributes children]
  (let [input-state (cell "")]
    (h/form
     :submit #(do
                (when-not (empty? @input-state)
                  (swap! shopping-list-items (fnil conj []) (make-item @input-state)))
                (reset! input-state "")
                (.preventDefault %))
     (h/input
      :type "text"
      :placeholder "Items"
      :value @input-state
      :change #(reset! input-state (object/getValueByKeys % "target" "value")))
     (h/input :type "submit" :value "Add"))))

(h/defelem shopping-list
  [attributes children]
  (let [items shopping-list-items]
    (h/ol
     attributes
     (h/loop-tpl
      :bindings [{:keys [name]} items]
      (h/li name)))))

;; === Initialization logic ===

(defn init
  []
  (h/html
    (h/body
      (h/h1 "Indkøbslisten")
      (shopping-list
       (get-in @app-state [:items]))
      (add-item))))

(defn reload
  []
  (.log js/console "[RELOAD]")
  (set! (.-innerHTML js/document.documentElement) "")
  (init))

(defn ^:export main
  []
  (.log js/console "[MAIN]")
  (h/with-init! (init)))

