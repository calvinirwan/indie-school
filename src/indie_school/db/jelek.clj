(ns indie-school.db.jelek
  (:require 
            [ring.util.anti-forgery :refer [anti-forgery-field]]))


(def znet-user
  [{:email "cal@g.com"
    :pass "tiger"
    :status "regular"}
   {:email "macaca@g.com"
    :pass "lebron"
    :status "premium"}
   {:email "a"
    :pass "a"
    :status "regular"}])

(def indie-user
  [{:id-indie "1"
    :nama-sekolah "sma lajsdl"
    :ip-sekolah "127.0.0.1"}
   {:id-indie "10"
    :nama-sekolah "sma tsundere"
    :ip-sekolah "1"}
   {:id-indie "13"
    :nama-sekolah "sma tigertsundere"
    :ip-sekolah "190.0.0.1"}])
