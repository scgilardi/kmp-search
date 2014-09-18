(ns kmp-search.core
  "functions to search bytes for a byte pattern

  - create a context with the bytes of the pattern

  - call a search function with the context and the bytes to be searched

  - search-bytes returns an updated context which carries enough state
    to allow matching across the boundary if the byte-array is a
    portion of a larger search target.

  reference: http://www.inf.fh-flensburg.de/lang/algorithmen/pattern/kmpen.htm"
  (:require [clojure.java.io :as io])
  (:import (kmp_search Kernel)))

(def byte-array-class (Class/forName "[B"))

(defprotocol Search
  (search
    [this data]
    [this data limit]))

(defn border
  [^bytes pattern]
  {:pre [(isa? byte-array-class (class pattern))]}
  (let [length (alength pattern)
        border (int-array (inc length))]
    (loop [i 0 j -1]
      (aset-int border i j)
      (when (< i length)
        (let [p (aget pattern i)
              j (int (loop [j j]
                       (if (or (neg? j) (= (aget pattern j) p))
                         j
                         (recur (aget border j)))))]
          (recur (inc i) (inc j)))))
    border))

(defrecord KMP
    [^bytes pattern ^int length ^ints border ^long offset ^int i ^int j]
  Search
  (search [this bytes]
    (search this bytes (count bytes)))
  (search [this bytes limit]
    {:pre [(isa? byte-array-class (class bytes))
           (<= limit (count bytes))]}
    (let [limit (int limit)
          [i j] (loop [i i j j]
                  (if (or (= i limit) (= j length))
                    [i j]
                    (let [b (aget ^bytes bytes i)
                          j (int (loop [j j]
                                   (if (or (neg? j) (= (aget pattern j) b))
                                     j
                                     (recur (aget border j)))))]
                      (recur (inc i) (inc j)))))]
      (if (= j length)
        [(+ offset (- i j)) (assoc this :offset offset :i i :j (aget border j))]
        [nil (assoc this :offset (+ offset i) :i 0 :j j)]))))

(extend-type Kernel
  Search
  (search
    ([this data]
       (search this data (count data)))
    ([this data limit]
       (.search this data limit))))

(defn _context [^bytes pattern]
  {:pre [(isa? byte-array-class (class pattern))]}
  (->KMP pattern (count pattern) (border pattern) 0 0 0))

(defn context [^bytes pattern]
  {:pre [(isa? byte-array-class (class pattern))]}
  (Kernel. pattern))

(defn search-file
  "returns the index of the first occurreence of a byte pattern in a
  file or nil if the pattern is not present."
  [^bytes pattern file & {:keys [buffer-size] :or {buffer-size 1024}}]
  (let [buffer (byte-array buffer-size)]
    (with-open [ins (io/input-stream file)]
      (loop [c (context pattern)
             read-count (.read ins buffer)]
        (if-not (neg? read-count)
          (let [[index c] (search c buffer read-count)]
            (or index
                (recur c (.read ins buffer)))))))))
