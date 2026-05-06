package com.uns.backtracker.usercase

import com.uns.backtracker.dominio.eventos.EventoLaberinto
import com.uns.backtracker.dominio.model.*
import com.uns.backtracker.observador.IObservadorLaberinto
import kotlin.random.Random

class UserCaseGeneradorDFS {
    private val observadores = mutableListOf<IObservadorLaberinto>()

    fun agregarObservador(obs: IObservadorLaberinto) = observadores.add(obs)
    private fun emitir(evento: EventoLaberinto) = observadores.forEach { it.onEvento(evento) }

    fun generar(config: ConfiguracionLaberinto): List<List<Celda>> {
        val random = Random(config.semillaValue)
        val grilla = Array(config.filas) { r -> Array(config.columnas) { c -> Celda(r, c) } }
        val visitadas = mutableSetOf<Coordenada>()
        val pila = mutableListOf<Coordenada>()

        emitir(EventoLaberinto.Iniciado(config))
        
        val inicio = config.inicio
        visitadas.add(inicio)
        pila.add(inicio)

        while (pila.isNotEmpty()) {
            val actual = pila.last()
            val vecinos = obtenerVecinosNoVisitados(actual, grilla, visitadas)

            if (vecinos.isNotEmpty()) {
                // Estrategia de selección basada en dificultad (factorExploracion)
                val (vecino, direccion) = if (random.nextFloat() < config.dificultad.factorExploracion) {
                    vecinos.random(random)
                } else {
                    vecinos.first()
                }

                // Abrir paredes
                grilla[actual.fila][actual.columna] = grilla[actual.fila][actual.columna].copy(
                    paredes = grilla[actual.fila][actual.columna].paredes - direccion,
                    fueVisitada = true
                )
                grilla[vecino.fila][vecino.columna] = grilla[vecino.fila][vecino.columna].copy(
                    paredes = grilla[vecino.fila][vecino.columna].paredes - direccion.opuesta(),
                    fueVisitada = true
                )

                visitadas.add(vecino)
                pila.add(vecino)
                emitir(EventoLaberinto.Cavado(actual, vecino))
            } else {
                pila.removeAt(pila.size - 1)
                if (pila.isNotEmpty()) {
                    emitir(EventoLaberinto.Retroceso(actual, pila.last()))
                }
            }
        }

        emitir(EventoLaberinto.FaseFinalizada("Finalizar Generación Laberinto"))
        return grilla.map { it.toList() }
    }

    private fun obtenerVecinosNoVisitados(pos: Coordenada, grilla: Array<Array<Celda>>, visitadas: Set<Coordenada>): List<Pair<Coordenada, Direccion>> {
        return Direccion.entries.mapNotNull { dir ->
            val nf = pos.fila + dir.dx
            val nc = pos.columna + dir.dy
            if (nf in grilla.indices && nc in grilla[0].indices) {
                val vecino = Coordenada(nf, nc)
                if (!visitadas.contains(vecino)) Pair(vecino, dir) else null
            } else null
        }
    }
}
