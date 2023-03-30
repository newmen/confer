(ns confer.confluence.page
  (:require [clojure.string :as s]
            [confer.http.client :as http]
            [confer.confluence.login :as login]))

(defn- combine-headers
  []
  (let [cookies (login/get-cookies)]
    {"Cookie" (s/join "; " cookies)}))

(defn- fetch-page
  [headers page-id]
  (:body (http/get* headers
                    "/pages/viewpage.action"
                    {"pageId" page-id})))

(defn fetch-pages
  [page-ids]
  (let [headers (combine-headers)]
    (map (juxt identity (partial fetch-page headers)) page-ids)))

(comment
  
  (spit "page.html" (fetch-page (combine-headers) 2510971489))
  
  )
