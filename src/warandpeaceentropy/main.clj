(ns warandpeaceentropy.main
  (:require
    [clojure.string :as str])
  (:gen-class)
)

; returns vector of character clumps
(defn split-file
  [file charactersplit]
  (def array (vector))
  (def numberofchars (count file))
  (loop [x 0]
    (when (< x numberofchars)
      (def item (subs file x (min numberofchars (+ x charactersplit))))
      (def array (conj array item))
      (recur (+ x charactersplit)))
  )
  (identity array)
)

; splits process into 'threads' number of threads
(defn getEntropyMultiThreaded
  [threads occurrance_counts total]

  ; log base 2
  (defn log2 [n]
    (/ (Math/log n) (Math/log 2))
  )

  ; function defined in assignment
  (defn termFunction [n]
    (def n_c n)
    (def p_c (/ n total))
    (* n_c (* (* -1 p_c) (log2 p_c)))
  )

  ; returns the amount of entropy in the specified file with the specified character split
  (defn getEntropySingleThreaded
    [beginindex endindex]
    (def result (map termFunction (subvec occurrance_counts beginindex endindex)))
    (reduce + result)
  )

  ; defined to match the size of the passed array
  (def beginindex 0)
  (def endindex (count occurrance_counts))
  ; if "threads < 2", use single threaded function
  ; else calulate with multiple threads
  (if (< threads 2)
    (getEntropySingleThreaded beginindex endindex)
    (do
      ; define the index groups based on the number of threads
      (def chunk_size (int (Math/ceil (/ endindex threads))))
      (def indexes (map-indexed
        (fn [idx itm] [idx itm] (vector (* idx chunk_size) (Math/min (+ (* idx chunk_size) chunk_size) endindex)))
        (vec (replicate threads 0)))
      )
      ; return the summation
      ; of the parallel map
      ; of the getEntropySingleThreaded
      ; of the index groups
      (reduce + (pmap (fn getEntropyMultiThreadedWrapper
        [index_group]
        (def b_i (nth index_group 0))
        (def e_i (nth index_group 1))
        (getEntropySingleThreaded b_i e_i)
      ) indexes))
    )
  )
)

; main function
(defn -main
  [& args]
  ; (def filename "Shorter.txt")
  (def filename "WarAndPeace.txt")
  (println "________Started________")
  (def start (System/currentTimeMillis))
  (def end (System/currentTimeMillis))
  (defn print-update
    [label nextlabel]
    (def end (System/currentTimeMillis))
    (println (format ". . . %s %s" label (format "time: %d ms" (- end start))))
    (def start (System/currentTimeMillis))
    (print (format "%s . . . " nextlabel))
  )

  (if (.exists (clojure.java.io/file filename))
    (do
      (def charactersplit 3)
      (print "Reading . . . ")
      (def filecontents (slurp filename))
      (print-update "Reading" "Splitting")
      (def file (split-file filecontents charactersplit))
      (def occurrance_counts (vec (vals (frequencies file))))
      (print-update "Splitting" "Caclulating")
      (def total (reduce + occurrance_counts))
      (def entropy (getEntropyMultiThreaded 64 occurrance_counts total))
      (print-update "Calculating" "Done")
      (println (format ". . . Entropy: %f" entropy))
    )
    (println (format "File \"%s\" not found" filename))
  )
  (System/exit 0)
)

; lein run
; lein repl
; (-main)
; https://youtu.be/ciGyHkDuPAE?t=2964
