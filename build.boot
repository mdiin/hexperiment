(set-env!
 :dependencies '[[adzerk/boot-cljs "2.1.4" :scope "test"]
                 [adzerk/boot-cljs-repl "0.3.3" :scope "test"]
                 [adzerk/boot-reload "0.5.2" :scope "test"]
                 [com.cemerick/piggieback "0.2.1"  :scope "test"]
                 [weasel                  "0.7.0"  :scope "test"]
                 [org.clojure/tools.nrepl "0.2.12" :scope "test"]
                 [seancorfield/boot-tools-deps "0.4.5" :scope "test"]
                 [tailrecursion/boot-jetty "0.1.3" :scope "test"]

                 [com.andrewmcveigh/cljs-time "0.5.2"]
                 [org.clojure/clojure "1.10.0-alpha4"]
                 [org.clojure/clojurescript "1.10.312"]
                 [io.replikativ/replikativ "0.2.4"]
                 [compojure "1.6.1"]
                 [hoplon "7.2.0"]])

(require '[boot-tools-deps.core :refer [deps]]
         '[tailrecursion.boot-jetty :refer [serve]]
         '[adzerk.boot-reload :refer [reload]]
         '[adzerk.boot-cljs :refer [cljs]]
         '[adzerk.boot-cljs-repl :refer [cljs-repl start-repl]])

(deftask dev-ui
  []
  (set-env! :source-paths #{"src/cljc" "src/cljs"}
            :asset-paths #{"assets"}
            :resource-paths #{"resources"})
  (comp
   (watch)
   (speak)
   (reload :on-jsload 'hexperiment.core/reload)
   (cljs-repl)
   (cljs :ids ["ui"])
   (serve :port 8000)))

(deftask build-ui
  []
  (set-env! :source-paths #{"src/cljc" "src/cljs"})
  (comp
   (target)
   (cljs :ids ["ui"])))

