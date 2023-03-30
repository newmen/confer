(ns confer.config
  (:require [clojure.java.io :as io]
            [aero.core :as aero]))

(defonce ^:private config
  (delay
    (aero/read-config (io/resource "config.edn"))))

(defn get-config
  [& args]
  (get-in @config args))

(comment
  
  (get-config :confluence :host)
  (get-config :confluence :user)
  (get-config :confluence :password)
  
  )


