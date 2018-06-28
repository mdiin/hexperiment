(ns hexperiment.list
  (:require
   [hexperiment.state :as state]

   [hoplon.core :as h]
   [javelin.core :as j :refer [cell]]))

(h/defelem add-item
  [attributes children]
  (let [input-state (cell "")]
    [(h/input :type "text" :placeholder "Items" :change #(reset! input-state (.. % -target -value)))
     (h/input :type "submit" :click #(do
                                       (swap! state/app-state update :items conj @input-state)))]))

(h/defelem shopping-list
  [attributes children]
  (h/ol
   attributes
   (map h/li children)))
