#!/usr/bin/env bb
(ns gh-graphql
  (:require
   [babashka.http-client :as http]
   [babashka.json :as json]
   [babashka.curl :as curl]
   [cheshire.core :as cheshire]
   [clojure.pprint :refer [pprint]]))

(def auth-token (System/getenv "GITHUB_AUTH"))

(def issue-query
  "
query ($query: String!, $last: Int) {
  search(type: ISSUE, query: $query, last: $last) {
    nodes {
      ... on Issue {
        title
        url
        repository {
          name
        }
        labels(first: 10) {
          nodes {
            name
          }
        }
      }
    }
  }
}
")

(defn run-query [query variables]
  (-> (http/post "https://api.github.com/graphql"
                 {:headers {"Content-Type" "application/json"
                            "Accept" "application/json"
                            "Authorization" (str "bearer " auth-token)}
                  :body (json/write-str {:query query
                                         :variables variables})})
      :body
      (cheshire/parse-string true)))

(defn query
  "Perform an HTTP graphql query"
  [request query variables]
  (-> (:request request)
      (http/request)
      :body
      (json/write-str {:query query
                       :variables variables})))

(def gh-issue
  {:request {:headers {"Content-Type" "application/json"
                       "Accept" "application/json"
                       "Authorization" (str "bearer " auth-token)}
             :uri "https://api.github.com/graphql"
             :method :post}})



(defn -main []
  (pprint
   (run-query graphql-query
              {:query "org:trovemoney is:issue is:open sort:updated"
               :last 100})))

(defn -main2 []
  (pprint
   (query gh-issue graphql-query
              {:query "org:trovemoney is:issue is:open sort:updated"
               :last 100})))

(when (= *file* (System/getProperty "babashka.file"))
  (-main))

(comment

  (-main)
  ,)
