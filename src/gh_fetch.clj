(ns gh-fetch
  (:require [babashka.http-client :as http]
            [babashka.json :as json]
            ))

; Generic functions to perform HTTP requests.
(def auth-token (System/getenv "GITHUB_AUTH"))
(defn fetch
  "Perform an HTTP request, injecting a continuation key if supplied."
  [request & [cont-key]]
  (-> (:request request)
      (cond-> cont-key
        (assoc-in [:query-params (get-in request [:page :key-fn])] cont-key))
      (http/request)
      :body
      (json/read-str keyword)))

(defn fetch-all
  "Perform repeated HTTP requests using continuation keys."
  [request]
  (-> (iteration (partial fetch request)
                 :vf (get-in request [:page :val-fn])
                 :kf (get-in request [:page :key-fn]))
      seq
      flatten))

;; GH APIs
(def ghapi "https://api.github.com")

(def trove-orgs {:cs "trove-core-services"
                 :tempo "trovemoney"})

(def headers
  {"Accept" "application/vnd.github.json"
   "X-GitHub-Api-Version" "2022-11-28"
   "Authorization" (str "Bearer " auth-token )})

(def trove-code
  {:request {:headers headers
             :uri (str ghapi "/search/code" )
             :query-params {:q "javascript"
                            :org (:cs trove-orgs)}
             :body "trove-ci"
             :method :get}})

(def trove-repos
  {:request {:headers headers
             :uri "https://api.github.com/orgs/trovemoney/repos"
             :query-params {:topic "backend"}
             :method :get}
   :page {:val-fn :next
          :key-fn :link}})

(comment
  ;; fetch first page of trove-code)

  (-> (http/request {:uri {:scheme "https"
                           :host   "httpbin.org"
                           :port   443
                           :path   "/get"
                           :query  "q=test"}})
      :body
      (json/read-str true))



  (str ghapi "/orgs/trovemoney/repo")

  (fetch trove-code)
  (def repos (fetch trove-repos))
  (map :git_commits_url repos)
  ,)
