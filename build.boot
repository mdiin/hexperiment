(set-env!
 :dependencies '[[adzerk/boot-cljs "2.1.4" :scope "test"]
                 [adzerk/boot-cljs-repl "0.3.3" :scope "test"]
                 [adzerk/boot-reload "0.5.2" :scope "test"]
                 [com.cemerick/piggieback "0.2.1"  :scope "test"]
                 [weasel                  "0.7.0"  :scope "test" :exclusions [http-kit]]
                 [org.clojure/tools.nrepl "0.2.12" :scope "test"]
                 [seancorfield/boot-tools-deps "0.4.5" :scope "test"]
                 [pandeiro/boot-http "0.8.3" :scope "test"]

                 [com.stuartsierra/component "0.3.2"]
                 [com.taoensso/timbre "4.10.0"]
                 [com.cognitect/transit-cljs "0.8.256"]
                 [com.andrewmcveigh/cljs-time "0.5.2"]
                 [org.clojure/clojure "1.10.0-alpha5"]
                 [org.clojure/clojurescript "1.10.312"]
                 [io.replikativ/replikativ "0.2.4"]
                 [compojure "1.6.1"]
                 [hoplon "7.2.0"]])

(require '[boot-tools-deps.core :refer [deps]]
         '[pandeiro.boot-http :refer [serve]]
         '[adzerk.boot-reload :refer [reload]]
         '[adzerk.boot-cljs :refer [cljs]]
         '[adzerk.boot-cljs-repl :refer [cljs-repl start-repl]])

(set-env! :source-paths #{"src/cljc"}
          :resource-paths #{"resources"}
          :asset-paths #{"assets"})

(deftask dev-ui
  []
  (set-env! :source-paths #(conj % "src/cljs"))
  (comp
   (serve :port 8000 :dir "boot-dev-target/public" :reload true)
   (watch)
   (speak)
   (reload :on-jsload 'hexperiment.core/reload)
   (cljs-repl)
   (cljs :ids ["ui"])
   (target :dir #{"boot-dev-target"})))

(deftask build-ui
  []
  (set-env! :source-paths #(conj % "src/cljs"))
  (comp
   (cljs :ids ["ui"]
         :optimizations :advanced)))

(deftask build-server
  []
  (set-env! :source-paths #(conj % "src/clj"))
  (comp
   (aot)
   (pom)
   (uber)
   (jar)))

(deftask build
  []
  (comp
   (build-ui)
   (sift :to-resource #{#"ui.js$"})
   (build-server)
   (sift :include #{#"\.jar$"})
   (target)))

(task-options!
 pom {:project 'hexperiment
      :version "1.0.0-SNAPSHOT"
      :description "A Replikativ+Hoplon+Javelin experiment, evolving."}
 aot {:namespace #{'hexperiment.core}}
 jar {:main 'hexperiment.core})
