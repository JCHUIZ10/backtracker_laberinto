package com.uns.backtracker.dominio.model

import kotlin.random.Random

data class ConfiguracionLaberinto(
    val filas: Int,
    val columnas: Int,
    val inicio: Coordenada,
    val fin: Coordenada,
    val dificultad: Dificultad,
    val semillaValue: Long
){
    init {
        require(filas > 0) { "Las filas deben ser mayores a 0" }
        require(columnas > 0) { "Las columnas deben ser mayores a 0" }
    }
}
