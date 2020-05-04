(ns warandpeaceentropy.main
  (:require
    [clojure.string :as str])
  (:gen-class)
)

; define the index groups based on the number of threads
(defn get-indexes
  [some-size threads]
  (def chunk_size (int (Math/ceil (/ some-size threads))))
  (map-indexed
    (fn [idx itm] [idx itm] (vector (Math/min (* idx chunk_size) (- some-size 1)) (Math/min (+ (* idx chunk_size) chunk_size) some-size)))
    (vec (replicate threads 0))
  )
)

; splits process into 'threads' number of threads
(defn split-file-multi-threaded
  [threads file charactersplit]
  ; returns vector of character clumps
  (defn split-file-single-threaded
    [beginindex endindex]
    (def array (vector))
    (loop [x 0]
      (when (< x (- endindex beginindex))
        (def array (conj array (subs (subs file beginindex endindex) x (min (- endindex beginindex) (+ x charactersplit)))))
        (recur (+ x charactersplit)))
    )
    (identity array)
  )

  (defn conjresult [some-vector elements] (conj some-vector elements))
  (def indexes (get-indexes (count file) threads))

  (reduce conjresult (pmap (fn [index_group]
    (split-file-single-threaded (nth index_group 0) (nth index_group 1))
  ) indexes))
)

; splits process into 'threads' number of threads
(defn get-entropy-multi-threaded
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
  (defn get-entropy-single-threaded
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
    (get-entropy-single-threaded beginindex endindex)
    (do
      (def indexes (get-indexes endindex threads))
      ; return the summation
      ; of the parallel map
      ; of the get-entropy-single-threaded
      ; of the index groups
      (reduce + (pmap (fn [index_group]
        (get-entropy-single-threaded (nth index_group 0) (nth index_group 1))
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
  (def begin (System/currentTimeMillis))
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
      (def threads 1)
      (print "Reading . . . ")
      (def filecontents (slurp filename))
      (print-update "Reading" "Splitting")
      (def file (split-file-multi-threaded threads filecontents charactersplit))
      (print-update "Splitting" "Occurrances")
      (def occurrance_counts (vec (vals (frequencies file))))
      (print-update "Occurrances" "Caclulating")
      (def total (reduce + occurrance_counts))
      (def entropy (get-entropy-multi-threaded threads occurrance_counts total))
      (print-update "Calculating" "Done")
      (println (format ". . . Entropy: %f" entropy))
      (def end (System/currentTimeMillis))
      (println (format "Total time: %d ms" (- end begin)))
    )
    (println (format "File \"%s\" not found" filename))
  )
  (System/exit 0)
)

; lein run
; lein repl
; (-main)
; https://youtu.be/ciGyHkDuPAE?t=2964
