(ns confer.core
  (:require [clojure.string :as s]
            [hickory.core :as hickory]
            [hiccup.core :as hiccup]
            [confer.confluence.page :as page]
            [confer.finder :as finder]
            [confer.cleaner :as cleaner]
            [confer.title :as title]))

(defn- parse-page
  [content]
  (-> (hickory/parse content)
      hickory/as-hiccup
      finder/find-main-content
      cleaner/cleanup-content
      first))

(defn process-pages
  [page-ids & pages-path]
  (let [dir-path (str "pages/"
                      (when (seq pages-path)
                        (str (s/join "/" pages-path) "/")))]
    (->> (page/fetch-pages page-ids)
         (map (fn [[page-id html]]
                (let [content (parse-page html)
                      page-name (title/fetch-name-from-content content)
                      fixed-name (s/replace page-name "/" "---")]
                  [(str page-id " - " fixed-name)
                   content])))
         (map (fn [[title content]]
                (spit (str dir-path title ".html")
                      (hiccup/html content))))
         count)))

(comment

  (title/fetch-name-from-content
   (parse-page (slurp "page.html")))

  (process-pages [2510971480 2510971484 1359279695 2510971489] "artemis")
  (process-pages [573554922 710847367 784665552 778795145 955327683 839617485 784665555 955327684] "ibm-mq")

  )
