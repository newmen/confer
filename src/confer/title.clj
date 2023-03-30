(ns confer.title
  (:require [confer.common :as cmn]))

(declare fetch-name-from-content)

(defn- td-name?
  [dom]
  (let [[tag _ contents] (cmn/prepare dom)]
    (and (= tag :td)
         (= (first contents) "Бизнес-имя"))))

(defn- fetch-name-from-tag
  [dom]
  (let [[tag _ contents] (cmn/prepare dom)]
    (if (and (= tag :tr)
             (td-name? (first contents)))
      (let [td-name (second contents)]
        (first (last (cmn/prepare td-name))))
      (fetch-name-from-content contents))))

(defn fetch-name-from-content
  [dom]
  (if (cmn/tag? dom)
    (fetch-name-from-tag dom)
    (reduce (fn [acc sub-dom]
              (or acc
                  (when (cmn/tag? sub-dom)
                    (fetch-name-from-tag sub-dom))))
            nil
            dom)))
