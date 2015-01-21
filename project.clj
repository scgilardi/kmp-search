(defproject kmp-search "0.1.2"
  :description "search a byte stream for a byte pattern"
  :url "http://github.com/scgilardi/kmp-search"
  :license {:name "Eclipse Public License 1.0"
            :url "https://www.eclipse.org/legal/epl-v10.html"
            :distribution :repo}
  :deploy-repositories [["releases" :clojars]]
  :global-vars {*warn-on-reflection* true}
  :java-source-paths ["java/src"]
  :jvm-opts []
  :profiles {:dev {:dependencies [[org.clojure/clojure "1.7.0-alpha5"]]}})
