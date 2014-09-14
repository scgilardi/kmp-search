(ns kmp-search.core
  "searches a byte array for a byte pattern")

(defn match-length
  [^bytes pattern ^longs failures ^long match-length ^long b]
  (let [match-length (loop [j match-length]
                       (if (and (> j 0) (not= (aget pattern j) b))
                         (recur (aget failures (dec j)))
                         j))]
    (if (= (aget pattern match-length) b)
      (inc match-length)
      match-length)))

(defn matcher [^bytes pattern]
  (let [length (alength pattern)
        failures (long-array length)
        compute-failure (fn [[i j] b]
                          (let [j (match-length pattern failures j b)]
                            (aset-long failures i j)
                            [(inc i) j]))
        _ (reduce compute-failure [1 0] (drop 1 pattern))]
    {:pattern pattern
     :length length
     :failures failures
     :state [0 0 0]}))

(defn reset [matcher]
  (assoc matcher :state [0 0 0]))

(defn index-of
  "returns [matcher index]"
  [matcher ^bytes data]
  (let [{:keys [pattern length failures state]} matcher
        [offset i j] state
        find-match (fn [[i j] b]
                     (let [j (match-length pattern failures j b)
                           match [(inc i) j]]
                       (if (= j length)
                         (reduced match)
                         match)))
        [i j] (reduce find-match [i j] data)
        offset (+ offset i)]
    (if (= j length)
      [(assoc matcher :state [offset i 0]) (- offset length)]
      [(assoc matcher :state [offset 0 j]) -1])))

(defn index-of-reducer
  [[matcher _] data]
  (let [[matcher index] (index-of matcher data)]
    (if (neg? index)
      [matcher index]
      (reduced [matcher index]))))

