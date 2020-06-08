(ns kmp-search.test.core
  (:require [clojure.test :refer [deftest is are testing]]
            [kmp-search.core :refer [context]])
  (:import (kmp_search Context)))

(defn the-bytes [^String x]
  (.getBytes x))

(defn all-matches
  [pattern byte-arrays]
  (loop [^Context context (context pattern)
         byte-arrays byte-arrays
         matches []]
    (if byte-arrays
      (let [context (.search context (first byte-arrays))
            match (.start context)]
        (if match
          (recur context byte-arrays (conj matches match))
          (recur context (next byte-arrays) matches)))
      matches)))

(deftest a-test
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
         "abc" ["1abc"] [1]
         "abc" ["ababc"] [2]
         "abc" ["ab" ""  "bd" "ab" "c"] [4]
         "abc" ["ab" "ca" "bc" "ab" "ca"] [0 3 6]
         "abc" ["ab" "ca" "bc" "ab" "cabraca" "abacab" "collate"] [0 3 6 19]
         "abc" ["abcabcabc"] [0 3 6])))

(deftest offset-test
  (let [^Context context (context (the-bytes "def"))
        context1 (.search context (the-bytes "abcdefghi"))
        context2 (.search context1 (the-bytes "abcdefghi"))
        context3 (.search context2 (the-bytes "abcdefghi"))]
    (is (= 0 (.offset context)))
    (is (= nil (.start context)))
    (is (= nil (.end context)))

    (is (= 0 (.offset context1)))
    (is (= 3 (.start context1)))
    (is (= 6 (.end context1)))

    (is (= 9 (.offset context2)))
    (is (= nil (.start context2)))
    (is (= nil (.end context2)))

    (is (= 9 (.offset context3)))
    (is (= 12 (.start context3)))
    (is (= 15 (.end context3)))))
