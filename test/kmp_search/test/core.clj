(ns kmp-search.test.core
  (:require [clojure.test :refer :all]
            [kmp-search.core :refer :all]))

(defn the-bytes [x]
  (.getBytes x))

(defn all-matches
  [pattern byte-arrays]
  (loop [matcher (matcher pattern)
         byte-arrays byte-arrays
         matches []]
    (if byte-arrays
      (let [[index matcher] (search-bytes matcher (first byte-arrays))]
        (if index
          (recur matcher byte-arrays (conj matches index))
          (recur matcher (next byte-arrays) matches)))
      matches)))

(deftest a-test
  (testing "border generation"
    (are [x y] (= (seq (:border (matcher (.getBytes x)))) y )
         "abcdabd" [-1 0 0 0 0 1 2 0]
         "ababaa" [-1 0 0 1 2 3 1]
         "participate in parachute"
         [-1 0 0 0 0 0 0 0 1 2 0 0 0 0 0 0 1 2 3 0 0 0 0 0 0]
         "ululation"
         [-1 0 0 1 2 0 0 0 0 0]
         "rangerangles"
         [-1 0 0 0 0 0 1 2 3 4 0 0 0]
         "aaaaaaaaaa"
         [-1 0 1 2 3 4 5 6 7 8 9]))
  (testing "matching"
    (are [p x y] (= (all-matches (the-bytes p) (map the-bytes x)) y)
         "" [""] [0]
         "" ["a"] [0 1]
         "" ["a" "b"] [0 1 2]
         "" ["abc"] [0 1 2 3]
         "" ["ab" "" "" "" "" "" "cd" "e"] [0 1 2 3 4 5]
         "a" [""] []
         "a" ["a"] [0]
         "a" ["a" "a" "a" "aaaa"] [0 1 2 3 4 5 6]
         "aa" ["aaaaaaaaaaaaa"] [0 1 2 3 4 5 6 7 8 9 10 11]
         "abc" [""] []
         "abc" ["rogue"] []
         "abc" ["abc"] [0]
         "abc" ["abc"] [0]
         "abc" ["1abc"] [1]
         "abc" ["ababc"] [2]
         "abc" ["ab" ""  "bd" "ab" "c"] [4]
         "abc" ["ab" "ca" "bc" "ab" "ca"] [0 3 6]
         "abc" ["ab" "ca" "bc" "ab" "cabraca" "abacab" "collate"] [0 3 6 19]
         "abc" ["abcabcabc"] [0 3 6])))
