package com.uns.backtracker.usercase

import com.uns.backtracker.dominio.eventos.EventoBot
import com.uns.backtracker.dominio.model.*
import kotlin.random.Random

interface EstrategiaExploracion {
    fun explorar(laberinto: Laberinto): List<EventoBot>
}

class ExploracionDFS : EstrategiaExploracion {
    override fun explorar(laberinto: Laberinto): List<EventoBot> {
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
        var pasos = 0
        val MAX_PASOS = 5000
        
        while (pila.isNotEmpty() && actual != fin && pasos < MAX_PASOS) {
            pasos++
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

class ExploracionManoDerecha : EstrategiaExploracion {
    override fun explorar(laberinto: Laberinto): List<EventoBot> {
        val config = laberinto.configuracion
        val grilla = laberinto.grilla
        val eventos = mutableListOf<EventoBot>()
        
        val inicio = config.inicio
        val fin = config.fin
        
        eventos.add(EventoBot.Iniciar(inicio))
        
        var actual = inicio
        var heading = Direccion.DERECHA
        
        var pasos = 0
        val MAX_PASOS = 5000
        
        while (actual != fin && pasos < MAX_PASOS) {
            pasos++
            val celdaActual = grilla[actual.fila][actual.columna]
            
            // Determinar prioridad de direcciones para mano derecha:
            // 1. Derecha relativo a heading
            // 2. Recto (heading)
            // 3. Izquierda relativo a heading
            // 4. Atrás (opuesta)
            val dirDerecha = heading.rotarDerecha()
            val dirRecto = heading
            val dirIzquierda = heading.rotarIzquierda()
            val dirAtras = heading.opuesta()
            
            val ordenIntentos = listOf(dirDerecha, dirRecto, dirIzquierda, dirAtras)
            var movido = false
            
            for (dir in ordenIntentos) {
                if (!celdaActual.tienePared(dir)) {
                    val nf = actual.fila + dir.dx
                    val nc = actual.columna + dir.dy
                    if (nf in grilla.indices && nc in grilla[0].indices) {
                        val siguiente = Coordenada(nf, nc)
                        eventos.add(EventoBot.Avanzar(actual, siguiente))
                        actual = siguiente
                        heading = dir
                        movido = true
                        break
                    }
                }
            }
            if (!movido) {
                break
            }
        }
        
        val exito = actual == fin
        eventos.add(EventoBot.Finalizar(exito, actual))
        
        return eventos
    }
    
    private fun Direccion.rotarDerecha(): Direccion = when (this) {
        Direccion.ARRIBA -> Direccion.DERECHA
        Direccion.DERECHA -> Direccion.ABAJO
        Direccion.ABAJO -> Direccion.IZQUIERDA
        Direccion.IZQUIERDA -> Direccion.ARRIBA
    }

    private fun Direccion.rotarIzquierda(): Direccion = when (this) {
        Direccion.ARRIBA -> Direccion.IZQUIERDA
        Direccion.IZQUIERDA -> Direccion.ABAJO
        Direccion.ABAJO -> Direccion.DERECHA
        Direccion.DERECHA -> Direccion.ARRIBA
    }
}

class ExploracionAleatoria : EstrategiaExploracion {
    override fun explorar(laberinto: Laberinto): List<EventoBot> {
        val config = laberinto.configuracion
        val grilla = laberinto.grilla
        val random = Random(config.semillaValue)
        val eventos = mutableListOf<EventoBot>()
        
        val inicio = config.inicio
        val fin = config.fin
        
        eventos.add(EventoBot.Iniciar(inicio))
        
        var actual = inicio
        var pasos = 0
        val MAX_PASOS = 5000
        
        while (actual != fin && pasos < MAX_PASOS) {
            pasos++
            val celdaActual = grilla[actual.fila][actual.columna]
            
            val opciones = mutableListOf<Coordenada>()
            for (dir in Direccion.entries) {
                if (!celdaActual.tienePared(dir)) {
                    val nf = actual.fila + dir.dx
                    val nc = actual.columna + dir.dy
                    if (nf in grilla.indices && nc in grilla[0].indices) {
                        opciones.add(Coordenada(nf, nc))
                    }
                }
            }
            
            if (opciones.isNotEmpty()) {
                val siguiente = opciones.random(random)
                eventos.add(EventoBot.Avanzar(actual, siguiente))
                actual = siguiente
            } else {
                break
            }
        }
        
        val exito = actual == fin
        eventos.add(EventoBot.Finalizar(exito, actual))
        
        return eventos
    }
}
