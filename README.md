# MiAppNav — Reto Super Ingenieros

App Android con **Jetpack Compose Navigation** y 4 pantallas:
**Inicio / Perfil / Ajustes / Detalle**.

Paleta: rosado fresa + verde matcha. Material 3.

## Qué cumple del reto

- `NavHost` central → `rutas/GrafoNavegacion.kt`
- 4 pantallas conectadas → carpeta `pantallas/`
- `sealed class Screen` con las rutas exactas pedidas (`home`, `profile`, `settings`, `details`) → `rutas/Destinos.kt`
- Navegación con parámetros (texto viaja de Inicio a Detalle vía `details/{texto}`)
- Menú en la barra inferior (construido manualmente con `BottomAppBar` + `Row`) → `PrincipalActivity.kt`
- Barra superior `TopAppBar` con título de la app
- Animación slide horizontal entre pantallas → `GrafoNavegacion.kt`
- Cada pantalla está envuelta en `Card` y tiene: `TextField`, texto con el nombre de la ingeniera, `ElevatedButton` y un ícono diferente.

## Cómo correrlo

1. **Abrir** la carpeta `MiAppNav` en Android Studio / IntelliJ.
2. Esperar la sincronización de Gradle (1ra vez ~varios minutos).
3. **En el teléfono Android**: activar Opciones de desarrollador (7 toques en "Número de compilación") y Depuración USB.
4. Conectar el teléfono por USB, aceptar el popup de "Permitir depuración USB".
5. Seleccionar el dispositivo en el dropdown del IDE y darle a ▶ Run.

## Generar APK

`Build` → `Build Bundle(s) / APK(s)` → `Build APK(s)`
Lo encuentras en `app/build/outputs/apk/debug/app-debug.apk`.

## Estructura del proyecto

```
app/src/main/java/com/abigail/miapp/
├── PrincipalActivity.kt         ← Activity principal, Scaffold, TopAppBar, BottomAppBar
├── rutas/
│   ├── Destinos.kt              ← sealed class Screen + lista de destinos de la barra
│   └── GrafoNavegacion.kt       ← NavHost + animación slide
├── pantallas/
│   ├── PantallaInicio.kt        ← envía parámetro a Detalle
│   ├── PantallaPerfil.kt
│   ├── PantallaAjustes.kt       ← usa Slider para nivel de dulzura
│   └── PantallaDetalle.kt       ← recibe parámetro
└── tema/
    ├── Paleta.kt                ← Fresa, Matcha, Crema, Tinta
    └── TemaApp.kt
```

## Detalle del parámetro

Cuando Inicio envía el texto a Detalle, se reemplazan espacios por `-` (guion)
para que la URL `details/{texto}` no se rompa. Detalle hace lo contrario al
mostrar: reemplaza `-` por espacios.
