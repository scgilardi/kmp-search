(ns kmp-search.core)

(defn match-length [pattern failures match-length b]
  (let [match-length (loop [j match-length]
                       (if (and (> j 0) (not= (aget pattern j) b))
                         (recur (failures (dec j)))
                         j))]
    (if (= (aget pattern match-length) b)
      (inc match-length)
      match-length)))

(defn matcher [pattern]
  (let [compute-failure (fn [[failures j] b]
                          (let [j (match-length pattern failures j b)]
                            [(conj failures j) j]))
        [failures _] (reduce compute-failure [[0] 0] (drop 1 pattern))]
    {:pattern pattern
     :failures failures
     :offset 0
     :i 0
     :j 0}))

(defn index-of
  "returns [matcher index]"
  [matcher data]
  (let [{:keys [pattern failures offset i j]} matcher
        length (count pattern)
        find-match (fn [[i j] b]
                     (let [j (match-length pattern failures j b)]
                       (if (= j length)
                         (reduced [(inc i) j])
                         [(inc i) j])))
        [i j] (reduce find-match [0 j] (drop i data))
        offset (+ offset i)
        matcher (assoc matcher :offset offset)]
    (if (= j length)
      [(assoc matcher :i i :j 0) (- offset length)]
      [(assoc matcher :i 0 :j j) -1])))

(defn index-of-reducer
  [[matcher _] data]
  (let [[matcher index] (index-of matcher data)]
    (prn matcher index)
    (if (neg? index)
      [matcher index]
      (reduced [matcher index]))))

(defn the-bytes [x]
  (.getBytes x))
