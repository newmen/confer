(ns confer.http.client
  (:require [clojure.string :as s]
            [java-http-clj.core :as http]
            [confer.config :as cfg])
  (:import [java.net URLEncoder]
           [javax.net.ssl SSLContext TrustManager X509TrustManager]))

(def default-headers
  {"Accept-Encoding" "gzip, deflate, br"
   "Accept-Language" "en-US,en;q=0.9,ru;q=0.8"
   "Cache-Control" "max-age=0"})

(defn- arg-encode
  [arg]
  (URLEncoder/encode (str arg) "UTF-8"))

(defn url-encode
  [args]
  (if (map? args)
    (let [real (filter (fn [[_ v]] (not (nil? v))) args)]
      (s/join \& (map #(s/join \= (map url-encode %)) real)))
    (arg-encode args)))

(defn- make-query
  [args]
  (let [query (url-encode args)]
    (if (empty? query)
      ""
      (str \? query))))

(defn- make-url
  [path args]
  (let [host (cfg/get-config :confluence :host)]
    (str host path (make-query args))))

(def insecure-context
  (let [context (SSLContext/getInstance "SSL")
        manager (reify X509TrustManager
                  (getAcceptedIssuers [_] nil)
                  (checkClientTrusted [_ _certs _authType])
                  (checkServerTrusted [_ _certs _authType]))
        trust (into-array TrustManager [manager])]
    (.init context nil trust (java.security.SecureRandom.))
    context))

(def insecure-client
  (http/build-client {:ssl-context insecure-context}))

(defn get*
  ([path query]
   (get* {} path query))
  ([headers path query]
   (let [full-url (make-url path query)
         response (http/send {:method :get
                              :uri full-url
                              :headers (merge default-headers headers)}
                             {:client insecure-client})]
     response)))

(defn post*
  ([path body]
   (post* path {} body))
  ([path query body]
   (post* {} path query body))
  ([headers path query body]
   (let [full-url (make-url path query)
         response (http/send {:method :post
                              :uri full-url
                              :headers (merge default-headers headers)
                              :body body}
                             {:client insecure-client})]
     response)))
