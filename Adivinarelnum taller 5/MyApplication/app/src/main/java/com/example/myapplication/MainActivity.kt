package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.ui.theme.MyApplicationTheme
import kotlinx.coroutines.delay

import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    GuessNumberGame(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun GuessNumberGame(modifier: Modifier = Modifier) {
    var targetNumber by remember { mutableStateOf((1..50).random()) }
    var userInput by remember { mutableStateOf("") }
    var attempts by remember { mutableIntStateOf(0) }
    val maxAttempts = 3
    var message by remember { mutableStateOf("¡Tienes 3 intentos para adivinar el número (1-50)!") }
    var timeMillis by remember { mutableLongStateOf(0L) }
    var isGameOver by remember { mutableStateOf(false) }
    var ranking by remember { mutableStateOf(listOf<Long>()) }

    // Cronómetro
    LaunchedEffect(isGameOver) {
        if (!isGameOver) {
            val startTime = System.currentTimeMillis()
            while (!isGameOver) {
                timeMillis = System.currentTimeMillis() - startTime
                delay(100)
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Text(text = "Adivina el Número", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))

        // Formato del tiempo: MM:SS.S
        val seconds = (timeMillis / 1000) % 60
        val minutes = (timeMillis / (1000 * 60)) % 60
        val tenths = (timeMillis / 100) % 10
        Text(
            text = String.format(Locale.getDefault(), "Tiempo: %02d:%02d.%d", minutes, seconds, tenths),
            fontSize = 20.sp,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "Intentos: $attempts / $maxAttempts", fontSize = 18.sp)
        Text(text = message, fontSize = 16.sp, modifier = Modifier.padding(vertical = 8.dp))

        OutlinedTextField(
            value = userInput,
            onValueChange = { newValue ->
                if (newValue.isEmpty()) {
                    userInput = ""
                } else if (newValue.all { it.isDigit() }) {
                    val num = newValue.toIntOrNull()
                    if (num != null && num <= 50) {
                        userInput = newValue
                    }
                }
            },
            label = { Text("Introduce tu número (1-50)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            enabled = !isGameOver,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                val guess = userInput.toIntOrNull()
                if (guess == null || guess < 1) {
                    message = "Por favor, ingresa un número válido (1-50)."
                    return@Button
                }

                attempts++
                
                // Lógica de validación
                if (guess == targetNumber) {
                    message = "¡Felicidades! Lo lograste en $attempts intentos."
                    isGameOver = true
                    ranking = (ranking + timeMillis).sorted()
                } else if (attempts >= maxAttempts) {
                    message = "Perdiste. El número era $targetNumber."
                    isGameOver = true
                } else {
                    if (guess < targetNumber) {
                        message = "El número secreto es MAYOR que $guess."
                    } else {
                        message = "El número secreto es MENOR que $guess."
                    }
                }
                userInput = ""
            },
            enabled = !isGameOver && userInput.isNotEmpty(),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Validar")
        }

        if (isGameOver) {
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = {
                targetNumber = (1..50).random()
                attempts = 0
                userInput = ""
                message = "¡Nuevo juego! Adivina el número (1-50)"
                timeMillis = 0
                isGameOver = false
            }) {
                Text("Reiniciar Juego")
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
        Text(text = "Ranking (Mejores Tiempos)", fontWeight = FontWeight.Bold)
        LazyColumn(modifier = Modifier.fillMaxWidth()) {
            items(ranking.take(5)) { time ->
                val s = (time / 1000) % 60
                val m = (time / (1000 * 60)) % 60
                val t = (time / 100) % 10
                Text(
                    text = String.format(Locale.getDefault(), "%02d:%02d.%d", m, s, t),
                    modifier = Modifier.padding(4.dp)
                )
            }
        }
    }
}
