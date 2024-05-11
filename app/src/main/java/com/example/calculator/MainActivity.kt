package com.example.calculator

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.calculator.databinding.ActivityMainBinding
import com.example.calculator.ui.theme.CalculatorTheme
import kotlinx.coroutines.processNextEventInCurrentThread
import org.w3c.dom.Text

class MainActivity : ComponentActivity() {
    // Flags to control whether operators, decimal, and trig functions can be added to the workingsTextView
    private var canAddOperation = false
    private var canAddDecimal = true
    private var canAddTrig = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)


    }
    fun trigAction(view: View) { // Function to handle trigonometric functions button click
        val workingsTv: TextView = findViewById(R.id.workingsTV)

        if (view is Button) {
            // Check if the trig function can be added and append it to the workingsTextView
            if (view.text == "TAN"|| view.text == "SIN" || view.text == "COS")
                if (canAddTrig)
                    workingsTv.append(view.text)

        }

    }

    fun numberAction(view: View) { // Function to handle number button click
        val workingsTv: TextView = findViewById(R.id.workingsTV)

        if (view is Button) {
            if (view.text == ".") {
                // Check if the decimal can be added and append it to the workingsTextView
                if (canAddDecimal) {
                    workingsTv.append(view.text)
                }
                canAddDecimal = false
            } else
                // Append the number to the workingsTextView
                workingsTv.append(view.text)
            // Set flag to true to allow adding operators
            canAddOperation = true
        }
    }

    fun operationAction(view: View) { // Function to handle operator button click
        val workingsTv: TextView = findViewById(R.id.workingsTV)
        if (view is Button && canAddOperation) {
            // Append the operator to the workingsTextView
            workingsTv.append(view.text)
            // Reset Flags
            canAddOperation = false
            canAddDecimal = true
        }
    }

    fun equalsAction(view: View) { // Function to handle equals button click
        val resultsTV: TextView = findViewById(R.id.resultsTV)
        // Calculate and display the result
        resultsTV.text = calculateResults()

    }

    fun backSpaceAction(view: View) { // Function to handle backspace button click
        val workingsTv: TextView = findViewById(R.id.workingsTV)
        val length = workingsTv.length()

        if (length > 0) { // Remove the last character from the workingsTextView
            workingsTv.text = workingsTv.text.subSequence(0, length - 1)
        }
    }

    fun allClearAction(view: View) { // Function to handle all clear button click
        val workingsTv: TextView = findViewById(R.id.workingsTV)
        workingsTv.text = "" // Clear the workingsTextView
        val resultsTV: TextView = findViewById(R.id.resultsTV)
        resultsTV.text = "" // Clear the resultsTextView
    }

    private fun calculateResults(): String { // function to calculate the results based on the contents of the workingsTextView
        val digitsOperators = digitsOperators()
        if (digitsOperators.isEmpty()) {
            return ""
        }

        val timesDivision = timesDivisionCalculate(digitsOperators)
        if (timesDivision.isEmpty()) {
            return ""
        }

        val addSubtract = addSubtractCalculate(timesDivision)
        if (addSubtract.isEmpty()) {
            return ""
        }

        val result = tanSinCosCalculate(addSubtract)
        return result.toString()
    }

    private fun tanSinCosCalculate(passedList: MutableList<Any>): Any { // Function to calculate trigonometric functions
        var result = passedList[0] as Float

        for (i in passedList.indices) {
            if (passedList[i] is Char && i != passedList.lastIndex) {
                val trig = passedList[i - 1]
                val digit = passedList[i] as Double
                val radians = Math.toRadians(digit).toFloat()
                when (trig) {
                    "TAN" -> result = kotlin.math.tan(radians)
                    "SIN" -> result = kotlin.math.sin(radians)
                    "COS" -> result = kotlin.math.cos(radians)
                }
            }
        }
        return result
    }

    private fun addSubtractCalculate(passedList: MutableList<Any>): MutableList<Any> { // Function to calculate addition and subtraction
        val newList = mutableListOf<Any>()
        var restartIndex = passedList.size

        for (i in passedList.indices) {
            if (passedList[i] is Char && i != passedList.lastIndex && i < restartIndex) {
                val operator = passedList[i]
                val prevDigit = passedList[i - 1] as Float
                val nextDigit = passedList[i + 1] as Float
                when (operator) {
                    '+' -> {
                        newList.add(prevDigit + nextDigit)
                        restartIndex = i + 1
                    }

                    '-' -> {
                        newList.add(prevDigit - nextDigit)
                        restartIndex = i + 1
                    }

                    else -> {
                        newList.add(prevDigit)
                        newList.add(operator)
                    }

                }
            }
            if (i > restartIndex) {
                newList.add(passedList[i])
            }
        }
        return newList
    }

    private fun timesDivisionCalculate(passedList: MutableList<Any>): MutableList<Any> { // Function to calculate multiplication and division
        var list = passedList
        while (list.contains('*') || list.contains('/')) {
            list = calcTimesDiv(list)
        }
        return list
    }

    private fun calcTimesDiv(passedList: MutableList<Any>): MutableList<Any> { // Function to perform multiplication and division calculations
        val newList = mutableListOf<Any>()
        var restartIndex = passedList.size

        for (i in passedList.indices) {
            if (passedList[i] is Char && i != passedList.lastIndex && i < restartIndex) {
                val operator = passedList[i]
                val prevDigit = passedList[i - 1] as Float
                val nextDigit = passedList[i + 1] as Float
                when (operator) {
                    '*' -> {
                        newList.add(prevDigit * nextDigit)
                        restartIndex = i + 1
                    }

                    '/' -> {
                        newList.add(prevDigit / nextDigit)
                        restartIndex = i + 1
                    }

                    else -> {
                        newList.add(prevDigit)
                        newList.add(operator)
                    }

                }
            }
            if (i > restartIndex) {
                newList.add(passedList[i])
            }
        }
        return newList
    }

    private fun digitsOperators(): MutableList<Any> { //  Function to extract digits and operator from workingsTextView
        val list = mutableListOf<Any>()
        val workingsTv: TextView = findViewById(R.id.workingsTV)
        var currentDigit = ""
        for (character in workingsTv.text) {
            if (character.isDigit() || character == '.') {
                currentDigit += character
            } else {
                list.add(currentDigit.toFloat())
                currentDigit = ""
                list.add(character)
            }
        }

        if (currentDigit != "") {
            list.add(currentDigit.toFloat())
        }


        return list
    }
}
