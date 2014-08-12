(defproject clj-firmata "2.0.0-SNAPSHOT"
  :description "clj-firmata provides access to Standard Firmata (http://firmata.org/) commands via clojure"
  :url "https://github.com/peterschwarz/clj-firmata"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :scm {:name "git"
        :url "https://github.com/peterschwarz/clj-firmata"}

  :jar-exclusions [#"\.cljx"]

  :source-paths ["src/cljx"]

  :test-paths ["target/test-classes"]

  :dependencies [[org.clojure/clojure "1.5.1"]
                 [org.clojure/clojurescript "0.0-2307"]

                 [org.clojure/core.async "0.1.303.0-886421-alpha"]

                 [clj-serial "2.0.1"]]

  :cljx {:builds [{:source-paths ["src/cljx"]
                   :output-path "target/classes"
                   :rules :clj}

                  {:source-paths ["src/cljx"]
                   :output-path "target/classes"
                   :rules :cljs}

                  {:source-paths ["test/cljx"]
                   :output-path "target/test-classes"
                   :rules :clj}]}

  :profiles {:dev {:plugins [[com.keminglabs/cljx "0.4.0"]
                             [lein-cljsbuild "1.0.3"]
                             [lein-cloverage "1.0.2"]]}}

  :hooks [cljx.hooks]

;;   :cljsbuild {:builds
;;               [{:source-paths ["target/classes"]
;;                 :compiler {:output-to "build/firmata.js"
;;                            :optimizations :advanced
;;                            :pretty-print true}}]}

  )
