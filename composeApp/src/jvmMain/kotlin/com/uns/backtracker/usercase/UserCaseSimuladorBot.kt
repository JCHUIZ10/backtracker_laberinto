package com.uns.backtracker.usercase

import com.uns.backtracker.dominio.eventos.EventoBot
import com.uns.backtracker.dominio.model.*

class UserCaseSimuladorBot {
    fun simular(laberinto: Laberinto, algoritmo: AlgoritmoBot): List<EventoBot> {
        val estrategia: EstrategiaExploracion = when (algoritmo) {
            AlgoritmoBot.DFS_ALEATORIO -> ExploracionDFS()
            AlgoritmoBot.MANO_DERECHA -> ExploracionManoDerecha()
            AlgoritmoBot.ALEATORIO_PURO -> ExploracionAleatoria()
        }
        return estrategia.explorar(laberinto)
    }
}
