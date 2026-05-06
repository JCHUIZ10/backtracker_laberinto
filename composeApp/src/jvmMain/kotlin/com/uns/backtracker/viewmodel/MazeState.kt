package com.uns.backtracker.viewmodel

import com.uns.backtracker.dominio.eventos.EventoLaberinto
import com.uns.backtracker.dominio.eventos.EventoBot
import com.uns.backtracker.dominio.model.*

enum class ModoInteraccion {
    NINGUNO, SELECCIONANDO_INICIO, SELECCIONANDO_FIN
}

data class MazeState(
    val config: ConfiguracionLaberinto = ConfiguracionLaberinto(
        filas = 10,
        columnas = 10,
        inicio = Coordenada(0, 0),
        fin = Coordenada(9, 9),
        dificultad = Dificultad.FACIL,
        semillaValue = 100
    ),
    
    val modoActual: ModoInteraccion = ModoInteraccion.NINGUNO,
    val laberintoFinal: Laberinto? = null,
    val animLaberinto: Laberinto? = null,
    val visitadas: Set<Coordenada> = emptySet(),
    val mineroPos: Coordenada? = null,
    val caminoOptimoVisual: List<Coordenada> = emptyList(),
    
    // Reproducción Laberinto
    val eventos: List<EventoLaberinto> = emptyList(),
    val eventoActualIndex: Int = -1,
    val isPlaying: Boolean = false,
    val velocidadMs: Long = 30L,

    // Bot Explorador
    val eventosBot: List<EventoBot> = emptyList(),
    val eventoBotActualIndex: Int = -1,
    val isBotPlaying: Boolean = false,
    val botPosActual: Coordenada? = null,
    
    // Toggles de Caminos
    val mostrarRutaOptima: Boolean = true,
    val mostrarRutaBot: Boolean = true
)