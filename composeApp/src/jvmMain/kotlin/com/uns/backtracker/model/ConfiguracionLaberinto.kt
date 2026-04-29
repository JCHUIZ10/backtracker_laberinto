package com.uns.backtracker.model

data class ConfiguracionLaberinto(
    val ancho : Int,
    val alto : Int,
    val semilla: Long,
    val esquinaInicial: EsquinaInicial,
    val tamanoCuartoCentro: Int
)