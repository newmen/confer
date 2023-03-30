(ns confer.finder
  (:require [confer.common :as cmn]))

(declare find-main-content)
(defn- fetch-main-content
  [dom]
  (let [[tag attrs contents] (cmn/prepare dom)]
    (if (and (= tag :div)
             (= (:id attrs) "main-content"))
      contents
      (find-main-content contents))))

(defn find-main-content
  [dom]
  (reduce (fn [acc sub-dom]
            (or acc
                (when (cmn/tag? sub-dom)
                  (fetch-main-content sub-dom))))
          nil
          dom))
