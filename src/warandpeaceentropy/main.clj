(ns warandpeaceentropy.main
  (:require
    [clojure.string :as str])
  (:gen-class)
)

; reads file and returns vector of character clumps
(defn readfile
  [filename charactersplit]
  (print "Reading file . . . ")
  (def file (slurp filename))
  (def array (vector))
  (def numberofchars (count file))
  (loop [x 0]
    (when (< x numberofchars)
      (def array (conj array (subs file x (min numberofchars (+ x charactersplit)))))
      (recur (+ x charactersplit)))
  )
  (println ". . . file read")
  (identity array)
)

; returns the amount of entropy in the specified file with the specified character split
(defn getEntropySingleThread
  [occurrance_counts total]

  ; log base 2
  (defn log2 [n]
    (/ (Math/log n) (Math/log 2)))

  ; function defined in assignment
  (defn termFunction [n]
    (def n_c n)
    (def p_c (/ n total))
    (* n_c (* (* -1 p_c) (log2 p_c))))

  (print "Calculating entropy . . . ")
  (def result (map termFunction occurrance_counts))
  (println ". . . entropy calculated")
  (reduce + result)
)

(defn getEntropyMultiThread2
  [occurrance_counts total]
  (defn singleThreadWrapper [oc] (getEntropySingleThread oc total))
  (def chunk_size (int (Math/ceil (/ (count occurrance_counts) 2))))
  (println "CHUNK SIZE")
  (println chunk_size)
  (def partitioned (split-at chunk_size occurrance_counts))
  (reduce + (doall (pmap singleThreadWrapper partitioned)))
)

(defn getEntropyMultiThread4
  [occurrance_counts total]
  (defn doubleThreadWrapper [oc] (getEntropyMultiThread2 oc total))
  (def chunk_size (int (Math/ceil (/ (count occurrance_counts) 2))))
  (println "CHUNK SIZE")
  (println chunk_size)
  (def partitioned (split-at chunk_size occurrance_counts))
  (reduce + (doall (pmap doubleThreadWrapper partitioned)))
)

(defn getEntropyMultiThread8
  [occurrance_counts total]
  (defn quadThreadWrapper [oc] (getEntropyMultiThread4 oc total))
  (def chunk_size (int (Math/ceil (/ (count occurrance_counts) 2))))
  (println "CHUNK SIZE")
  (println chunk_size)
  (def partitioned (split-at chunk_size occurrance_counts))
  (reduce + (doall (pmap quadThreadWrapper partitioned)))
)

(defn getEntropyMultiThread16
  [occurrance_counts total]
  (defn octThreadWrapper [oc] (getEntropyMultiThread8 oc total))
  (def chunk_size (int (Math/ceil (/ (count occurrance_counts) 2))))
  (println "CHUNK SIZE")
  (println chunk_size)
  (def partitioned (split-at chunk_size occurrance_counts))
  (reduce + (doall (pmap octThreadWrapper partitioned)))
)

(defn getEntropyMultiThread32
  [occurrance_counts total]
  (defn sixteenThreadWrapper [oc] (getEntropyMultiThread16 oc total))
  (def chunk_size (int (Math/ceil (/ (count occurrance_counts) 2))))
  (println "CHUNK SIZE")
  (println chunk_size)
  (def partitioned (split-at chunk_size occurrance_counts))
  (reduce + (doall (pmap sixteenThreadWrapper partitioned)))
)

(defn getEntropyMultiThread64
  [occurrance_counts total]
  (defn thirtytwoThreadWrapper [oc] (getEntropyMultiThread32 oc total))
  (def chunk_size (int (Math/ceil (/ (count occurrance_counts) 2))))
  (println "CHUNK SIZE")
  (println chunk_size)
  (def partitioned (split-at chunk_size occurrance_counts))
  (reduce + (doall (pmap thirtytwoThreadWrapper partitioned)))
)

; main function
(defn -main
  [& args]

  (def filename "Shorter.txt")
  (println "________Started________")
  (def start (System/currentTimeMillis))
  (def end (System/currentTimeMillis))
  (if (.exists (clojure.java.io/file filename))
    (do
      (def charactersplit 3)
      (def file (readfile filename charactersplit))
      ; takes in vector of character clumps and returns a hash-map of occurrence counts
      (def occurrance_counts (vals (frequencies file)))
      (def end (System/currentTimeMillis))
      (println (format "Readtime: %d ms" (- end start)))
      (def start (System/currentTimeMillis))
      (def total (reduce + occurrance_counts))
      (def entropy (getEntropyMultiThread64 occurrance_counts total))
      (println (format "\n________Finished________\nTotal entropy: %s" (str entropy))))
    (println (format "File \"%s\" not found" filename)))
  (def end (System/currentTimeMillis))
  (println (format "Runtime: %d ms" (- end start)))
  (System/exit 0)
)

; lein run
; lein repl
; (-main)
; https://youtu.be/ciGyHkDuPAE?t=2964
