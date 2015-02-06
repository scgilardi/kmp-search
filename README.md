# kmp-search

Uses a streaming-compatible version of the Knuth-Morris-Pratt
algorithm to search a byte stream for a byte pattern. The stream is
presented to a search context object as a succession of calls to
process buffers one at a time. The matching is correct independent of
the position of any buffer boundaries.

## References:
  - [Wikipedia: Knuth-Morris-Pratt](http://en.wikipedia.org/wiki/Knuth%E2%80%93Morris%E2%80%93Pratt_algorithm)
  - [Flensburg University CS Description](http://www.inf.fh-flensburg.de/lang/algorithmen/pattern/kmpen.htm)

## Usage

project.clj

[![Clojars Project](http://clojars.org/kmp-search/latest-version.svg)](http://clojars.org/kmp-search)

## License

Copyright © 2014 Stephen C. Gilardi

Distributed under the Eclipse Public License version 1.0
