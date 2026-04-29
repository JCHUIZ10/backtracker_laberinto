package com.uns.backtracker.generador

import com.uns.backtracker.model.*
import com.uns.backtracker.util.UtilGeneracion
import kotlin.random.Random

class GeneradorBacktrackingRecursivo : GeneradorLaberinto {

    override fun generar(configuracion: ConfiguracionLaberinto): DataLaberinto {
        val azar = Random(configuracion.semilla)
        val laberinto = Laberinto(configuracion.ancho, configuracion.alto)
        val eventos = mutableListOf<EventoGeneracion>()

        val inicio = UtilGeneracion.resolverInicio(configuracion)
        UtilGeneracion.reservarCuartoCentro(laberinto, configuracion)

        // Usamos ArrayDeque de Kotlin (más eficiente que Stack)
        val pila = ArrayDeque<Coordenada>()

        pila.addFirst(inicio)
        laberinto.celdaEn(inicio).marcarVisitada()
        eventos.add(EventoGeneracion.Iniciado(inicio))

        while (pila.isNotEmpty()) {
            val actual = pila.first()
            
            // Usando el truco matemático del módulo
            val startIndex = azar.nextInt(4)
            var elegida: Direccion? = null

            for (i in 0 until 4) {
                val index = (startIndex + i) % 4
                val dir = Direccion.entries[index]
                val vecina = laberinto.obtenerCeldaVecinaDe(actual, dir)
                
                // Si la celda existe y no ha sido visitada, la elegimos
                if (vecina != null && !vecina.visitado) {
                    elegida = dir
                    break
                }
            }

            if (elegida == null) {
                pila.removeFirst()
                if (pila.isNotEmpty()) {
                    eventos.add(EventoGeneracion.Retrocedido(actual, pila.first()))
                }
            } else {
                val siguiente = actual.mover(elegida)

                laberinto.cavarCamino(actual, elegida)
                laberinto.celdaEn(siguiente).marcarVisitada()

                pila.addFirst(siguiente)
                eventos.add(EventoGeneracion.Cavado(actual, siguiente, elegida))
            }
        }

        UtilGeneracion.abrirPuertasCuartoCentro(laberinto, configuracion, azar)
        eventos.add(EventoGeneracion.Finalizado(eventos.size))

        val objetivo = Coordenada(configuracion.ancho / 2, configuracion.alto / 2)

        // Imprimir eventos de forma funcional
        eventos.forEach { println(it) }

        return DataLaberinto(laberinto, inicio, objetivo, eventos)
    }
}