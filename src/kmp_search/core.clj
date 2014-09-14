(ns kmp-search.core
  "functions to search bytes for a byte pattern

  - create a matcher with the bytes of the pattern

  - call a search function with the matcher and the bytes to be searched

  - search-bytes returns an updated matcher which carries enough state
    to allow matching across the boundary if the byte-array is a
    portion of a larger search target."
  (:require [clojure.java.io :as io]))

(def byte-array-class (Class/forName "[B"))

(defn matcher [^bytes pattern]
  {:pre [(isa? byte-array-class (class pattern))]}
  (let [length (alength pattern)
        failure (long-array length)]
    (loop [i 1 j 0]
      (when (< i length)
        (let [b (aget pattern i)
              ^long j (loop [j j]
                        (let [p (aget pattern j)]
                          (if (and (pos? j) (not= p b))
                            (recur (aget failure (dec j)))
                            (if (= p b)
                              (inc j)
                              j))))]
          (aset-long failure i j)
          (recur (inc i) j))))
    {:pattern pattern
     :length length
     :failure failure
     :state [0 0 0]}))

(defn search-bytes
  "searches some or all of a byte array. returns the index of the
  first match (or nil if no match is found) and an updated matcher
  that can be used to continue the search in a subsequent call.
  matches within byte arrays or across byte array boundaries will
  be found."
  ([matcher bytes]
     (search-bytes matcher bytes (alength bytes)))
  ([matcher ^bytes bytes ^long limit]
     {:pre [(isa? byte-array-class (class bytes))]}
     (let [{:keys [^bytes pattern ^long length ^longs failure state]} matcher
           [offset i j] state
           [i j] (if (= j length)
                   [i j]
                   (loop [i i ^long j j]
                     (if (= i limit)
                       [i j]
                       (let [b (aget bytes i)
                             ^long j (loop [j j]
                                       (let [p (aget pattern j)]
                                         (if (and (pos? j) (not= p b))
                                           (recur (aget failure (dec j)))
                                           (if (= p b)
                                             (inc j)
                                             j))))]
                         (if (= j length)
                           [(inc i) j]
                           (recur (inc i) j))))))]
       (if (= j length)
         [(- (+ offset i) length) (assoc matcher :state [offset i 0])]
         [nil (assoc matcher :state [(+ offset i) 0 j])]))))

(defn search-file
  "returns the index of the first occurreence of a byte pattern in a
  file or nil if the pattern is not present."
  [^bytes pattern file & {:keys [buffer-size] :or {buffer-size 1024}}]
  (let [bytes (byte-array buffer-size)]
    (with-open [ins (io/input-stream file)]
      (loop [m (matcher pattern)
             read-count (.read ins bytes)]
        (if-not (neg? read-count)
          (let [[index m] (search-bytes m bytes read-count)]
            (or index
                (recur m (.read ins bytes)))))))))
