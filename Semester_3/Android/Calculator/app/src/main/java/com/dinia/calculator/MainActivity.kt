package com.dinia.calculator
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.text.NumberFormat
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var exprLabel: TextView
    private lateinit var entryLabel: TextView

    companion object {
        const val FIRST_OP_KEY = "FIRST_OP_KEY"
        const val SECOND_OP_KEY = "SECOND_OP_KEY"
        const val EXPRESSION_FIELD_KEY = "EXPRESSION_FIELD_KEY"
        const val ENTRY_FIELD_KEY = "ENTRY_FIELD_KEY"
        const val OPERATION_FIELD_KEY = "OPERATION_FIELD_KEY"
        const val STATE_FIELD_KEY = "STATE_FIELD_KEY"
        const val DECIMAL_POINT_FLAG = "DECIMAL_POINT_FLAG"

        val fmt: NumberFormat = NumberFormat.getInstance(Locale("en"))
        init {
            fmt.isGroupingUsed = false
            fmt.maximumFractionDigits = 10
        }

        val operations: Map<String, (Double, Double) -> Double> = mapOf(
            "+" to ::add,
            "-" to ::subtract,
            "*" to ::multiply,
            "/" to ::divide
        )

        private fun add(a : Double, b : Double) : Double {
            return a + b
        }

        private fun subtract(a : Double, b : Double) : Double {
            return a - b
        }

        private fun multiply(a : Double, b : Double) : Double {
            return a * b
        }

        private fun divide(a : Double, b : Double) : Double {
            return a / b
        }

        private enum class State {
            NUMBER,
            OPERATION,
            RESULT
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        exprLabel = findViewById(R.id.expression)
        entryLabel = findViewById(R.id.currentNumber)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putDouble(FIRST_OP_KEY, firstOperand)
        outState.putDouble(SECOND_OP_KEY, secondOperand)
        outState.putString(EXPRESSION_FIELD_KEY, expression.toString())
        outState.putString(ENTRY_FIELD_KEY, entry.toString())
        outState.putBoolean(DECIMAL_POINT_FLAG, decimalPoint)
        outState.putString(OPERATION_FIELD_KEY, operation)
        outState.putString(STATE_FIELD_KEY, state.name)
        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        firstOperand = savedInstanceState.getDouble(FIRST_OP_KEY)
        secondOperand = savedInstanceState.getDouble(SECOND_OP_KEY)
        expression.append(savedInstanceState.getString(EXPRESSION_FIELD_KEY))
        entry.append(savedInstanceState.getString(ENTRY_FIELD_KEY))
        decimalPoint = savedInstanceState.getBoolean(DECIMAL_POINT_FLAG)
        operation = savedInstanceState.getString(OPERATION_FIELD_KEY).toString()
        state = State.valueOf(savedInstanceState.getString(STATE_FIELD_KEY).toString())
        updateLabel()
    }

    private var expression = StringBuilder()
    private var entry = StringBuilder()
    private var firstOperand = 0.0
    private var secondOperand = 0.0
    private var decimalPoint = false
    private var operation: String = "+"
    private var state = State.NUMBER

    private fun updateLabel() {
        exprLabel.text = expression
        entryLabel.text = entry
    }

    fun clickButton(view: View) {
        when (view.id) {
            R.id.one -> addDigit('1')
            R.id.two -> addDigit('2')
            R.id.three -> addDigit('3')
            R.id.four -> addDigit('4')
            R.id.five -> addDigit('5')
            R.id.six -> addDigit('6')
            R.id.seven -> addDigit('7')
            R.id.eight -> addDigit('8')
            R.id.nine -> addDigit('9')
            R.id.zero -> addDigit('0')
            R.id.point -> addPoint()

            R.id.add -> setOperation("+")
            R.id.subtract -> setOperation("-")
            R.id.multiply -> setOperation("*")
            R.id.divide -> setOperation("/")

            R.id.clear_digit -> clearDigit()
            R.id.clear -> startClear()
            R.id.exterminatus -> startClear()

            R.id.count -> countResult()
            R.id.copy -> makeCopy()
        }
        updateLabel()
    }

    private fun addDigit(digit: Char) {
        if (state == State.RESULT) {
            state = State.NUMBER
            clearAll()
        } else if (state == State.OPERATION) {
            state = State.NUMBER
            expression.append(' ').append(entry)
            entry.clear()
        }
        if (state == State.NUMBER) {
            entry.append(digit)
        }
    }

    private fun addPoint() {
        if (state == State.NUMBER && !decimalPoint) {
            decimalPoint = true
            entry.append('.')
        }
    }

    private fun setOperation(symbol: String) {
        if (state == State.NUMBER) {
            recalculate()
            expression.append(' ').append(entry)
        } else if (state == State.RESULT) {
            expression.clear()
            expression.append(entry)
        }
        entry.clear().append(symbol)
        state = State.OPERATION
        operation = symbol
        decimalPoint = false
    }

    private fun clearDigit() {
        if (state == State.RESULT) {
            startClear()
        } else if (entry.isNotEmpty()) {
            entry.deleteCharAt(entry.length - 1)
        }
    }

    private fun clearAll() {
        expression.clear()
        entry.clear()
    }

    private fun startClear() {
        firstOperand = 0.0
        secondOperand = 0.0
        decimalPoint = false
        operation = "+"
        state = State.NUMBER
        clearAll()
    }

    private fun countResult() {
        if (state == State.NUMBER) {
            recalculate()
        }
        if (state != State.OPERATION) {
            state = State.RESULT
            clearAll()
            entry.append(fmt.format(firstOperand))
        }
    }

    private fun makeCopy() {
        val clipboard: ClipboardManager =
            getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Copied text", entry)
        clipboard.setPrimaryClip(clip)
    }

    private fun recalculate() {
        if (expression.isNotEmpty()) {
            secondOperand = entry.toString().toDouble()
            firstOperand = operations[operation]?.invoke(firstOperand, secondOperand) ?: 0.0
        } else {
            if (entry.isNotEmpty()) {
                firstOperand = entry.toString().toDouble()
            }
        }
    }
}