(ns kmp-search.utils
  "Utilities supporting byte array matching"
  (:import (java.util Arrays)
           (java.util.regex Pattern)
           (kmp_search.utils ByteCharSequence)))

(defn byte-char-sequence
  "Returns a CharSequence on a byte buffer. Allows some regex matching
  on bytes. Each byte in the byte array is treated as an 8-bit
  character, expressed as the unsigned lower 8 bits of a Java UTF-16
  Character."
  ([^bytes data]
   (ByteCharSequence. data))
  ([^bytes data ^long offset ^long length]
   (ByteCharSequence. data offset length)))

(defn find-match
  "Finds matches for a regex in a range of bytes in a byte
  array. Returns data about a match: the overall start and end
  positions of the matched range and a vector of byte-arrays, each
  containing the bytes matched by a group in the regex. Returns nil on
  failure to match.

  Uses a ByteCharSequence class to leverage Java's Matcher on byte
  arrays via the CharSequence abstraction. This treats every byte as a
  logical character and it preserves all 8 bits of each byte. UTF-8 or
  other multibyte characters will be handled safely. Without this
  special treatment (like if we tried to convert the peek buffer
  contents into a big Java String for regex matching), we would risk
  things like the peek buffer ending in the middle of a multibyte
  character which wouldn't be handled well. With each byte treated as
  a character, regex matching, grouping and replacement (all that we
  are interested in here) will work safely and as expected."
  ([^Pattern pattern ^bytes buf ^long start ^long end]
   (let [byte-chars (ByteCharSequence. buf start (- end start))
         matcher (re-matcher pattern byte-chars)]
     (when (.find matcher)
       (letfn [(group-bytes [^long i]
                 (Arrays/copyOfRange buf (.start matcher i) (.end matcher i)))]
         {:start (.start matcher)
          :end (.end matcher)
          :groups (let [xfrm (map group-bytes)]
                    (into [] xfrm (range 1 (inc (.groupCount matcher)))))}))))
  ([^Pattern pattern ^bytes buf]
   (find-match pattern buf 0 (alength buf))))
