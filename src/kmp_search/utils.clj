(ns kmp-search.utils
  "Utilities supporting byte array matching"
  (:import (kmp_search.utils ByteCharSequence)))

(defn byte-char-sequence
  "returns a CharSequence on a byte buffer"
  [^bytes data offset length]
  (ByteCharSequence. data offset length))
