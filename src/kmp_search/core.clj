(ns kmp-search.core
  "functions to search bytes for a byte pattern

  - create a context with the bytes of the pattern

  - call a search function with the context and the bytes to be searched

  - search-bytes returns an updated context which carries enough state
    to allow matching across the boundary if the byte-array is a
    portion of a larger search target.

  - see search-file for an example

  reference: http://www.inf.fh-flensburg.de/lang/algorithmen/pattern/kmpen.htm"
  (:require [clojure.java.io :as io])
  (:import (kmp_search Kernel)))

(defprotocol Search
  (search
    [this data]
    [this data limit]))

(extend-type Kernel
  Search
  (search
    ([this data]
       (search this data (count data)))
    ([this data limit]
       (vec (.search this data limit)))))

(defn context [^bytes pattern]
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
