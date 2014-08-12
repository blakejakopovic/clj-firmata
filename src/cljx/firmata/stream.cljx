(ns firmata.stream
  #+clj
  (:require [clojure.core.async :refer [go timeout]]
            [serial.core :as serial])
  #+cljs
  (:require [cljs.core.async :refer [timeout]]
            [cljs.nodejs :as nodejs])
  #+cljs
  (:require-macros
    [cljs.core.async.macros :refer [go]])
  #+clj (:import [java.net InetSocketAddress Socket]))

(defprotocol FirmataStream
  "A FirmataStream provides methods for creating connections, writing
  values to and listening for events on a Firmata-enabled device."

  (open! [this] "opens the stream")
  (close! [this] "closes the stream")

  (listen [this handler] "listens for data on this stream")
  (write [this data]))


#+clj
(defrecord SerialStream [port-name baud-rate]
  FirmataStream

  (open! [this]
    (let [serial-port (serial/open port-name :baud-rate baud-rate)]
      (assoc this :serial-port serial-port)))

  (close! [this]
    (when-let [serial-port (:serial-port this)]
      (serial/close serial-port)
      (dissoc this :serial-port)))

  (write [this data]
    (when-let [serial-port (:serial-port this)]
      (serial/write serial-port data)))

  (listen [this handler]
    (when-let [serial-port (:serial-port this)]
      (serial/listen serial-port handler false))))


#+clj
(defrecord SocketStream [host port]
  FirmataStream

  (open! [this]
    (let [addr (InetSocketAddress. (:host this) (:port this))
          socket (Socket.)]
      (try
        (do
          (.setSoTimeout socket 0)
          (.connect socket addr)
          (assoc this :socket socket))
        (catch java.net.SocketException se
          ; TODO: Is there a better way to deal with this?
          (throw (RuntimeException. (str "Unable to connect to " host ":" port) se))))))

  (close! [this]
    (when-let [socket (:socket this)]
      (.close (:socket this))
      (dissoc this :socket)))

  (write [this data]
    ; NOTE: This relies on the fact that we're using clj-serial,
    ; so we can use serial/to-bytes here
    (when-let [socket (:socket this)]
      (let [output-stream (.getOutputStream socket)]
        (.write output-stream (serial/to-bytes data))
        (.flush output-stream))))

  (listen [this handler]
    (when-let [socket (:socket this)]
      (go
        (while (.isConnected socket)
          (try
            (handler (.getInputStream socket))
            (catch java.net.SocketException se)))))))

#+cljs
(def SerialPort (.-SerialPort (nodejs/require "serialport")))

#+cljs
(defrecord SerialStream [port-name baud-rate]
  FirmataStream

  (open! [this]
    (let [serial-port (new SerialPort (:port-name this) #js {:baudrate (:baud-rate this)})]
      (assoc this :serial-port serial-port)))

  (close! [this]
    (when-let [serial-port (:serial-port this)]
      (.close serial-port)
      (dissoc this :serial-port)))

  (listen [this handler]
    (when-let [serial-port (:serial-port this)]
      (.on serial-port "data" handler)))

  (write [this data]
    (when-let [serial-port (:serial-port this)]
      (.write serial-port data))))

; TODO: Implement me!
#+cljs
(defrecord SocketStream [host port]
  FirmataStream

  (open! [this])

  (close! [this])

  (write [this data])

  (listen [this handler]))
