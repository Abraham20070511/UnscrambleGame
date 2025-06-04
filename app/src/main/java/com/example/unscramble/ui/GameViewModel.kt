package com.example.unscramble.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.unscramble.data.MAX_NO_OF_WORDS
import com.example.unscramble.data.SCORE_INCREASE
import com.example.unscramble.data.allWords
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * ViewModel que contiene los datos y métodos para manejar la lógica del juego.
 */
class GameViewModel : ViewModel() {

    // Estado de la UI del juego, expuesto como StateFlow para ser observado
    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    // Almacena la palabra que el usuario ha escrito
    var userGuess by mutableStateOf("")
        private set

    // Conjunto de palabras que ya se han usado en la partida actual
    private var usedWords: MutableSet<String> = mutableSetOf()
    // La palabra actual que el jugador debe adivinar (sin mezclar)
    private lateinit var currentWord: String

    // Inicializa el juego al crear el ViewModel
    init {
        resetGame()
    }

    /*
     * Reinicia los datos del juego para comenzar una nueva partida.
     */
    fun resetGame() {
        usedWords.clear() // Limpia las palabras usadas
        _uiState.value = GameUiState(currentScrambledWord = pickRandomWordAndShuffle()) // Elige una palabra nueva mezclada
    }

    /*
     * Actualiza la suposición que el usuario escribe en el campo de texto.
     */
    fun updateUserGuess(guessedWord: String){
        userGuess = guessedWord
    }

    /*
     * Verifica si la suposición del usuario es correcta.
     * Si es correcta, incrementa el puntaje y avanza el juego.
     * Si es incorrecta, muestra un error en la UI.
     */
    fun checkUserGuess() {
        if (userGuess.equals(currentWord, ignoreCase = true)) {
            // Si la palabra es correcta
            val updatedScore = _uiState.value.score.plus(SCORE_INCREASE) // Aumenta puntaje
            updateGameState(updatedScore) // Actualiza estado para siguiente ronda
        } else {
            // Si la palabra es incorrecta, marca error en la UI
            _uiState.update { currentState ->
                currentState.copy(isGuessedWordWrong = true)
            }
        }
        // Limpia la suposición del usuario para la próxima entrada
        updateUserGuess("")
    }

    /*
     * Método para saltar la palabra actual sin aumentar el puntaje.
     */
    fun skipWord() {
        updateGameState(_uiState.value.score) // Actualiza estado sin cambiar puntaje
        updateUserGuess("") // Limpia el campo de texto
    }

    /*
     * Actualiza el estado del juego eligiendo una nueva palabra o
     * termina el juego si se alcanzó el límite de palabras.
     */
    private fun updateGameState(updatedScore: Int) {
        if (usedWords.size == MAX_NO_OF_WORDS){
            // Si ya se usaron todas las palabras, termina el juego
            _uiState.update { currentState ->
                currentState.copy(
                    isGuessedWordWrong = false,
                    score = updatedScore,
                    isGameOver = true // Marca el juego como terminado
                )
            }
        } else{
            // Si quedan palabras, sigue con la siguiente ronda
            _uiState.update { currentState ->
                currentState.copy(
                    isGuessedWordWrong = false,
                    currentScrambledWord = pickRandomWordAndShuffle(), // Nueva palabra mezclada
                    currentWordCount = currentState.currentWordCount.inc(), // Incrementa contador de palabras
                    score = updatedScore
                )
            }
        }
    }

    /*
     * Mezcla las letras de la palabra actual asegurándose de que
     * el resultado no sea igual a la palabra original.
     */
    private fun shuffleCurrentWord(word: String): String {
        val tempWord = word.toCharArray()
        tempWord.shuffle() // Mezcla las letras
        // Vuelve a mezclar si la palabra no cambió (para evitar repetición)
        while (String(tempWord) == word) {
            tempWord.shuffle()
        }
        return String(tempWord)
    }

    /*
     * Escoge una palabra aleatoria que no se haya usado antes,
     * la agrega a la lista de usadas y devuelve la palabra mezclada.
     */
    private fun pickRandomWordAndShuffle(): String {
        currentWord = allWords.random() // Escoge palabra aleatoria
        return if (usedWords.contains(currentWord)) {
            // Si ya se usó la palabra, intenta con otra recursivamente
            pickRandomWordAndShuffle()
        } else {
            usedWords.add(currentWord) // Añade palabra a usadas
            shuffleCurrentWord(currentWord) // Devuelve palabra mezclada
        }
    }
}
