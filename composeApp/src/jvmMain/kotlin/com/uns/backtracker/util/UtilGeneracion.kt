package com.uns.backtracker.util

import com.uns.backtracker.model.ConfiguracionLaberinto
import com.uns.backtracker.model.Coordenada
import com.uns.backtracker.model.Direccion
import com.uns.backtracker.model.EsquinaInicial
import com.uns.backtracker.model.Laberinto
import kotlin.random.Random

object UtilGeneracion {

    fun resolverInicio(config: ConfiguracionLaberinto): Coordenada {
        val (ancho, alto) = config.ancho to config.alto
        return when (config.esquinaInicial) {
            EsquinaInicial.SUPERIOR_IZQ -> Coordenada(0, 0)
            EsquinaInicial.SUPERIOR_DER -> Coordenada(ancho - 1, 0)
            EsquinaInicial.INFERIOR_IZQ -> Coordenada(0, alto - 1)
            EsquinaInicial.INFERIOR_DER -> Coordenada(ancho - 1, alto - 1)
        }
    }

    fun reservarCuartoCentro(laberinto: Laberinto, config: ConfiguracionLaberinto) {
        val tamano = config.tamanoCuartoCentro
        if (tamano <= 0) return

        val cx = config.ancho / 2
        val cy = config.alto / 2
        
        val startX = cx - tamano / 2
        val endX = startX + tamano - 1
        val startY = cy - tamano / 2
        val endY = startY + tamano - 1

        for (y in startY..endY) {
            for (x in startX..endX) {
                val p = Coordenada(x, y)
                if (!laberinto.enLimites(p)) continue

                val celda = laberinto.celdaEn(p)
                celda.marcarVisitada()

                Direccion.entries.forEach { dir ->
                    val vecina = p.mover(dir)
                    if (estaDentroDelCuartoCentro(vecina, startX, endX, startY, endY) && laberinto.enLimites(vecina)) {
                        celda.abrirPared(dir)
                    }
                }
            }
        }
    }

    fun abrirPuertasCuartoCentro(laberinto: Laberinto, c: ConfiguracionLaberinto, azar: Random) {
        val tamano = c.tamanoCuartoCentro
        if (tamano <= 0) return

        val cx = c.ancho / 2
        val cy = c.alto / 2

        val startX = cx - tamano / 2
        val endX = startX + tamano - 1
        val startY = cy - tamano / 2
        val endY = startY + tamano - 1

        val puertasCandidatas = mutableListOf<CandidataPuerta>()

        for (y in startY..endY) {
            for (x in startX..endX) {
                val coordenada = Coordenada(x, y)
                if (!laberinto.enLimites(coordenada)) continue

                Direccion.entries.forEach { dir ->
                    val afuera = coordenada.mover(dir)
                    if (laberinto.enLimites(afuera) && !estaDentroDelCuartoCentro(afuera, startX, endX, startY, endY)) {
                        puertasCandidatas.add(CandidataPuerta(coordenada, dir))
                    }
                }
            }
        }

        if (puertasCandidatas.isEmpty()) return

        val maxPuertas = minOf(4, puertasCandidatas.size)
        val cantidadPuertas = azar.nextInt(1, maxPuertas + 1)

        repeat(cantidadPuertas) {
            if (puertasCandidatas.isNotEmpty()) {
                val puerta = puertasCandidatas.removeAt(azar.nextInt(puertasCandidatas.size))
                laberinto.cavarCamino(puerta.desde, puerta.direccion)
            }
        }
    }

    private fun estaDentroDelCuartoCentro(c: Coordenada, startX: Int, endX: Int, startY: Int, endY: Int): Boolean {
        return c.x in startX..endX && c.y in startY..endY
    }

    private data class CandidataPuerta(val desde: Coordenada, val direccion: Direccion)
}