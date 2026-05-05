package com.uns.backtracker.observador

import com.uns.backtracker.dominio.eventos.EventoLaberinto

interface IObservadorLaberinto {
    fun onEvento(evento: EventoLaberinto)
}
