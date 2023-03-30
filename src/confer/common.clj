(ns confer.common)

(defn tag?
  [dom]
  (and (not (string? dom))
       (coll? dom)
       (keyword? (first dom))))

(defn prepare
  [[tag attrs* & contents*]]
  (let [attrs (if (map? attrs*) attrs* nil)
        contents (if attrs
                   contents*
                   (cons attrs* contents*))]
    [tag attrs contents]))
