package com.uns.backtracker.usercase

import com.uns.backtracker.dominio.eventos.EventoBot
import com.uns.backtracker.dominio.model.*
import kotlin.random.Random

class UserCaseSimuladorBot {
    fun simular(laberinto: Laberinto): List<EventoBot> {
        val config = laberinto.configuracion
        val grilla = laberinto.grilla
        val random = Random(config.semillaValue)
        
        val eventos = mutableListOf<EventoBot>()
        val visitadas = mutableSetOf<Coordenada>()
        val pila = mutableListOf<Coordenada>()
        
        val inicio = config.inicio
        val fin = config.fin
        
        eventos.add(EventoBot.Iniciar(inicio))
        visitadas.add(inicio)
        pila.add(inicio)
        
        var actual = inicio
        
        while (pila.isNotEmpty() && actual != fin) {
            val celdaActual = grilla[actual.fila][actual.columna]
            
            // Obtener vecinos conectados (sin pared) y que no hayan sido visitados
            val vecinosDisponibles = mutableListOf<Pair<Coordenada, Direccion>>()
            for (dir in Direccion.entries) {
                if (!celdaActual.tienePared(dir)) {
                    val nf = actual.fila + dir.dx
                    val nc = actual.columna + dir.dy
                    if (nf in grilla.indices && nc in grilla[0].indices) {
                        val vecino = Coordenada(nf, nc)
                        if (!visitadas.contains(vecino)) {
                            vecinosDisponibles.add(Pair(vecino, dir))
                        }
                    }
                }
            }
            
            if (vecinosDisponibles.isNotEmpty()) {
                // Elegir un vecino al azar usando la semilla
                val (siguiente, _) = vecinosDisponibles.random(random)
                eventos.add(EventoBot.Avanzar(actual, siguiente))
                visitadas.add(siguiente)
                pila.add(siguiente)
                actual = siguiente
            } else {
                // Backtracking
                pila.removeAt(pila.size - 1)
                if (pila.isNotEmpty()) {
                    val anterior = pila.last()
                    eventos.add(EventoBot.Retroceder(actual, anterior))
                    actual = anterior
                }
            }
        }
        
        val exito = actual == fin
        eventos.add(EventoBot.Finalizar(exito, actual))
        
        return eventos
    }
}
