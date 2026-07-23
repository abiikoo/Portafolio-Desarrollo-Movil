package com.example.lazycolumns_paises

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


private val GreenBackground  = Color(0xFFE8F5E9)
private val GreenSurface     = Color(0xFFF1F8F2)
private val GreenPrimary     = Color(0xFF66BB6A)
private val GreenOnPrimary   = Color(0xFFFFFFFF)
private val GreenContainer   = Color(0xFFA5D6A7)
private val GreenTextPrimary = Color(0xFF2E7D32)
private val GreenTextSecond  = Color(0xFF4CAF50)
private val PinkHeart        = Color(0xFFE91E8C)

private val GreenColorScheme = lightColorScheme(
    primary          = GreenPrimary,
    onPrimary        = GreenOnPrimary,
    primaryContainer = GreenContainer,
    background       = GreenBackground,
    surface          = GreenSurface,
    onBackground     = GreenTextPrimary,
    onSurface        = GreenTextPrimary,
)

// Tema
@Composable
fun CountriesAppTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = GreenColorScheme,
        content     = content
    )
}

// Activity
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CountriesAppTheme {
                MainApp()
            }
        }
    }
}

// Navegación
@Composable
fun MainApp() {
    val showFavorites = remember { mutableStateOf(false) }
    val countries = remember {
        mutableStateOf(
            listOf(
                "Panamá", "México", "Argentina", "Chile",
                "Colombia", "España", "Italia", "Francia",
                "Japón", "Brasil", "Perú", "Canadá"
            )
        )
    }
    val favorites = remember { mutableStateListOf<String>() }

    if (showFavorites.value) {
        FavoritesScreen(favorites = favorites, onBack = { showFavorites.value = false })
    } else {
        CountryListScreen(
            countries         = countries.value,
            favorites         = favorites,
            onCountriesChange = { countries.value = it },
            onShowFavorites   = { showFavorites.value = true }
        )
    }
}

// Pantalla principal
@Composable
fun CountryListScreen(
    countries: List<String>,
    favorites: SnapshotStateList<String>,
    onCountriesChange: (List<String>) -> Unit,
    onShowFavorites: () -> Unit
) {

    var newCountry by remember { mutableStateOf("") }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        floatingActionButton = {
            FloatingActionButton(
                onClick = onShowFavorites,
                containerColor = PinkHeart,
                contentColor = Color.White
            ) {
                Icon(
                    imageVector = Icons.Filled.Favorite,
                    contentDescription = "Ver favoritos"
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {

            // Campo para agregar país
            OutlinedTextField(
                value         = newCountry,
                onValueChange = { newCountry = it },
                label         = { Text("Agregar país") },
                modifier      = Modifier.fillMaxWidth(),
                singleLine    = true,
                colors        = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor   = GreenPrimary,
                    unfocusedBorderColor = GreenContainer,
                    focusedLabelColor    = GreenTextPrimary,
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    if (newCountry.isNotBlank()) {
                        onCountriesChange(countries + newCountry.trim())
                        newCountry = ""
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors   = ButtonDefaults.buttonColors(
                    containerColor = GreenPrimary,
                    contentColor   = GreenOnPrimary
                )
            ) {
                Text("Agregar", fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(countries) { country ->
                    CountryCard(country = country, favorites = favorites)
                }
            }
        }
    }
}

// Pantalla de Favoritos
@Composable
fun FavoritesScreen(favorites: SnapshotStateList<String>, onBack: () -> Unit) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier          = Modifier
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
                    .fillMaxWidth()
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.List, contentDescription = "Volver", tint = GreenPrimary)
                }
                Text(
                    text       = "Mis Favoritos",
                    fontSize   = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color      = GreenTextPrimary,
                    modifier   = Modifier.padding(start = 8.dp)
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            if (favorites.isEmpty()) {
                Box(
                    modifier         = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text     = "No tienes países favoritos aún.",
                        color    = GreenTextSecond,
                        fontSize = 18.sp
                    )
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(favorites) { country ->
                        CountryCard(country = country, favorites = favorites)
                    }
                }
            }
        }
    }
}

// Card de cada país
@Composable
fun CountryCard(country: String, favorites: SnapshotStateList<String>) {


    val context    = LocalContext.current.applicationContext
    val isFavorite = favorites.contains(country)

    Card(
        modifier  = Modifier.fillMaxWidth(),
        colors    = CardDefaults.cardColors(containerColor = GreenSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier              = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            // Nombre del país
            Text(
                text       = country,
                fontSize   = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color      = GreenTextPrimary,
                modifier   = Modifier.weight(1f)
            )

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {

                // Botón Detalles y Toast
                Button(
                    onClick = {
                        Toast.makeText(context, "País: $country", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = GreenPrimary,
                        contentColor   = GreenOnPrimary
                    ),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text("Detalles", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }

                // Favoritos
                IconButton(
                    onClick = {
                        if (isFavorite) favorites.remove(country)
                        else favorites.add(country)
                    }
                ) {
                    Icon(
                        imageVector        = if (isFavorite) Icons.Filled.Favorite
                        else Icons.Outlined.FavoriteBorder,
                        contentDescription = if (isFavorite) "Quitar favorito"
                        else "Agregar a favoritos",
                        tint               = if (isFavorite) PinkHeart else GreenTextSecond,
                        modifier           = Modifier.size(28.dp)
                    )
                }
            }
        }
    }
}