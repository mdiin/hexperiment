(ns hexperiment.state
  (:require
   [javelin.core :as j :refer [cell cell=]]))

(enable-console-print!)

(defonce app-state (cell {}))

(cell= (pr :app-state app-state))

