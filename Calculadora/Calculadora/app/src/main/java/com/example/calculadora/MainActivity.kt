package com.example.calculadora

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.StateListDrawable
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.InputType
import android.text.TextWatcher
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.graphics.toColorInt
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    // ── Colores ──────────────────────────────────────────────────────────────
    private val colorFondo        = "#F3EAFF".toColorInt()
    private val colorMoradoOscuro = "#5B2D8E".toColorInt()
    private val colorMoradoMedio  = "#9C5FD6".toColorInt()
    private val colorMoradoClaro  = "#C9A6F0".toColorInt()
    private val colorBoton        = "#8A4CC7".toColorInt()
    private val colorBotonPress   = "#6B2FA8".toColorInt()
    private val colorResultFondo  = "#EDE0FF".toColorInt()
    private val colorError        = "#D32F2F".toColorInt()

    // ── Vistas ───────────────────────────────────────────────────────────────
    private lateinit var etNum1: EditText
    private lateinit var etNum2: EditText
    private lateinit var etResultado: EditText
    private lateinit var tvError: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ── Layout raíz ──────────────────────────────────────────────────────
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity     = Gravity.TOP or Gravity.CENTER_HORIZONTAL
            setBackgroundColor(colorFondo)
            setPadding(dp(32), dp(60), dp(32), dp(32))
        }

        // ── Título ───────────────────────────────────────────────────────────
        val tvTitulo = TextView(this).apply {
            text          = getString(R.string.title_calculadora)
            textSize      = 28f
            setTextColor(colorMoradoOscuro)
            gravity       = Gravity.CENTER
            typeface      = android.graphics.Typeface.DEFAULT_BOLD
            letterSpacing = 0.15f
        }
        root.addView(tvTitulo, fullWidthLP(marginBottom = dp(8)))

        // ── Divisor ──────────────────────────────────────────────────────────
        val divider = View(this).apply { setBackgroundColor(colorMoradoMedio) }
        root.addView(divider, LinearLayout.LayoutParams(-1, dp(3)).also {
            it.setMargins(dp(16), 0, dp(16), dp(40))
        })

        // ── Filas de entrada ─────────────────────────────────────────────────
        etNum1     = buildInput(readOnly = false)
        etNum2     = buildInput(readOnly = false)
        etResultado = buildInput(readOnly = true, resultado = true)

        root.addView(buildRow(getString(R.string.label_num1),      etNum1),      fullWidthLP(marginBottom = dp(20)))
        root.addView(buildRow(getString(R.string.label_num2),      etNum2),      fullWidthLP(marginBottom = dp(20)))
        root.addView(buildRow(getString(R.string.label_resultado), etResultado), fullWidthLP(marginBottom = dp(28)))

        // ── Mensaje de error ─────────────────────────────────────────────────
        tvError = TextView(this).apply {
            textSize   = 13f
            setTextColor(colorError)
            gravity    = Gravity.CENTER
            typeface   = android.graphics.Typeface.DEFAULT_BOLD
            visibility = View.GONE
        }
        root.addView(tvError, fullWidthLP(marginBottom = dp(20)))

        // ── Botón SUMAR ──────────────────────────────────────────────────────
        val btnSumar = Button(this).apply {
            text          = getString(R.string.btn_sumar)
            textSize      = 18f
            setTextColor(Color.WHITE)
            letterSpacing = 0.1f
            isAllCaps     = true
            background    = buildButtonDrawable()
            setOnClickListener { sumar() }
        }
        root.addView(btnSumar, LinearLayout.LayoutParams(-1, dp(56)).also {
            it.setMargins(dp(16), 0, dp(16), 0)
        })

        setContentView(root)

        // ── Filtro: solo números y punto (bloquea letras al instante) ─────────
        val soloNumeros = InputFilter { source, _, _, _, _, _ ->
            val filtrado = source.filter { it.isDigit() || it == '-' || it == '.' }
            if (filtrado.length < source.length) {
                showError(getString(R.string.error_only_numbers))
            }
            if (filtrado.toString() == source.toString()) null else filtrado
        }
        etNum1.filters = arrayOf(soloNumeros)
        etNum2.filters = arrayOf(soloNumeros)

        // Limpiar error cuando el usuario empieza a escribir correctamente
        val limpiarError = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, a: Int, b: Int, c: Int) {}
            override fun onTextChanged(s: CharSequence?, a: Int, b: Int, c: Int) {
                if (etNum1.text.isNotEmpty() && etNum2.text.isNotEmpty()) hideError()
            }
            override fun afterTextChanged(s: Editable?) {}
        }
        etNum1.addTextChangedListener(limpiarError)
        etNum2.addTextChangedListener(limpiarError)
    }

    // ── Lógica principal ─────────────────────────────────────────────────────
    private fun sumar() {
        val s1 = etNum1.text.toString().trim()
        val s2 = etNum2.text.toString().trim()

        when {
            s1.isEmpty() && s2.isEmpty() -> {
                showError(getString(R.string.error_both_empty))
                return
            }
            s1.isEmpty() -> {
                showError(getString(R.string.error_num1_empty))
                etNum1.requestFocus()
                return
            }
            s2.isEmpty() -> {
                showError(getString(R.string.error_num2_empty))
                etNum2.requestFocus()
                return
            }
        }

        val n1 = s1.toDoubleOrNull() ?: run { showError(getString(R.string.error_invalid_num1)); return }
        val n2 = s2.toDoubleOrNull() ?: run { showError(getString(R.string.error_invalid_num2)); return }

        val resultado = (n1 + n2).let { 
            if (it % 1.0 == 0.0) it.toLong() else kotlin.math.round(it).toLong()
        }
        etResultado.setText(resultado.toString())
        hideError()
    }

    // ── Helpers de UI ────────────────────────────────────────────────────────
    private fun showError(msg: String) {
        tvError.text       = msg
        tvError.visibility = View.VISIBLE
    }

    private fun hideError() {
        tvError.visibility = View.GONE
    }

    private fun buildRow(label: String, input: EditText): LinearLayout {
        return LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity     = Gravity.CENTER_VERTICAL
            val lbl = TextView(this@MainActivity).apply {
                text     = label
                textSize = 15f
                setTextColor(colorMoradoOscuro)
                typeface = android.graphics.Typeface.DEFAULT_BOLD
                minWidth = dp(95)
            }
            addView(lbl)
            addView(input, LinearLayout.LayoutParams(0, dp(52), 1f))
        }
    }

    private fun buildInput(readOnly: Boolean, resultado: Boolean = false): EditText {
        return EditText(this).apply {
            hint = getString(R.string.hint_zero)
            setHintTextColor(colorMoradoClaro)
            setTextColor(colorMoradoOscuro)
            textSize = 16f
            if (readOnly) {
                hint                  = getString(R.string.hint_dash)
                inputType             = InputType.TYPE_NULL
                isFocusable           = false
                isFocusableInTouchMode = false
                if (resultado) typeface = android.graphics.Typeface.DEFAULT_BOLD
            } else {
                inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_SIGNED or InputType.TYPE_NUMBER_FLAG_DECIMAL
            }
            setPadding(dp(14), 0, dp(14), 0)
            background = GradientDrawable().apply {
                shape        = GradientDrawable.RECTANGLE
                cornerRadius = dp(10).toFloat()
                setColor(if (resultado) colorResultFondo else Color.WHITE)
                setStroke(dp(2), if (resultado) colorMoradoMedio else colorMoradoClaro)
            }
        }
    }

    private fun buildButtonDrawable(): StateListDrawable {
        fun shape(color: Int) = GradientDrawable().apply {
            shape        = GradientDrawable.RECTANGLE
            cornerRadius = dp(14).toFloat()
            setColor(color)
        }
        return StateListDrawable().apply {
            addState(intArrayOf(android.R.attr.state_pressed), shape(colorBotonPress))
            addState(intArrayOf(), shape(colorBoton))
        }
    }

    private fun fullWidthLP(marginBottom: Int = 0) =
        LinearLayout.LayoutParams(-1, -2).also { it.setMargins(0, 0, 0, marginBottom) }

    private fun dp(value: Int) = (value * resources.displayMetrics.density).toInt()
}