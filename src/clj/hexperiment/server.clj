(ns hexperiment.server
  (:require
   [clojure.core.async :as async]
   [com.stuartsierra.component :as component :refer [Lifecycle]]
   [compojure.core :refer [defroutes GET]]
   [compojure.route :refer [resources not-found]]
   [kabel.peer]
   [konserve.memory :as mem]
   [org.httpkit.server :as httpkit]
   [replikativ.peer]
   [superv.async :refer [<?? S]]
   [taoensso.timbre :as timbre]

   [clojure.java.io :as io]))

(defroutes base-routes
  (GET "/" [] (io/resource "index.html"))
  (resources "/")
  (not-found "<h1>Page not found.</h1>"))

(defrecord Server [port routes peer]
  Lifecycle
  (start [this]
    (if (:instance this)
      this
      (do
        (timbre/info "Starting server")
        (let [server-instance (httpkit/run-server routes {:port port})
              _ (timbre/info "Http-kit webserver started")]
          (if (<?? S (kabel.peer/start (:instance peer)))
            (do
              (timbre/info "Replikativ server peer started")
              (assoc this :instance server-instance))
            (do
              (timbre/info "Replikativ peer failed to start, terminating webserver")
              (server-instance)
              this))))))

  (stop [this]
    (if-let [instance (:instance this)]
      (do
        (timbre/info "Stopping server")
        (instance)
        (dissoc this :instance))
      this)))

(defrecord Peer [store uri]
  Lifecycle
  (start [this]
    (if (:instance this)
      this
      (assoc this :instance (<?? S (replikativ.peer/server-peer S store uri)))))

  (stop [this]
    (if (:instance this)
      (dissoc this :instance)
      this)))

(def app (component/system-map
          :uri "ws://127.0.0.1:9090"
          :routes #'base-routes
          :store (<?? S (mem/new-mem-store))
          :peer (component/using (map->Peer {})
                                 [:store :uri])
          :server (component/using
                   (map->Server {:port 8080})
                   [:routes :peer])))

(defn start-server []
  (alter-var-root #'hexperiment.server/app component/start-system)
  :okay)

(comment
  (alter-var-root #'hexperiment.server/app component/start-system)
  (alter-var-root #'hexperiment.server/app component/stop-system))
