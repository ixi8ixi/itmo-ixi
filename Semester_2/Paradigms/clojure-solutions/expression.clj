(def constant constantly)
(defn variable [name] (fn [vars] (vars name)))

(defn abstractOp [f]
  (fn [& operands]
    (fn [vars] (apply f (mapv (fn [op] (op vars)) operands)))))

(defn checked-divide [& ops]
  (cond
    (= (count ops) 1) (if (== (first ops) 0) 0 (/ (first ops)))
    (every? #(not (zero? %)) (rest ops)) (apply / ops)
    :else 0))
(defn mean-impl [& ops] (/ (apply + ops) (count ops)))
(defn varn-impl [& ops] (-
                          (apply mean-impl (mapv #(* % %) ops))
                          (Math/pow (apply mean-impl ops) 2)))

(def negate (abstractOp unchecked-negate))
(def add (abstractOp +))
(def subtract (abstractOp -))
(def multiply (abstractOp *))
(def divide (abstractOp checked-divide))
(def mean (abstractOp mean-impl))
(def varn (abstractOp varn-impl))

(load-file "proto.clj")

(def evaluate (method :evaluate))
(def diff (method :diff))
(def toString (method :toString))
(def toStringInfix (method :toStringInfix))
(def sign (field :sign))

(def op (field :op))

(declare Constant)
(defn ConstImpl [this value]
  (assoc this :value value))
(def ConstProto {:evaluate (fn [this vars] (:value this))
                 :diff (fn [this var] (Constant 0))
                 :toString (fn [this] (str (this :value)))
                 :toStringInfix toString})
(def Constant (constructor ConstImpl ConstProto))

(def ZERO (Constant 0))
(def ONE (Constant 1))

(def VarProto
  {:evaluate (fn [this vars] (vars (this :trueName)))
   :diff (fn [this var] (cond
                          (= (this :trueName) var) ONE
                          :else ZERO))
   :toString (fn [this] (str (this :name)))
   :toStringInfix toString})
(defn VarImpl [this name] (assoc this :name name :trueName (clojure.string/lower-case (subs name 0 1))))
(def Variable (constructor VarImpl VarProto))

(def OpProto
  {:evaluate (fn [this vars] (apply (op this) (map #(evaluate % vars) (this :args))))
   :toString (fn [this] (str "(" (sign this) (cond
                                               (empty? (this :args)) " "
                                               :else (apply str (map #(str " " (toString %)) (this :args)))) ")"))
   :toStringInfix (fn [this] (str "(" (apply str
                                     (cons
                                       (toStringInfix (first (this :args)))
                                       (map #(str " " (sign this) " " (toStringInfix %)) (rest (this :args))))) ")"))})

(defn AbstOp [op sign diff & infix]
  (constructor
    (fn [this & args]
      (assoc this :args args))
    (let [proto (assoc {:prototype OpProto} :op op :sign sign :diff diff)]
      (cond
        (== 0 (count infix)) proto
        :else (assoc proto :toStringInfix (first infix))))))

(declare Add Subtract Multiply Divide Negate)
(def Add
  (AbstOp + "+"
          (fn [this var] (apply Add (map #(diff % var) (this :args))))))
(def Subtract
  (AbstOp - "-"
          (fn [this var] (apply Subtract (map #(diff % var) (this :args))))))
(def Multiply
  (AbstOp * "*"
          (fn [this var]
            (let [mul (apply Multiply (this :args))]
              (apply Add (map #(Multiply (diff % var) (Divide mul %)) (this :args)))))))
(def Divide
  (AbstOp checked-divide "/"
          (fn [this var]
            (let [mul (apply Multiply (rest (this :args)))
                  fst (first (this :args))]
              (cond
                (empty? (rest (this :args))) (Divide (Negate (diff fst var)) (Multiply fst fst))
                :else (Divide
                        (Subtract (Multiply (diff fst var) mul) (Multiply fst (diff mul var)))
                        (Multiply mul mul)))))))
(def Negate
  (AbstOp unchecked-negate "negate"
          (fn [this var] (Negate (diff (first (this :args)) var)))
          (fn [this] (str "negate" "(" (toStringInfix (first (this :args))) ")"))))

(def Mean
  (AbstOp mean-impl "mean"
          (fn [this var] (Divide (diff (apply Add (this :args)) var) (Constant (count (this :args)))))))

(def Varn
  (AbstOp varn-impl "varn"
          (fn [this var]
            (diff (Subtract
                    (apply Mean (map #(Multiply % %) (this :args)))
                    (let [m (apply Mean (this :args))] (Multiply m m))) var))))

(def IPow
  (AbstOp (fn [a b] (Math/pow a b)) "**" nil))
(def ILog
  (AbstOp (fn [a b] (/ (Math/log (Math/abs b)) (Math/log (Math/abs a)))) "//" nil))

(def operations
  (hash-map
    'negate [negate Negate]
    '+ [add Add]
    '- [subtract Subtract]
    '* [multiply Multiply]
    '/ [divide Divide]
    'mean [mean Mean]
    'varn [varn Varn]
    '** [nil IPow]
    (symbol "//") [nil ILog]))

(defn makeParseImpl [cnst vrbl fs]
  (fn parseImpl [op]
    (cond
      (number? op) (cnst op)
      (list? op) (apply (fs (operations (first op))) (map parseImpl (rest op)))
      :else (vrbl (str op)))))

(def parseFunctionImpl (makeParseImpl constant variable first))
(defn parseFunction [str] (parseFunctionImpl (read-string str)))

(def parseObjectImpl (makeParseImpl Constant Variable second))
(defn parseObject [str] (parseObjectImpl (read-string str)))

(load-file "parser.clj")
(defn *expect [word] (apply +seq (map #(+char (str %)) (seq word))))

(def *all-chars (mapv char (range 0 128)))
(def *space (+char (apply str (filter #(Character/isWhitespace %) *all-chars))))
(def *ws (+ignore (+star *space)))
(def *digit (+char (apply str (filter #(Character/isDigit %) *all-chars))))

(defn *sign [s tail]
  (if (#{\- \+} s)
    (cons s tail)
    tail))

(def *number
  (+map read-string
        (+str
          (+seqf concat
                 (+seqf cons (+opt (+char "+-")) (+plus *digit))
                 (+opt (+seqf cons (+char ".") (+plus *digit)))))))
(def *letter (+char (apply str (filter #(Character/isLetter %) *all-chars))))
(def *vname (+str (+plus *letter)))

(def *const (+map Constant *number))
(def *variable (+map Variable *vname))
(defn *negate [p]
  (+map Negate
        (+seqn 1 *ws
               (*expect "negate") *ws p)))

(declare *sum)
(def *value (delay (+or (*negate *value) *const *variable (+seqn 1 (+char "(") *sum (+char ")")))))

(defn *opLevel [prevl order & ops]
  (let [*op (apply +or (map *expect ops))]
    (letfn [(getOp [pair] (second (operations (symbol (apply str (first pair))))))]
      (+map
        (cond
          order (fn [sq] (reduce #((getOp %2) %1 (second %2)) (first sq) (first (rest sq))))
          :else (partial apply
                         (fn rec [prev sq]
                           (cond
                             (empty? sq) prev
                             :else ((getOp (first sq)) prev (rec (second (first sq)) (rest sq)))))))
        (+seq *ws prevl *ws (+star (+seq *ws *op *ws prevl *ws)))))))

(def *powlog (*opLevel *value false "**" "//"))
(def *term (*opLevel *powlog true "*" "/"))
(def *sum (*opLevel *term true "+" "-"))

(def parseObjectInfix (+parser *sum))
