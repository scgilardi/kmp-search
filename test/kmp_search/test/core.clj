(ns kmp-search.test.core
  (:require [clojure.test :refer :all]
            [kmp-search.core :refer :all]))

(defn index-of-reducer
  [[_ matcher] bytes]
  (let [[index matcher] (search-bytes matcher bytes)]
    (if index
      (reduced [index matcher])
      [index matcher])))

(defn the-bytes [x]
  (.getBytes x))

(defn test-index [m x]
  (let [[index _] (if (string? x)
                    (search-bytes m (the-bytes x))
                    (reduce index-of-reducer [0 m] (map the-bytes x)))]
    index))

(deftest a-test
  (testing "failure generation"
    (are [x y] (= (seq (:failure (matcher (.getBytes x)))) y )
         "abcdabd" [0 0 0 0 1 2 0]
         "participate in parachute"
         [0 0 0 0 0 0 0 1 2 0 0 0 0 0 0 1 2 3 0 0 0 0 0 0]
         "ululation"
         [0 0 1 2 0 0 0 0 0]
         "rangerangles"
         [0 0 0 0 0 1 2 3 4 0 0 0]))
  (testing "matches"
    (let [m (matcher (the-bytes "abc"))]
      (are [x y] (= (test-index m x) y)
           "" nil
           "rogue" nil
           "abc" 0
           "1abc" 1
           "ababc" 2
           ["a" ""  "b" "a" "b" "c"] 2))))
