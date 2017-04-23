(ns overpitch.time-scaling-test
  (:require [clojure.test :refer :all]
            [overpitch.time-scaling :refer :all]
            [overpitch.utils :refer :all]))

(deftest hann-window-test
  (is (== 0 (hann-window (- 3.1))))
  (is (== 0 (hann-window 0)))
  (is (== 0 (hann-window 1)))
  (is (== 0 (hann-window 2.3)))
  (is (almost-equal 1 (hann-window 0.5)))
  (is (almost-equal 0.5 (hann-window 0.25)))
  (is (almost-equal 0.5 (hann-window 0.75)))
  (is (< 0 (hann-window 0.1) 0.5))
  (is (< 0 (hann-window 0.82) 0.5))
  (is (< 0.5 (hann-window 0.44) 1))
  (is (< 0.5 (hann-window 0.57) 1)))

(deftest apply-hann-window-test
  (is (almost-equal [0 0.5 1 0.5 0] (apply-hann-window [1 1 1 1 1])))
  (is (almost-equal [0 (- 0.5) -1 (- 0.5) 0] (apply-hann-window [-1 -1 -1 -1 -1]))))

(deftest complex-conversion-test
  ; Make a template test to apply to multiple series of values
  (are [magnitudes phases real-parts imaginary-parts]
    ; Test both directions of conversion
    (and
      (almost-equal
        {:magnitudes magnitudes :phases phases}
        (rectangular-to-polar real-parts imaginary-parts))
      (almost-equal
        {:real-parts real-parts :imaginary-parts imaginary-parts}
        (polar-to-rectangular magnitudes phases)))
    [0 1] [0 0] [0 1] [0 0]
    [1] [Math/PI] [-1] [0]
    [1] [(- (/ Math/PI 4))] [(/ (Math/sqrt 2) 2)] [(/ (- (Math/sqrt 2)) 2)]
    ; All at once
    [0 1 1 1] [0 0 Math/PI (- (/ Math/PI 4))] [0 1 -1 (/ (Math/sqrt 2) 2)] [0 0  0 (/ (- (Math/sqrt 2)) 2)]))

(defn generate-cos
  ([k]
    (generate-cos k 1))
  ([k factor]
    (generate-cos k factor 0))
  ([k factor phase]
    (Math/cos (+ phase (/ (* factor 2 Math/PI k) frame-size)))))

(defn generate-zeros-frame
  [& values]
    (concat values (repeat (- frame-size (count values)) 0)))

(deftest fft-test
  (are [magnitudes phases signal-function]
    ; We apply the fft on the signal generated by the signal function called
    ; with all the numbers in the range [0, frame-size[ and compare the result
    ; to the expected magnitudes and phases
    (almost-equal
      {:magnitudes magnitudes :phases phases}
      (fft (mapv signal-function (range frame-size))))
    ; All zeros
    (generate-zeros-frame)
    (generate-zeros-frame)
    (constantly 0)
    ; Testing one harmonic.
    ; In principle, the magnitude should be 1. But since it is split between
    ; negative and positive frequencies, it should be 1/2. But it is finally
    ; length/2 because the fft does not scale the output, so all magnitudes
    ; get multiplied by frame-size.
    (generate-zeros-frame 0 (/ frame-size 2))
    (generate-zeros-frame)
    generate-cos
    ; Testing two harmonics
    (generate-zeros-frame 0 (/ frame-size 2) (/ frame-size 2))
    (generate-zeros-frame)
    #(+ (generate-cos %) (generate-cos % 2))
    ; Testing one dephased harmonic
    (generate-zeros-frame 0 (/ frame-size 2))
    (generate-zeros-frame 0 0.1)
    #(generate-cos % 1 (/ Math/PI 5))))

(deftest map-phase-test
  (are [input expected]
    (almost-equal expected (map-phase input))
    0 0
    0.499 0.499
    0.5 0.5
    0.5001 -0.4999
    1.01 0.01
    52.2 0.2
    -0.499 -0.499
    -0.5001 0.4999
    -0.5 0.5
    -1.1 -0.1
    -0.64 0.36))

(deftest phase-vocoder-test
  (are [expected-frequencies frequencies phases next-phases analysis-hoptime]
    (almost-equal expected-frequencies (phase-vocoder frequencies phases next-phases analysis-hoptime))
      [1.3 1.3] [1 1] [0 0] [0.3 0.3] 1
      [0.9 1.9] [1 2] [0 0] [0.9 0.9] 1))
