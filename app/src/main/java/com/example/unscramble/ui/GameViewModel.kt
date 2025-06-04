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

// ViewModel que maneja el estado del juego
class GameViewModel : ViewModel() {

    // Flujo mutable del estado de la UI (interno)
    private val _uiState = MutableStateFlow(GameUiState())

    // Flujo inmutable del estado de la UI que se expone a la vista
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    // Variable que guarda la palabra que el usuario escribe
    var userGuess by mutableStateOf("")
        private set

    // Conjunto de palabras ya usadas en el juego
    private var usedWords: MutableSet<String> = mutableSetOf()

    // Palabra actual que debe adivinarse (sin mezclar)
    private lateinit var currentWord: String

    // Bloque de inicialización: reinicia el juego al crear el ViewModel
    init {
        resetGame()
    }

    /*
     * Reinicia los datos del juego para comenzar de nuevo.
     */
    fun resetGame() {
        usedWords.clear() // Limpia las palabras usadas
        _uiState.value = GameUiState(
            currentScrambledWord = pickRandomWordAndShuffle() // Asigna una nueva palabra mezclada
        )
    }

    /*
     * Mezcla los caracteres de la palabra dada hasta que la palabra mezclada sea diferente.
     */
    private fun shuffleCurrentWord(word: String): String {
        val tempWord = word.toCharArray()
        tempWord.shuffle() // Mezcla los caracteres

        // Se asegura de que la palabra mezclada no sea igual a la original
        while (String(tempWord) == word) {
            tempWord.shuffle()
        }

        return String(tempWord) // Retorna la palabra mezclada
    }

    /*
     * Elige aleatoriamente una palabra del conjunto, que no haya sido usada antes,
     * y retorna su versión mezclada.
     */
    private fun pickRandomWordAndShuffle(): String {
        currentWord = allWords.random() // Escoge una palabra aleatoria

        // Si ya fue usada, se repite el proceso
        return if (usedWords.contains(currentWord)) {
            pickRandomWordAndShuffle()
        } else {
            usedWords.add(currentWord) // Añade la nueva palabra al conjunto de usadas
            shuffleCurrentWord(currentWord) // Retorna la palabra mezclada
        }
    }
}
