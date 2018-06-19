(set-env!
  :dependencies '[[adzerk/boot-cljs "2.1.4" :scope "test"]
                  [adzerk/boot-reload "0.5.2" :scope "test"]
                  [seancorfield/boot-tools-deps "0.4.5" :scope "test"]
                  [tailrecursion/boot-jetty "0.1.3" :scope "test"]

                  [org.clojure/clojure "1.10.0-alpha4"]
                  [org.clojure/clojurescript "1.10.312"]
                  [hoplon "7.2.0"]])

(require '[boot-tools-deps.core :refer [deps]]
         '[tailrecursion.boot-jetty :refer [serve]]
         '[adzerk.boot-reload :refer [reload]]
         '[adzerk.boot-cljs :refer [cljs]])

(deftask dev-ui
  []
  (set-env! :source-paths #{"src/cljc" "src/cljs"}
            :asset-paths #{"assets"}
            :resource-paths #{"resources"})
  (comp
    (watch)
    (speak)
    (reload)
    (cljs :ids ["main"])
    (serve :port 8000)))

(deftask build-ui
  []
  (set-env! :source-paths #{"src/cljc" "src/cljs"})
  (comp
    (cljs :ids ["main"])))

