(ns confer.utils)

(defn update-if
  [hm key f & args]
  (if (contains? hm key)
    (apply (partial update hm key f) args)
    hm))
