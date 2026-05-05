package com.uns.backtracker.dominio.eventos

import com.uns.backtracker.dominio.model.*

sealed class EventoLaberinto {
    data class Iniciado(val configuracion: ConfiguracionLaberinto) : EventoLaberinto()
    data class Cavado(val desde: Coordenada, val hacia: Coordenada) : EventoLaberinto()
    data class Retroceso(val desde: Coordenada, val hacia: Coordenada) : EventoLaberinto()
    data class CicloCreado(val desde: Coordenada, val hacia: Coordenada) : EventoLaberinto()
    data class FaseFinalizada(val nombreFase: String) : EventoLaberinto()
    data class CaminoActualizado(val camino: List<Coordenada>) : EventoLaberinto()
    data class Finalizado(val laberinto: Laberinto) : EventoLaberinto()
}
