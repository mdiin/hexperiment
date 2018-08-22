(ns hexperiment.core
  (:require
   [konserve.memory :as mem]
   [replikativ.peer]
   [replikativ.stage]
   [replikativ.crdt.ormap.realize :as real]
   [replikativ.crdt.ormap.stage :as ors]
   [cljs.core.async :as async]
   [superv.async :refer [S] :as sasync]
   [hasch.core :as hasch :refer [uuid]]

   [taoensso.timbre :as timbre]


   [cljs-time.core :as time]
   [cljs-time.coerce :as time-coerce]
   [goog.object :as object]
   [hoplon.core :as h]
   [hoplon.jquery]
   [javelin.core :as j :refer [cell cell= defc defc=]]
   )
  (:require-macros
   [cljs.core.async.macros :as async-macros]
   [superv.async :as sasync-macros :refer [<?]]
   ))

(enable-console-print!)

;; === State management ===
;; === Replikativ data replication

(def uri "ws://127.0.0.1:9090")

(def user "mail:matthias@ingesman.dk")
(def ormap-id #uuid "e29493b3-4241-4bdd-8e81-a573ddc3028d")

(defc app-state {:items []})

(def stream-eval-fns
  {'add (fn [S a new]
          (.log js/console "Adding: " new)
          (swap! a update-in [:items] conj new)
          a)
   'remove (fn [S a val]
             (.log js/console "Removing: " val)
             (swap! a update-in [:items] (fn [old-state] (remove #{val} old-state))))})

(defn setup-replikativ
  []
  (println "Setting up replikativ")
  (sasync-macros/go-try
   S
   (let [store (<? S (mem/new-mem-store))
         peer (<? S (replikativ.peer/client-peer S store))
         stage (<? S (replikativ.stage/create-stage! user peer))
         stream (real/stream-into-identity! stage [user ormap-id] stream-eval-fns app-state)]
     (<? S (ors/create-ormap! stage :description "items" :id ormap-id))
     (replikativ.stage/connect! stage uri)
     {:store store
      :peer peer
      :stage stage
      :stream stream})))

(declare replikativ-state)

(cell= (pr :app-state app-state))

(defn add-item-to-state
  [state item]
  (ors/assoc! (:stage state)
              [user ormap-id]
              (uuid item)
              [['add item]]))

;; === Components ===

(defc= shopping-list-items
  (get-in app-state [:items])
  (partial swap! app-state assoc-in [:items]))

(defn make-item
  [name]
  {:category :items/misc
   :name name
   :added (time-coerce/to-long (time/time-now))})

(h/defelem add-item
  [attributes children]
  (let [input-state (cell "")]
    (h/form
     :submit #(do
                (when-not (empty? @input-state)
                  (add-item-to-state replikativ-state (make-item @input-state)))
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
  (sasync-macros/go-try
   S
   (do
     (def replikativ-state (<? S (setup-replikativ)))
     (.error js/console "Connected to Replikativ")))
  (h/with-init! (init)))

