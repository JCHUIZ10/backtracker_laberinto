package com.uns.backtracker.dominio.model

enum class Dificultad(
    val tasaAperturaCiclos: Float, // % de paredes extra que se abren para crear ciclos
    val factorExploracion: Float, // 0.0 = DFS puro, 1.0 = aleatoriedad máxima
    val descripción: String
) {
    FACIL(0.02f, 0.2f, "Caminos directos y pocos callejones."),
    SUFRIBLE(0.08f, 0.6f, "Más bifurcaciones y ciclos moderados."),
    INFERNAL(0.15f, 0.9f, "Entropía máxima: laberinto altamente complejo y cíclico.")
}
