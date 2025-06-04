package com.example.unscramble.ui

/**
 * Clase que representa el estado de la interfaz de usuario (UI) del juego.
 * Contiene toda la información necesaria para mostrar el estado actual del juego.
 */
data class GameUiState(
    val currentScrambledWord: String = "",  // La palabra actual mezclada que se muestra al usuario
    val currentWordCount: Int = 1,          // Número de la palabra actual en la partida (por ejemplo, 1 de 10)
    val score: Int = 0,                     // Puntaje acumulado del jugador hasta ahora
    val isGuessedWordWrong: Boolean = false, // Indica si la última suposición fue incorrecta (para mostrar error)
    val isGameOver: Boolean = false          // Indica si el juego ha terminado
)
