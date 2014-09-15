(ns kmp-search.test.core
  (:require [clojure.test :refer :all]
            [kmp-search.core :refer :all]))

(defn index-of-reducer
  [[_ matcher] bytes]
  (let [[index matcher] (search-bytes matcher bytes)]
    (if index
      (reduced [index matcher])
      [index matcher])))

(defn all-matches
  [matcher byte-arrays]
  (loop [matcher matcher
         byte-arrays byte-arrays
         matches []]
    (if byte-arrays
      (let [[index matcher] (search-bytes matcher (first byte-arrays))]
        (if index
          (recur matcher byte-arrays (conj matches index))
          (recur matcher (next byte-arrays) matches)))
      matches)))

(defn the-bytes [x]
  (.getBytes x))

(defn test-index [m x]
  (let [[index _] (if (string? x)
                    (search-bytes m (the-bytes x))
                    (reduce index-of-reducer [0 m] (map the-bytes x)))]
    index))

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
  (testing "matches"
    (let [m (matcher (the-bytes "abc"))]
      (are [x y] (= (test-index m x) y)
           "" nil
           "rogue" nil
           "abc" 0
           "1abc" 1
           "ababc" 2
           ["ab" ""  "bd" "ab" "c"] 4)))
  (testing "all-matches"
    (let [m (matcher (the-bytes "abc"))]
      (are [x y] (= (all-matches m (map the-bytes x)) y)
           ["abc"] [0]
           ["abcabcabc"] [0 3 6]
           ["ab" "ca" "bc" "ab" "ca"] [0 3 6]
           ["ab" "ca" "bc" "ab" "cabraca" "abacab" "collate"] [0 3 6 19])))
  (testing "matching empty"
    (let [m (matcher (the-bytes ""))]
      (are [x y] (= (test-index m x) y)
           "" 0
           "rogue" 0
           "abc" 0
           "1abc" 0
           "ababc" 0
           ["ab" ""  "bd" "ab" "c"] 0)))
  (testing "matching empty"
    (let [m (matcher (the-bytes "aa"))]
      (are [x y] (= (all-matches m (map the-bytes x)) y)
           ["aaaaaaaaaaaaa"] [0 1 2 3 4 5 6 7 8 9 10 11]))))
