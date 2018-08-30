(ns hexperiment.core
  (:gen-class)
  (:require
   [hexperiment.server]
   ))


(defn -main [& args]
  (alter-var-root #'hexperiment.server/app component/start-system)
  :okay)

(comment
  (alter-var-root #'hexperiment.server/app component/start-system)
  (alter-var-root #'hexperiment.server/app component/stop-system)

  )
