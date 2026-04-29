package com.uns.backtracker.model

sealed interface EventoGeneracion {

    data class Iniciado(val posicionInicial: Coordenada) : EventoGeneracion

    data class Cavado(val desde: Coordenada, val hasta: Coordenada, val direccion: Direccion) : EventoGeneracion

    data class Retrocedido(val desde: Coordenada, val hasta: Coordenada) : EventoGeneracion

    data class Finalizado(val totalPasos: Int) : EventoGeneracion
}