package com.uns.backtracker.viewmodel

import com.uns.backtracker.dominio.eventos.EventoLaberinto
import com.uns.backtracker.dominio.model.*
import kotlin.random.Random

enum class ModoInteraccion {
    NINGUNO, SELECCIONANDO_INICIO, SELECCIONANDO_FIN
}

data class MazeState(
    val config: ConfiguracionLaberinto = ConfiguracionLaberinto(
        filas = 20,
        columnas = 20,
        inicio = Coordenada(0, 0),
        fin = Coordenada(19, 19),
        dificultad = Dificultad.FACIL,
        semillaValue = System.currentTimeMillis()
    ),
    
    val modoActual: ModoInteraccion = ModoInteraccion.NINGUNO,
    val laberintoFinal: Laberinto? = null,
    val animLaberinto: Laberinto? = null,
    val visitadas: Set<Coordenada> = emptySet(),
    val mineroPos: Coordenada? = null,
    val caminoOptimoVisual: List<Coordenada> = emptyList(),
    
    // Reproducción
    val eventos: List<EventoLaberinto> = emptyList(),
    val eventoActualIndex: Int = -1,
    val isPlaying: Boolean = false,
    val velocidadMs: Long = 30L
)
