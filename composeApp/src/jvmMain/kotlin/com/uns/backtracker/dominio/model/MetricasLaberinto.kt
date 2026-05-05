package com.uns.backtracker.dominio.model

data class MetricasLaberinto(
    val longitudCaminoOptimo: Int,      // L*
    val distanciaManhattan: Int,        // L_manhattan
    val ratioOptimalidad: Float,        // ρ = L* / L_manhattan
    val factorRamificacion: Float,      // b — promedio de decisiones por nodo
    val profundidadMediaTrampas: Float, // τ — longitud media de callejones
    val ratioTrampasSimilares: Float,   // δ — proporción de trampas engañosas
    val totalCeldas: Int,
    val densidadParedes: Float          // paredes / totalCeldas
)
