(ns hexperiment.core
  (:gen-class)
  (:require
   [hexperiment.server :as server]
   ))

(defn -main [& args]
  (server/start-server))
