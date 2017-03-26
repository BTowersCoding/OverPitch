(ns overpitch.core
  (:require [overtone.live :as overtone]
            [clojure.java.io :as io]
            [overpitch.resampling :refer [resample]]
            [overpitch.time-scaling :refer [time-scale]]
            [overpitch.pitch-shifting :refer [pitch-shift]]))

(defn merge-channels
  [channels-data]
  (reduce into (apply mapv vector channels-data)))

(defn split-channels
  [input-data n-channels]
  (for [channel (range n-channels)]
    (vec
      (for [[i x] (map-indexed vector input-data)
            :when (= (mod i n-channels) channel)]
        x))))

(defn transform-wav
  "Transform the content of a wav file, and writes the result to the given path."
  [input-path output-path transformation scale]
  ; If scale is 1, just copy the file to the output-path
  (if (= scale 1)
    (io/copy (io/file input-path) (io/file output-path))
    ; else
    (let [input-buffer         (overtone/load-sample input-path)
          input-buffer-info    (overtone/buffer-info input-buffer)
          n-channels           (:n-channels input-buffer-info)
          input-data           (vec (overtone/buffer-data input-buffer))]
      (overtone/write-wav
        (merge-channels
          (for [channel (split-channels input-data n-channels)]
            (transformation channel scale)))
        output-path (:rate input-buffer-info) n-channels))))

(defn -main
  "Main function"
  [& args]
  (let [args (vec args)]
    (transform-wav (args 0) (args 1) pitch-shift (args 2))))
