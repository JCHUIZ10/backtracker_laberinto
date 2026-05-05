package com.uns.backtracker.usercase

import com.uns.backtracker.dominio.eventos.EventoLaberinto
import com.uns.backtracker.dominio.model.*
import com.uns.backtracker.observador.IObservadorLaberinto
import java.util.ArrayDeque

class UserCaseSolucionadorBFS {
    private val observadores = mutableListOf<IObservadorLaberinto>()
    fun agregarObservador(obs: IObservadorLaberinto) = observadores.add(obs)
    private fun emitir(evento: EventoLaberinto) = observadores.forEach { it.onEvento(evento) }

    fun resolver(grilla: List<List<Celda>>, config: ConfiguracionLaberinto): List<Coordenada> {
        val inicio = config.inicio
        val fin = config.fin
        val cola = ArrayDeque<Coordenada>()
        val predecesores = mutableMapOf<Coordenada, Coordenada?>()

        cola.add(inicio)
        predecesores[inicio] = null

        while (cola.isNotEmpty()) {
            val actual = cola.poll()
            if (actual == fin) break

            val celda = grilla[actual.fila][actual.columna]
            Direccion.entries.forEach { dir ->
                if (!celda.tienePared(dir)) {
                    val vecino = Coordenada(actual.fila + dir.dx, actual.columna + dir.dy)
                    if (vecino !in predecesores) {
                        predecesores[vecino] = actual
                        cola.add(vecino)
                    }
                }
            }
        }

        val camino = mutableListOf<Coordenada>()
        var temp: Coordenada? = fin
        while (temp != null) {
            camino.add(0, temp)
            temp = predecesores[temp]
        }

        emitir(EventoLaberinto.CaminoActualizado(camino))
        emitir(EventoLaberinto.FaseFinalizada("Solución BFS"))
        return camino
    }
}
