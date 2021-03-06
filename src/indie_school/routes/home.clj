(ns indie-school.routes.home
  (:require [indie-school.layout :as layout]
            [compojure.core :refer [defroutes GET POST]]
            [ring.util.http-response :refer [ok]]
            [ring.util.response :refer [redirect response]]
            [clojure.java.io :as io]
            [ring.util.anti-forgery :refer [anti-forgery-field]]
            [ring.middleware.anti-forgery :refer [*anti-forgery-token*]]
            [indie-school.db.jelek :refer [znet-user indie-user]]
            [indie-school.control.login :refer [get-znet-user
                                                get-user-status-znet
                                                session-znet?
                                                get-indie-school-user
                                                get-user-status-indie-school
                                                generate-indie-school-session
                                                session-indie-school?
                                                session-premium?]]
            [clj-http.client :as client]))

(defn set-user! [id {session :session}]
  (-> (str "User set to: " id)
      response
      (assoc :session (assoc session :user id))))

(defn remove-user! [{session :session}]
  (-> (response "")
      (assoc :session (dissoc session :user))))

(defn clear-session! []
  (dissoc (response "") :session))

(defn home-page [session]
  (layout/render
   "home.html" {:docs (-> "docs/docs.md" io/resource slurp)
                :session session}))

(defn about-page [session]
  (layout/render "about.html"
                 {:session session}))

(defn login-page [session]
  (layout/render
   "login.html"
   {:anti-forgery (anti-forgery-field)
    :session session}))

(defn znet-page [session]
  (layout/render
   "znet.html"
   {:anti-forgery (anti-forgery-field)
    :session session}))

(defn premium-page [session]
  (layout/render
   "premium.html"
   {:session session}))

(defroutes home-routes
  (GET "/" [:as req] (home-page (:session req)))
  (GET "/about" [:as req] (about-page (:session req)))
  (GET "/znet" req
       (let [session (:session req)]
         (if (session-znet? session)
           (znet-page (:session req))
           (redirect "/login"))))
  (POST "/znet"
        [indie-school-id :as req]
        (let [ip (:remote-addr req)
              indie-school (client/post "http://127.0.0.1:3000/indie-school"
                                        {:form-params {:indie-school-id indie-school-id
                                                       :ip ip}})
              session (:session req)
              body (read-string (:body indie-school))
              status (:status body)]
          (do (clojure.pprint/pprint status)
              (if status
                (-> (redirect "/znet")
                    (assoc :session (generate-indie-school-session session body)))
                (str body status)))))
  (POST "/indie-school"
        [indie-school-id ip :as req]
        (let [status (get-user-status-indie-school indie-school-id ip indie-user)]
          (str status)))
  (GET "/login" [:as req] (login-page (:session req)))
  (POST "/login"
        [usermail password :as req]
        (let [status (get-user-status-znet usermail password znet-user)
              session (:user status)]
          (if (:status status)
            (-> (redirect "/znet")
                (assoc :session {:user session}))
            (redirect "/login"))))
  (GET "/logout"
        [:as req]
        (let [a (redirect "/")]
          (assoc a :session (dissoc (:session a) :user))))
  (GET "/premium"
       req
       (let [session (:session req)]
         (if (session-premium? session)
           (premium-page session)
           (redirect "/znet")))))
