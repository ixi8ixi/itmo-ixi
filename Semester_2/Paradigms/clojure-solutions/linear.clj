(defn testLen [v l] (= (count v) l))
(defn eqLen? [a b] (= (count a) (count b)))

(defn allVectors? [ops]
  (and (> (count ops) 0)
    (every? #(and (vector? %) (every? number? %) (eqLen? (first ops) %)) ops)))

(defn postVector? [v chv dim]
  (and (vector? v) (eqLen? v chv) (every? number? v)))

(defn matrix? [mx]
  (and (vector? mx) (or (testLen mx 0) (allVectors? mx))))

(defn doubleEqLen? [a b]
  (and (eqLen? a b) (or (= (count a) 0) (eqLen? (first a) (first b)))))

(defn allMatrices? [ops]
  (and (> (count ops) 0) (every? #(and (matrix? %) (doubleEqLen? (first ops) %)) ops)))

(defn postMatrix? [mx chmx dim]
  (and (matrix? mx) (doubleEqLen? mx chmx)))

(defn getSimplexDim [s dim]
  (cond
    (vector? s) (recur (first s) (inc dim))
    (number? s) dim
    :else -1))

(defn simplex? [s len dim]
  (and (vector? s) (= len (count s)) (> dim 0)
    (or
      (and (= dim 1) (every? number? s))
      (every? true? (mapv #(simplex? %1 %2 (dec dim)) s (range len 0 -1))))))

(defn allSimplices? [ops]
  (let [dim (getSimplexDim (first ops) 0)]
    (every? #(simplex? % (count (first ops)) dim) ops)))
(defn postSimplex? [s len dim] (simplex? s (count len) dim))

(defn abstractOp [f preTest postTest]
  (fn th [& operands]
    {:pre [(preTest operands)]
     :post [(postTest % (first operands) (getSimplexDim (first operands) 0))]}
    (letfn [(abstRec [& currentOps]
              (apply mapv (fn [& t]
                            (cond
                              (number? (first t)) (apply f t)
                              :else (apply abstRec t))) currentOps))]
      (apply abstRec operands))
    ))

(defn abstractVector [f] (abstractOp f allVectors? postVector?))
(def v+ (abstractVector +))
(def v- (abstractVector -))
(def v* (abstractVector *))
(def vd (abstractVector /))

(defn abstractMatrix [f] (abstractOp f allMatrices? postMatrix?))
(def m+ (abstractMatrix +))
(def m- (abstractMatrix -))
(def m* (abstractMatrix *))
(def md (abstractMatrix /))

(defn abstractSimplex [f] (abstractOp f allSimplices? postSimplex?))
(def x+ (abstractSimplex +))
(def x- (abstractSimplex -))
(def x* (abstractSimplex *))
(def xd (abstractSimplex /))

(defn mulByNumber [func preTest postTest]
  (fn [a & scs]
    {:pre [(and (every? number? scs) (preTest a))]
     :post [(postTest % a 0)]}
    (mapv #(func % (apply * scs)) a)))
(def v*s (mulByNumber * vector? postVector?))
(def m*s (mulByNumber v*s matrix? postMatrix?))

(defn scalar [& vectors]
  {:pre [(allVectors? vectors)]
   :post [(number? %)]}
  (apply + (apply v* vectors)))

(defn crossMul [v1 v2 c1 c2]
  (- (* (get v1 c1) (get v2 c2)) (* (get v1 c2) (get v2 c1))))
(defn cc [v1 v2 col]
  (case col 0 (crossMul v1 v2 1 2), 1 (crossMul v2 v1 0 2), 2 (crossMul v1 v2 0 1)))
(defn vect [& vs]
  {:pre [(and (allVectors? vs) (testLen (first vs) 3))]
   :post [(and (vector? %) (testLen % 3))]}
  (reduce #(identity [(cc %1 %2 0) (cc %1 %2 1) (cc %1 %2 2)]) vs))

(defn m*v [m v]
  {:pre [(and (matrix? m) (vector? v)
              (or (= 0 (count v) (count m)) (eqLen? (first m) v)))]
   :post [(and (vector? %) (every? number? %) (eqLen? % m))]}
  (mapv #(scalar % v) m))

(defn transpose [mx]
  {:pre [(matrix? mx)]
   :post [(and (matrix? %)
               (or
                 (and (or (testLen mx 0) (testLen (first mx) 0)) (testLen % 0))
                 (and (eqLen? mx (first %)) (eqLen? (first mx) %))))]}
  (cond (testLen mx 0) [] :else (apply mapv vector mx)))

(defn rm ([a b]
          {:pre [(and (matrix? a) (matrix? b) (or (= (count a) (count b) 0) (eqLen? (first a) b)))]
           :post [(and (matrix? %)
                       (or (= (count a) (count %) 0)
                         (and (eqLen? a %) (eqLen? (first b) (first %)))))]}
          (transpose (mapv #(m*v a %) (transpose b))))
  ([a] {:pre [(matrix? a)]} (identity a)))
(defn m*m [& mxs] (reduce rm mxs))
