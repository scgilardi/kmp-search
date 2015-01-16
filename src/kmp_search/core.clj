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
    [this buffer]
    [this buffer limit])
  (match
    [this]))

(extend-type Context
  Search
  (search
    ([this buffer]
     (search this buffer (count buffer)))
    ([this buffer limit]
     (.search this buffer limit)))
  (match
    [this]
    (.match this)))

(defn context [^bytes pattern]
  (Context. pattern))

(defn search-file
  "returns a vector containing all the offsets where a byte pattern
  appears within a file"
  [^bytes pattern file & {:keys [buffer-size] :or {buffer-size 1024}}]
  (let [buffer (byte-array buffer-size)]
    (with-open [ins (io/input-stream file)]
      (loop [context (context pattern)
             read-count (.read ins buffer)
             matches []]
        (if (neg? read-count)
          matches
          (let [new-context (search context buffer read-count)
                match (match new-context)]
            (if match
              (recur new-context read-count (conj matches match))
              (recur new-context (.read ins buffer) matches))))))))

;; > (search-file (.getBytes "local") "/etc/hosts")
;; [10 113 148]
;; > (search-file (.getBytes "local") "/etc/hosts" :buffer-size 1)
;; [10 113 148]
;; > (search-file (.getBytes "local") "/etc/hosts" :buffer-size 2)
;; [10 113 148]
;; > (search-file (.getBytes "local") "/etc/hosts" :buffer-size 3)
;; [10 113 148]
;; > (search-file (.getBytes "local") "/etc/hosts" :buffer-size 71)
;; [10 113 148]
