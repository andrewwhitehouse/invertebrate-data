(defproject invertebrate-data "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Copyright (c) Andrew Whitehouse"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.10.0"]
                 [dk.ative/docjure "1.16.0"]
                 [clojure.java-time "0.3.2"]
                 [org.clojure/data.csv "1.0.0"]]
  :main invertebrate-data.core
  :repl-options {:init-ns invertebrate-data.core}
  :resource-paths ["src/resources"])
