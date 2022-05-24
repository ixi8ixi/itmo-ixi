"use strict"
const operations = new Map();
function UnitedExpression(...operands) { this.operands = operands }
function setExpression(name, sign, arity, makeFunc, makeDiff) {
    operations.set(sign, name);
    name.arity = () => arity;
    name.prototype = Object.create(UnitedExpression.prototype);
    name.prototype.sign = () => sign;
    name.prototype.makeOperation = makeFunc;
    name.prototype.createDiff = makeDiff;
    name.prototype.constructor = name;
    name.prototype.evaluate = function (x, y, z) {
        return this.makeOperation(...this.operands.map(a => a.evaluate(x, y, z)));
    }
    name.prototype.diff = function (name) {
        return this.createDiff(...this.operands.map(a => a.diff(name)), ...this.operands);
    };
}

function setNumber(name, evalFunc, diffFunc) {
    name.prototype.evaluate = evalFunc;
    name.prototype.diff = diffFunc;
    name.prototype.toString = function () { return this.operands[0].toString() };
    name.prototype.prefix = name.prototype.toString;
    name.prototype.postfix = name.prototype.toString;
}

UnitedExpression.prototype.toString = function () {
    return this.operands.reduce((a, b) => a + b + " ", "") + this.sign()
}
UnitedExpression.prototype.prefix = function () {
    return this.operands.reduce((a, b) => a + ' ' + b.prefix(), '(' + this.sign()) + ')'
}
UnitedExpression.prototype.postfix = function () {
    return this.operands.reduce((a, b) => a + b.postfix() + ' ', '(') + this.sign() + ')'
}

function Const(value) { UnitedExpression.call(this, value) }
setExpression(Const);
setNumber(Const, function (x, y, z) { return this.operands[0] },
    function (x, y, z) { return ZERO })

let E = new Const(Math.E);
let PI = new Const(Math.PI);
let ZERO = new Const(0);
let ONE = new Const(1);
let TWO = new Const(2);

function Variable(value) { UnitedExpression.call(this, value) }
setExpression(Variable)
setNumber(Variable, function(x, y, z) { return this.operands[0] === "x" ? x : this.operands[0] === "y" ? y : z },
    function (name) { return  new Const(name === this.operands[0] ? 1 : 0) })

function Negate(a) { UnitedExpression.call(this, a) }
setExpression(Negate, 'negate', 1, a => -a,
    function (da) { return new Negate(da) })

function Add(a, b) { UnitedExpression.call(this, a, b) }
setExpression(Add, '+', 2, (a, b) => a + b,
    function (da, db) { return new Add(da, db) })

function Subtract(a, b) { UnitedExpression.call(this, a, b) }
setExpression(Subtract, '-', 2, (a, b) => a - b, function (da, db) { return new Subtract(da, db) })

function Multiply(a, b) { UnitedExpression.call(this, a, b) }
setExpression(Multiply, '*', 2, (a, b) => a * b,
    function (da, db, a, b) { return new Add(new Multiply(da, b), new Multiply(a, db)) })

function Divide(a, b) { UnitedExpression.call(this, a, b) }
setExpression(Divide, '/', 2, (a, b) => a / b,
    function (da, db, a, b)
    { return new Divide(new Subtract(new Multiply(da, b), new Multiply(a, db)), new Multiply(b, b))})

function Pow(a, b) { UnitedExpression.call(this, a, b) }
setExpression(Pow, 'pow', 2, (a, b) => Math.pow(a, b),
    function (da, db, a, b) { return new Multiply(
        new Pow(a , new Subtract(b, ONE)),
        new Add(
            new Multiply(b, da),
            new Multiply(new Multiply(a, new Log(E, a)), db)
        )
    ) })

function Log(a, b) { UnitedExpression.call(this, a, b) }
setExpression(Log, 'log', 2, (a, b) => Math.log(Math.abs(b)) / Math.log(Math.abs(a)),
    function (da, db, a, b) { return new Divide(
        new Subtract(
            new Multiply(a, new Multiply(new Log(E, a), db)),
            new Multiply(b, new Multiply(da, new Log(E, b)))
        ),
        new Multiply(a, new Multiply(b, new Pow(new Log(E, a), TWO)))
    ) })

function countMean(...args) { return args.reduce((a, b) => a + b) / args.length }
function diffMean(...args) { return new Mean(...args.slice(0, args.length / 2)) }
function Mean(...args) { UnitedExpression.call(this, ...args) }
setExpression(Mean, "mean", 0, countMean, diffMean)

function countVar(...args) { return countMean(...args.map(a => a * a)) - Math.pow(countMean(...args), 2) }
function diffVar (...args) {
    let first = [];
    for (let i = 0; i < args.length / 2; i++) {
        first.push(new Multiply(new Const(2), new Multiply(args[i], args[i + args.length / 2])));
    }
    first = new Mean(...first);
    let second = new Multiply(new Const(2),
        new Multiply(new Mean(...args.slice(args.length / 2, args.length)), diffMean(...args)))
    return new Subtract(first, second);
}
function Var(...args) { UnitedExpression.call(this, ...args) }
setExpression(Var, 'var', 0, countVar, diffVar)

function parse(expression) {
    const stack = [];
    const rawExpression = expression.split(' ').filter(a => a !== '');
    for (const element of rawExpression) {
        stack.push(!isNaN(element) ? new Const(parseFloat(element)) :
            ['x', 'y', 'z'].indexOf(element) !== -1 ? new Variable(element) :
                new (operations.get(element))(...stack.splice(-operations.get(element).arity())));
    }
    return stack[0];
}

function setError(name, pName) {
    name.prototype = Object.create(Error.prototype);
    name.prototype.constructor = name;
    name.prototype.name = pName;
}

function ParsingError(pos, expected, actual) {
    const msg = `On position: ${pos} :: Unexpected token: '${actual}' : '${expected}' expected`;
    Error.call(this, msg);
    this.message = msg;
}
setError(ParsingError, "ParsingError");

function IllegalArgumentError(message) {
    Error.call(this, message);
    this.message = message;
}
setError(IllegalArgumentError, "IllegalArgumentError")

function toRawExp(expression) {
    return expression.split(/([()])|\s+/g).filter(a => a !== '' && a !== undefined);
}

function abstractParse(expression, leftDelimiter, rightDelimiter, order) {
    const rawExpression = toRawExp(expression);
    let delta, pos, end, eof, addArg;
    if (order) {
        delta = 1;
        pos = 0;
        end = rawExpression.length - 1;
        eof = function () { return pos >= rawExpression.length }
        addArg = function (args, element) { args.push(element) }
    } else {
        delta = -1;
        pos = rawExpression.length - 1;
        end = 0;
        eof = function () { return pos <= -1}
        addArg = function (args, element) { args.unshift(element) }
    }
    function parseExpr() {
        if (!isNaN(rawExpression[pos])) {
            return new Const(parseFloat(rawExpression[pos]))
        } else if (['x', 'y', 'z'].indexOf(rawExpression[pos]) !== -1) {
            return new Variable(rawExpression[pos])
        }
        if (rawExpression[pos] !== leftDelimiter) {
            throw new ParsingError(pos, leftDelimiter, rawExpression[pos])
        }
        pos += delta;
        if (!operations.has(rawExpression[pos])) {
            throw new ParsingError(pos, 'operation', rawExpression[pos])
        }
        const currentOperation = operations.get(rawExpression[pos]);
        pos += delta;
        const args = [];
        while (!eof() && rawExpression[pos] !== rightDelimiter) {
            addArg(args, parseExpr()); pos += delta
        }
        if (eof()) {
            throw new ParsingError(pos, rightDelimiter, 'end of file')
        }
        if (currentOperation.arity() > 0 && currentOperation.arity() !== args.length) {
            throw new IllegalArgumentError(`Expected ${currentOperation.arity()} arguments, actual: ${args.length}`);
        }
        return new (currentOperation)(...args);
    }
    const result = parseExpr(rawExpression);
    if (pos !== end) {
        throw new ParsingError(pos, 'end of file', rawExpression[pos])
    }
    return result;
}

function parsePrefix(expression) {
    return abstractParse(expression, '(', ')', true)
}

function parsePostfix(expression) {
    return abstractParse(expression, ')', '(', false)
}
