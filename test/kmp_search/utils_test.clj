(ns kmp-search.utils-test
  (:require [kmp-search.utils :as sut]
            [clojure.test :refer [deftest is]]))

(defn stringize-groups
  [{:keys [groups] :as x}]
  (let [groups (map #(String. ^bytes %) groups)]
    (assoc x :groups groups)))

(deftest test-find-match
  (let [test-bytes (.getBytes "abcdefghi")]
    (is (= {:start 3, :end 6, :groups []}
           (sut/find-match #"def" test-bytes)))
    (is (= {:start 3, :end 6, :groups []}
           (sut/find-match #"ghi" test-bytes 3 9)))
    (is (= {:start 2, :end 9, :groups ["def" "h"]}
           (stringize-groups (sut/find-match #"c(.+)g(.)i" test-bytes))))))
