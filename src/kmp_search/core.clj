(ns kmp-search.core
  "functions to search a byte stream for a byte pattern

  - create a context with the byte pattern

  - call the search function repeatedly to process each sequential
    chunk of stream contents
    - could be a complete byte array or a byte array, offset,
      and length

  - each call to search returns a new context when it:
    - finds a match within a chunk, or
    - exhausts a chunk without finding a match

  - retrieve the search result from the new context using the
    start or end functions. The result is either:

      - if a match was found, a Long containing the offset of the
        start or end of the match within the entire stream of bytes
        processed by the context, or

      - if no match was found, nil

  - each returned context contains enough state continue the search
    either after a match or across buffer boundaries

  - to begin searching for a new pattern starting at the position in
    the stream held by a previous context, call the focus function on
    the new context, passing in the previous context

  - to find all matches, call search on each buffer repeatedly until
    match returns nil, then continue searching the next buffer

    - in each call to search use the context returned by the previous
      call

  - search-file is useful both in its own right and as an example

  reference: http://www.inf.fh-flensburg.de/lang/algorithmen/pattern/kmpen.htm"
  (:require [clojure.java.io :as io])
  (:import (kmp_search Context)))

(defn search-file
  "returns a vector containing all the offsets where the start of a byte
  pattern appears within a file"
  [^bytes pattern file & {:keys [buffer-size] :or {buffer-size 1024}}]
  (let [buffer (byte-array buffer-size)]
    (with-open [ins (io/input-stream file)]
      (loop [context (Context. pattern)
             read-count (.read ins buffer)
             matches []]
        (if (neg? read-count)
          matches
          (let [new-context (.search context buffer 0 read-count)]
            (if-let [match (.start new-context)]
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
;; > (search-file (.getBytes "local") "/etc/hosts" :buffer-size 13)
;; [10 113 148]
;; > (search-file (.getBytes "local") "/etc/hosts" :buffer-size 71)
;; [10 113 148]
