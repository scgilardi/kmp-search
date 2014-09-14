(ns kmp-search.test.core-test
  (:require [clojure.test :refer :all]
            [kmp-search.core :refer :all]))

(defn the-bytes [x]
  (.getBytes x))

(defn test-index [m x]
  (let [[_ index] (if (string? x)
                    (index-of m (the-bytes x))
                    (reduce index-of-reducer [m 0] (map the-bytes x)))]
    index))

(deftest a-test
  (let [m (matcher (the-bytes "abc"))]
    (are [x y] (= (test-index m x) y)
         "" -1
         "abc" 0
         "1abc" 1
         "ababc" 2
         ["a" ""  "b" "a" "b" "c"] 2)))

