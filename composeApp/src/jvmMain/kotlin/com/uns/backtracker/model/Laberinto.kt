package com.uns.backtracker.model

import java.util.*


class Laberinto(
    val alto:Int = 30,
    val ancho:Int = 30,
) {
    // Definimos la matriz que contendrá las celdas después de inicializarse
    val celdas: Array<Array<Celda>> = Array(alto) { fila ->
        Array(ancho) { columna ->
            Celda(Coordenada(fila, columna))
        }
    }

    fun enLimites(c: Coordenada): Boolean {
        return (c.x >= 0) && (c.x < ancho) && (c.y >= 0) && (c.y < alto)
    }

    fun celdaEn(c: Coordenada): Celda {
        require(enLimites(c)) { "Coordenada fuera de limites: $c" }
        return celdas[c.y][c.x]
    }

    fun obtenerCeldaVecinaDe(c: Coordenada, direccion: Direccion): Celda? {
        return c.mover(direccion).takeIf { enLimites(it) }?.let { celdaEn(it) }
    }

    fun cavarCamino(desde: Coordenada, direccion: Direccion) {
        val origen = celdaEn(desde)
        val destino = celdaEn(desde.mover(direccion))
        origen.abrirPared(direccion)
        destino.abrirPared(direccion.opuesta())
    }

    fun todasLasCeldas(): MutableList<Celda> {
        val lista: MutableList<Celda> = ArrayList<Celda>(ancho * alto)
        for (y in 0..<alto) {
            for (x in 0..<ancho) {
                lista.add(celdas[y][x])
            }
        }
        return lista
    }
}