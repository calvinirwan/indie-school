(ns indie-school.control.login
  (:require [indie-school.db.jelek :refer [znet-user indie-user]]))

(defn get-znet-user
  "searching if user-login map exist in user-znet map of coll"
  [user-login user-znet]
  (let [user-email (:email user-login)
        user-pass (:pass user-login)
        user (first (filter #(= user-email (:email %)) user-znet))]
    (if user
      (if (= user-pass (:pass user))
        {:status true :user user}
        {:status false :error "password salah"})
      {:status false :error "user ini ndak ada"})))

(defn get-user-status-znet
  [user pass user-znet]
  (get-znet-user {:email user :pass pass} user-znet))

(defn session-znet?
  [session]
  (:user session))

(defn get-indie-school-user
  "searching if user-login map exist in user-znet map of coll"
  [user-login user-indie]
  (let [user-indie-id (:indie-school-id user-login)
        user-ip (:ip user-login)
        user (first (filter #(= user-indie-id (:id-indie %)) user-indie))]
    (if user
      (if (= user-ip (:ip-sekolah user))
        {:status true :user user}
        {:status false :error "user tidak berada di sekolah"})
      {:status false :error "id tidak valid"})))

(defn get-user-status-indie-school
  [user-indie-school-id ip user-indie]
  (get-indie-school-user {:indie-school-id user-indie-school-id :ip ip} user-indie))

(defn generate-indie-school-session
  [session indie-status]
  (-> {:user (merge (:user session)
                    (:user indie-status))}
      (assoc-in [:user :status] "premium")))

(defn session-indie-school?
  [session]
  (let [user (:user session)]
    user))

(defn session-premium?
  [session]
  (let [user (:user session)
        status (:status user)]
    (= status "premium")))
