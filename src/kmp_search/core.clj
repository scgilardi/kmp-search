(ns kmp-search.core
  "functions to search a byte stream for a byte pattern

  - create a context with the byte pattern

  - call the search function repeatedly to process each sequential
    buffer of stream contents

  - each call to search returns a new context when it:
    - finds a match within a buffer, or
    - exhausts a buffer without finding a match

  - retrieve the result a search from the new context using the
    match function. The result is either:

      - if a match was found, a Long containing the offset of the
        match within the stream of bytes processed, or

      - if no match was not found, nil

  - each returned context also contains enough state to allow matching
    across buffer boundaries

  - to find all matches, call search on each buffer repeatedly until
    match returns nil, then continue searching the next buffer

    - the context for each call to search will be the context returned
      by the previous call

  reference: http://www.inf.fh-flensburg.de/lang/algorithmen/pattern/kmpen.htm"
  (:require [clojure.java.io :as io])
  (:import (kmp_search Context)))

(defprotocol Search
  (search
    [this data]
    [this data limit])
  (match
    [this]))

(extend-type Context
  Search
  (search
    ([this data]
     (search this data (count data)))
    ([this data limit]
     (.search this data limit)))
  (match
    [this]
    (.match this)))

(defn context [^bytes pattern]
  (Context. pattern))

(defn search-file
  "returns the index of the first occurreence of a byte pattern in a
  file or nil if the pattern is not present."
  [^bytes pattern file & {:keys [buffer-size] :or {buffer-size 1024}}]
  (let [buffer (byte-array buffer-size)]
    (with-open [ins (io/input-stream file)]
      (loop [c (context pattern)
             read-count (.read ins buffer)]
        (if-not (neg? read-count)
          (let [c (search c buffer read-count)]
            (or (match c)
                (recur c (.read ins buffer)))))))))
