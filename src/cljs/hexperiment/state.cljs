(ns hexperiment.state
  (:require
   [javelin.core :as j :refer [cell cell=]]))

(enable-console-print!)

(defonce app-state (cell {:messages {:initial "This is your first message!"}}))

(cell= (pr :app-state app-state))

