"use strict"
const cnst = a => (x, y, z) => a;
const pi = cnst(Math.PI);
const e = cnst(Math.E);
const variable = name => (x, y, z) => name === 'x' ? x : name === 'y' ? y : z;
const operation = f => (...args) => (x, y, z) => f(...args.map(a => a(x, y, z)));
const negate = operation(a => -a);
const abs = operation(a => Math.abs(a))
const add = operation((a, b) => a + b);
const subtract = operation((a, b) => a - b);
const multiply = operation((a, b) => a * b);
const divide = operation((a, b) => a / b);
const iff = operation((a, b, c) => a >= 0 ? b : c);

const operations = new Map().set('+', [add, 2]).set('-', [subtract, 2]).set('*', [multiply, 2]).set('/', [divide, 2]).set('negate', [negate, 1]).set('abs', [abs, 1]).set('iff', [iff, 3]);

const parse = expression => {
    const rawExp = expression.split(' ').filter(x => x !== '');
    const stack = [];
    for (const element of rawExp) {
        stack.push(['x', 'y', 'z'].indexOf(element) !== -1 ? variable(element) :
            !isNaN(parseFloat(element)) ? cnst(parseFloat(element)) : element === "pi" ? pi : element === "e" ? e :
            operations.get(element)[0](...stack.splice(-operations.get(element)[1])));
    }
    return stack[0];
}

const test = n => {
    for (let i = 0; i < n + 1; i++) {
        console.log(parse("x x * 2 x * - 1 +")(i, 0, 0));
    }
}
