(ns kmp-search.core-test
  (:require [clojure.test :refer [deftest is are testing]])
  (:import (kmp_search Context)))

(defn the-bytes [^String x]
  (.getBytes x))

(defn all-matches
  [pattern byte-arrays]
  (loop [context (Context. pattern)
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

(deftest position-test
  (let [buf (the-bytes "abcdefghi")
        nothing (the-bytes "")
        context (Context. (the-bytes "def"))
        context1 (.search context buf)
        context2 (.search context1 buf)
        context3 (.search context2 buf)
        context4 (.search context3 buf)
        context5 (.search context4 buf)
        context6 (.search context5 buf)
        context7 (.search context6 nothing)]
    (is (= 0 (.position context)))
    (is (= nil (.start context)))
    (is (= nil (.end context)))
    (is (= 0 (.position context1)))
    (is (= 3 (.start context1)))
    (is (= 6 (.end context1)))
    (is (= 9 (.position context2)))
    (is (= nil (.start context2)))
    (is (= nil (.end context2)))
    (is (= 9 (.position context3)))
    (is (= 12 (.start context3)))
    (is (= 15 (.end context3)))
    (is (= 18 (.position context4)))
    (is (= nil (.start context4)))
    (is (= nil (.end context4)))
    (is (= 18 (.position context5)))
    (is (= 21 (.start context5)))
    (is (= 24 (.end context5)))
    (is (= 27 (.position context6)))
    (is (= nil (.start context6)))
    (is (= nil (.end context6)))
    (is (= 27 (.position context7)))
    (is (= nil (.start context7)))
    (is (= nil (.end context7)))))

(deftest offset-test
  (let [buf (the-bytes "abcdefghiabcdefghiabcdefghi")
        context (Context. (the-bytes "def"))
        context1 (.search context buf 0 9)
        context2 (.search context1 buf 0 9)
        context3 (.search context2 buf 9 18)
        context4 (.search context3 buf 9 18)
        context5 (.search context4 buf 18 27)
        context6 (.search context5 buf 18 27)
        context7 (.search context6 buf 27 27)]
    (is (= 0 (.position context)))
    (is (= nil (.start context)))
    (is (= nil (.end context)))
    (is (= 0 (.position context1)))
    (is (= 3 (.start context1)))
    (is (= 6 (.end context1)))
    (is (= 9 (.position context2)))
    (is (= nil (.start context2)))
    (is (= nil (.end context2)))
    (is (= 9 (.position context3)))
    (is (= 12 (.start context3)))
    (is (= 15 (.end context3)))
    (is (= 18 (.position context4)))
    (is (= nil (.start context4)))
    (is (= nil (.end context4)))
    (is (= 18 (.position context5)))
    (is (= 21 (.start context5)))
    (is (= 24 (.end context5)))
    (is (= 27 (.position context6)))
    (is (= nil (.start context6)))
    (is (= nil (.end context6)))
    (is (= 27 (.position context7)))
    (is (= nil (.start context7)))
    (is (= nil (.end context7)))))
