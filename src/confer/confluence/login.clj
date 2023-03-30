(ns confer.confluence.login
  (:require [clojure.string :as s]
            [confer.config :as cfg]
            [confer.http.client :as http]))

(defn- parse-cookies
  [response-headers]
  (->> (get response-headers "set-cookie")
       (map (fn [cookie]
              (first (s/split cookie #";"))))))

(defn- get-cookies'
  []
  (let [headers {"Content-Type" "application/x-www-form-urlencoded"}
        ccfg (cfg/get-config :confluence)
        body (http/url-encode {"os_username" (ccfg :user)
                               "os_password" (ccfg :password)
                               "os_cookie" "true"
                               "login" "Log in"
                               "os_destination" ""})
        response (http/post* headers "/dologin.action" {} body)]
    (parse-cookies (:headers response))))

(def get-cookies
  (memoize get-cookies'))
