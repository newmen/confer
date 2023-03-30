(ns confer.cleaner
  (:require [clojure.string :as s]
            [confer.common :as cmn]
            [confer.utils :as u]))

(defn- any-expand-all?
  [contents]
  (->> (filter string? contents)
       (some #(s/includes? % "Expand All"))))

(defn- click-to-expand?
  [contents]
  (and (= (count contents) 1)
       (= (first contents) "Click here to expand...")))

(declare cleanup-content)

(defn- cleanup-attrs
  [attrs]
  (let [{:keys [id class style]} attrs
        classes (when class
                  (->> (s/split class #" ")
                       (remove #(s/starts-with? % "expand-"))))
        attrs (if (empty? classes)
                (dissoc attrs :class)
                (assoc attrs :class (s/join " " classes)))
        attrs (if (and id (s/starts-with? id "expander-"))
                (dissoc attrs :id)
                attrs)
        attrs (if (= style "display: none; opacity: 0;")
                (dissoc attrs :style)
                attrs)]
    (u/update-if attrs :href s/trim)))

(defn- cleanup-string
  [dom]
  (let [t (-> (s/trim dom)
              (s/replace " " "") ;; extra space
              (s/replace "\n" " ")
              (s/replace #"\s+" " ")
              (s/replace "-СУБО" "- СУБО"))]
    (when-not (= t "")
      t)))

(defn- cleanup-tag
  [dom]
  (let [[tag attrs contents] (cmn/prepare dom)]
    (when-not (#{:button :script :style} tag)
      (let [clean-contents (cleanup-content contents)]
        (when (or (= tag :br)
                  (= tag :td)
                  (seq clean-contents))
          (when-not (and (= tag :span)
                         (or (click-to-expand? clean-contents)
                             (any-expand-all? clean-contents)))
            (let [tag (if (#{:h1 :h2 :h3 :h4 :h5 :h6} tag) :b tag)]
              (vec (if attrs
                     (concat [tag (cleanup-attrs attrs)] clean-contents)
                     (cons tag clean-contents))))))))))

(defn cleanup-content
  [dom]
  (if (cmn/tag? dom)
    (cleanup-tag dom)
    (reduce (fn [acc sub-dom]
              (let [cf (cond
                         (string? sub-dom) cleanup-string
                         (cmn/tag? sub-dom) cleanup-tag
                         :else (throw (Exception. sub-dom)))
                    clean-dom (cf sub-dom)]
                (if clean-dom
                  (conj acc clean-dom)
                  acc)))
            []
            dom)))

(comment

  (cleanup-attrs {:id "844319391", :class "expand-control"})
  (cleanup-tag [:span {} " "])
  (cleanup-content [:span {} " "])
  (cleanup-string " ")

  )
