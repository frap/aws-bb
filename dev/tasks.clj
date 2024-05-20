(ns tasks
  (:require [babashka.process :as p]
            [clojure.string :as str]))

(defn add-dep [command-line-args]
  (let [args (or command-line-args ["--help"])
        neil-args (concat ["neil" "dep" "add" "--deps-file" "bb.edn"] args)
        {:keys [out]} (apply p/shell {:out :string} neil-args)]
    (if (= "--help" (first args))
      (->> [(str/replace out "Usage: neil add dep" "Usage: bb add-dep")
            "Examples:\n"
            "bb add-dep com.cognitect.aws/endpoints"
            "bb add-dep com.cognitect.aws/s3 --version 848.2.1413.0"
            "bb add-dep grzm/awyeah-api --latest-sha"]
           (str/join "\n")
           println)
      (println out))))

(comment
 (add-dep ["babashka/spec.alpha"])
 ,)
