(ns hexperiment.core
  (:require
   [clojure.core.async :as async]
   [compojure.core :refer [defroutes]]
   [compojure.route :refer [resources not-found]]
   [kabel.peer]
   [konserve.memory :as mem]
   [replikativ.peer]
   [superv.async :refer [<?? S]]
   [org.httpkit.server :as httpkit]
   ))

(def uri "ws://127.0.0.1:9090")

(defroutes base-routes
  (resources "/")
  (not-found "<h1>Page not found.</h1>"))

(defn start-server
  []
  (let [store (<?? S (mem/new-mem-store))
        peer (<?? S (replikativ.peer/server-peer S store uri))]
    (httpkit/run-server #'base-routes {:port 8080})
    (println ";; httpkit server started")
    (<?? S (kabel.peer/start peer))
    (println ";; peer started")
    (<?? S (async/chan))))

(comment
  (start-server)

  )
