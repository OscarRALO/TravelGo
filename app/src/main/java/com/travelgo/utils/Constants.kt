package com.travelgo.utils

object Constants {

    // Firebase Collections
    const val COLLECTION_DESTINOS = "destinos"
    const val COLLECTION_USUARIOS = "usuarios"
    const val COLLECTION_FAVORITOS = "favoritos"

    // SharedPreferences
    const val PREFS_NAME = "TravelGoPrefs"
    const val KEY_IS_FIRST_TIME = "isFirstTime"
    const val KEY_USER_ID = "userId"
    const val KEY_DARK_MODE = "darkMode"
    const val KEY_LANGUAGE = "language"

    // Intent Extras
    const val EXTRA_DESTINO_ID = "destinoId"
    const val EXTRA_DESTINO = "destino"
    const val EXTRA_FROM_FAVORITES = "fromFavorites"

    // Request Codes
    const val RC_SIGN_IN = 9001
    const val RC_LOCATION_PERMISSION = 1001
    const val RC_EDIT_PROFILE = 2001

    // Validation
    const val MIN_PASSWORD_LENGTH = 6
    const val MAX_NAME_LENGTH = 50

    // UI
    const val SPLASH_DELAY = 2000L
    const val ANIMATION_DURATION = 300L

    // Map
    const val DEFAULT_ZOOM = 15f
    const val MAP_CAMERA_ANIMATE_DURATION = 1000

    // Pagination
    const val ITEMS_PER_PAGE = 20

    // Error Messages
    const val ERROR_NETWORK = "Error de conexión. Verifica tu internet."
    const val ERROR_GENERIC = "Ocurrió un error. Intenta nuevamente."
    const val ERROR_AUTH = "Error de autenticación."
    const val ERROR_EMPTY_FIELDS = "Por favor completa todos los campos."
    const val ERROR_INVALID_EMAIL = "Correo electrónico inválido."
    const val ERROR_WEAK_PASSWORD = "La contraseña debe tener al menos 6 caracteres."

    // Success Messages
    const val SUCCESS_LOGIN = "¡Bienvenido!"
    const val SUCCESS_REGISTER = "Cuenta creada exitosamente."
    const val SUCCESS_ADD_FAVORITE = "Agregado a favoritos."
    const val SUCCESS_REMOVE_FAVORITE = "Eliminado de favoritos."
    const val SUCCESS_PROFILE_UPDATE = "Perfil actualizado."

    // Peru Cities (para tu proyecto)
    val CIUDADES_PERU = listOf(
        "Lima",
        "Cusco",
        "Arequipa",
        "Trujillo",
        "Piura",
        "Iquitos",
        "Chiclayo",
        "Huancayo",
        "Puno",
        "Tacna",
        "Ayacucho",
        "Cajamarca",
        "Huaraz"
    )
}

// Extensiones útiles
object Validators {
    fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun isValidPassword(password: String): Boolean {
        return password.length >= Constants.MIN_PASSWORD_LENGTH
    }

    fun isValidName(name: String): Boolean {
        return name.isNotBlank() && name.length <= Constants.MAX_NAME_LENGTH
    }
}