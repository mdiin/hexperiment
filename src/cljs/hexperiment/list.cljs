(ns hexperiment.list
  (:require
   [hexperiment.state :as state]

   [cljs-time.core :as time]
   [goog.object :as object]
   [hoplon.core :as h]
   [javelin.core :as j :refer [cell] :refer-macros [cell= defc=]]))

(defc= shopping-list-items
  (get-in state/app-state [:items])
  (partial swap! state/app-state assoc-in [:items]))

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
