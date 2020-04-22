(ns warandpeaceentropy.main
  (:require
    [clojure.string :as str]
    [com.climate.claypoole :as cp]
    [com.climate.claypoole :as cplazy])
  (:gen-class)
)

(defn myfun
  [x]
  (+ x 1))

; (defn -main
;     [& args]
;     ; (def pool (cp/threadpool 10 :daemon false))
;     (def pool (cp/threadpool 2))
;     (println (cp/pmap pool myfun (vector 1 2 3 4 5 6)))
;     (cp/shutdown pool))

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

; takes in vector of character clumps and returns a hash-map of occurrence counts
(defn countOccurrences
  [listofcharacterclumps]
  (print "Counting occurrences . . . ")
  (def occurrences (hash-map "a" 3))
  (def numberofclumps (count listofcharacterclumps))
  (loop [x 0]
    (when (< x numberofclumps)
      (def occurrences (merge-with + occurrences (hash-map (nth listofcharacterclumps x) 1)))
      (recur (+ x 1)))
  )
  (println ". . . occurrences counted")
  (vals occurrences)
)

; log base 2
(defn log2 [n]
  (/ (Math/log n) (Math/log 2)))

; function defined in assignment
(defn termFunction [n_c_p_c]
  (def n_c (nth n_c_p_c 0))
  (def p_c (nth n_c_p_c 1))
  (* n_c (* (* -1 p_c) (log2 p_c))))

; returns the amount of entropy in the specified file with the specified character split
(defn getEntropy
  [filename charactersplit]
  (def result (countOccurrences (readfile filename charactersplit)))
  (print "Calculating entropy . . . ")
  (def total (reduce + result))
  (defn n_cANDp_c [n] (vector n (/ n total)))
  (def output (map termFunction (map n_cANDp_c result)))
  (println ". . . entropy calculated")
  (identity (reduce + output))
)

; main function
(defn -main
  [& args]
  (def filename "WarAndPeace.txt")
  (println "________Started________")
  (def start (System/currentTimeMillis))
  (if (.exists (clojure.java.io/file filename))
    (do 
      (def entropy (getEntropy filename 1))
      (println (format "\n________Finished________\nTotal entropy: %f" entropy)))
    (println (format "File \"%s\" not found" filename)))
  (def end (System/currentTimeMillis))
  (println (format "Runtime: %f seconds" (/ (- end start) 1000.0)))
  (System/exit 0)
)

; lein run
; lein repl
; (-main)
; https://youtu.be/ciGyHkDuPAE?t=2964
