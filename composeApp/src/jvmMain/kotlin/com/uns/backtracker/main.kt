package com.uns.backtracker

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Algoritmo de generación de Laberinto usando Backtracker",
    ) {
        App()
    }
}