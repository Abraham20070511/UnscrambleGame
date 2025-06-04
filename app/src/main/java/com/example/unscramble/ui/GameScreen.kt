/*
 * Copyright (C) 2023 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.unscramble.ui

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.shapes
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.unscramble.R
import com.example.unscramble.ui.theme.UnscrambleTheme

// Composable principal que representa la pantalla del juego
@Composable
fun GameScreen(gameViewModel: GameViewModel = viewModel()) {
    // Observa el estado de la UI expuesto por el ViewModel
    val gameUiState by gameViewModel.uiState.collectAsState()
    // Obtiene el padding mediano definido en recursos
    val mediumPadding = dimensionResource(R.dimen.padding_medium)

    Column(
        modifier = Modifier
            .statusBarsPadding() // Padding para no solaparse con la barra de estado
            .verticalScroll(rememberScrollState()) // Permite scroll vertical
            .safeDrawingPadding() // Padding para evitar recortes en bordes seguros
            .padding(mediumPadding), // Padding general
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // Título de la app
        Text(
            text = stringResource(R.string.app_name),
            style = typography.titleLarge,
        )

        // Layout que contiene la palabra mezclada, el campo de texto y la información del juego
        GameLayout(
            onUserGuessChanged = { gameViewModel.updateUserGuess(it) }, // Actualiza la palabra escrita por el usuario
            wordCount = gameUiState.currentWordCount, // Número de palabra actual
            userGuess = gameViewModel.userGuess, // Valor del texto escrito por el usuario
            onKeyboardDone = { gameViewModel.checkUserGuess() }, // Acción al terminar de escribir
            currentScrambledWord = gameUiState.currentScrambledWord, // Palabra mezclada actual
            isGuessWrong = gameUiState.isGuessedWordWrong, // Indica si la suposición es incorrecta
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(mediumPadding)
        )

        // Botones para enviar la respuesta o saltar palabra
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(mediumPadding),
            verticalArrangement = Arrangement.spacedBy(mediumPadding), // Espacio entre botones
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // Botón para enviar la palabra
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = { gameViewModel.checkUserGuess() }
            ) {
                Text(
                    text = stringResource(R.string.submit),
                    fontSize = 16.sp
                )
            }

            // Botón para saltar palabra
            OutlinedButton(
                onClick = { gameViewModel.skipWord() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = stringResource(R.string.skip),
                    fontSize = 16.sp
                )
            }
        }

        // Muestra la puntuación actual en un Card
        GameStatus(score = gameUiState.score, modifier = Modifier.padding(20.dp))

        // Si el juego terminó, muestra un diálogo con la puntuación final y opciones
        if (gameUiState.isGameOver) {
            FinalScoreDialog(
                score = gameUiState.score,
                onPlayAgain = { gameViewModel.resetGame() }
            )
        }
    }
}

// Composable que muestra la puntuación actual dentro de una tarjeta
@Composable
fun GameStatus(score: Int, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
    ) {
        Text(
            text = stringResource(R.string.score, score), // Texto con la puntuación
            style = typography.headlineMedium,
            modifier = Modifier.padding(8.dp)
        )

    }
}

// Composable que contiene la estructura principal del juego:
// palabra mezclada, número de palabra, campo de texto y etiquetas
@Composable
fun GameLayout(
    currentScrambledWord: String,
    wordCount: Int,
    isGuessWrong: Boolean,
    userGuess: String,
    onUserGuessChanged: (String) -> Unit,
    onKeyboardDone: () -> Unit,
    modifier: Modifier = Modifier
) {
    val mediumPadding = dimensionResource(R.dimen.padding_medium)

    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 5.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(mediumPadding), // Espaciado vertical entre elementos
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(mediumPadding)
        ) {
            // Muestra el conteo de palabras con fondo y bordes redondeados
            Text(
                modifier = Modifier
                    .clip(shapes.medium)
                    .background(colorScheme.surfaceTint)
                    .padding(horizontal = 10.dp, vertical = 4.dp)
                    .align(alignment = Alignment.End),
                text = stringResource(R.string.word_count, wordCount),
                style = typography.titleMedium,
                color = colorScheme.onPrimary
            )
            // Muestra la palabra mezclada
            Text(
                text = currentScrambledWord,
                style = typography.displayMedium
            )
            // Instrucciones para el usuario
            Text(
                text = stringResource(R.string.instructions),
                textAlign = TextAlign.Center,
                style = typography.titleMedium
            )
            // Campo de texto donde el usuario escribe su respuesta
            OutlinedTextField(
                value = userGuess, // Texto actual
                singleLine = true,
                shape = shapes.large,
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = colorScheme.surface,
                    unfocusedContainerColor = colorScheme.surface,
                    disabledContainerColor = colorScheme.surface,
                ),
                onValueChange = onUserGuessChanged, // Actualiza el texto conforme se escribe
                label = {
                    if (isGuessWrong) {
                        Text(stringResource(R.string.wrong_guess)) // Mensaje si la respuesta es incorrecta
                    } else {
                        Text(stringResource(R.string.enter_your_word)) // Etiqueta estándar
                    }
                },
                isError = isGuessWrong, // Resalta error si la suposición es incorrecta
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Done // Acción de teclado 'Done'
                ),
                keyboardActions = KeyboardActions(
                    onDone = { onKeyboardDone() } // Acción al presionar done en teclado
                )
            )
        }
    }
}

/*
 * Composable que crea y muestra un diálogo AlertDialog con la puntuación final.
 */
@Composable
private fun FinalScoreDialog(
    score: Int,
    onPlayAgain: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Obtiene la actividad actual para poder cerrarla si el usuario sale
    val activity = (LocalContext.current as Activity)

    AlertDialog(
        onDismissRequest = {
            // Se ejecuta cuando el usuario toca fuera del diálogo o el botón atrás
            // Aquí se deja vacío para permitir que el diálogo se cierre así
        },
        title = { Text(text = stringResource(R.string.congratulations)) }, // Título del diálogo
        text = { Text(text = stringResource(R.string.you_scored, score)) }, // Texto con la puntuación
        modifier = modifier,
        dismissButton = {
            // Botón para salir de la aplicación (termina la actividad)
            TextButton(
                onClick = {
                    activity.finish()
                }
            ) {
                Text(text = stringResource(R.string.exit))
            }
        },
        confirmButton = {
            // Botón para volver a jugar, llama a la función para reiniciar el juego
            TextButton(onClick = onPlayAgain) {
                Text(text = stringResource(R.string.play_again))
            }
        }
    )
}

// Vista previa para diseño en Android Studio
@Preview(showBackground = true)
@Composable
fun GameScreenPreview() {
    UnscrambleTheme {
        GameScreen()
    }
}
