(ns user
  (:require [babashka.deps :as deps]
            [clojure.edn :as edn])
  (:import (java.time Instant)))

(defmacro ->map [& ks]
  (assert (every? symbol? ks))
  (zipmap (map keyword ks)
          ks))

(defn lazy-concat [colls]
  (lazy-seq
   (when-first [c colls]
     (lazy-cat c (lazy-concat (rest colls))))))

(defn log [msg data]
  (prn {:msg msg
        :data data
        :timestamp (str (Instant/now))}))

(defn error [msg data]
  (log msg data)
  (throw (ex-info msg data)))

(defn refresh-deps []
  (-> (slurp "bb.edn")
      edn/read-string
      deps/add-deps))
