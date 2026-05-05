package com.uns.backtracker.dominio.model

data class Celda(
    val fila: Int,
    val columna: Int,
    val paredes: Set<Direccion> = Direccion.entries.toSet(),
    val fueVisitada: Boolean = false,
    val estaEnCaminoOptimo: Boolean = false
) {
    fun coordenada(): Coordenada = Coordenada(fila, columna)

    fun tienePared(direccion: Direccion): Boolean = paredes.contains(direccion)
}
