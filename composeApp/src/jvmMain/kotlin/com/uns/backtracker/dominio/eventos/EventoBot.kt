package com.uns.backtracker.dominio.eventos

import com.uns.backtracker.dominio.model.Coordenada

sealed class EventoBot {
    data class Iniciar(val inicio: Coordenada) : EventoBot()
    data class Avanzar(val desde: Coordenada, val hacia: Coordenada) : EventoBot()
    data class Retroceder(val desde: Coordenada, val hacia: Coordenada) : EventoBot()
    data class Finalizar(val exito: Boolean, val fin: Coordenada) : EventoBot()
}
