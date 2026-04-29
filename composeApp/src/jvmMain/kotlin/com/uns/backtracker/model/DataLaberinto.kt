package com.uns.backtracker.model

data class DataLaberinto(
    val laberinto: Laberinto,
    val inicio: Coordenada,
    val objetivo: Coordenada,
    val eventos: MutableList<EventoGeneracion>
)