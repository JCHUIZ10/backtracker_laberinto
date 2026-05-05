package com.uns.backtracker.usercase

import com.uns.backtracker.dominio.model.*
import kotlin.math.abs

class UserCaseCalculadorMetricas {
    fun calcular(grilla: List<List<Celda>>, caminoOptimo: List<Coordenada>, config: ConfiguracionLaberinto): MetricasLaberinto {
        val totalCeldas = config.filas * config.columnas
        val lOptimo = caminoOptimo.size
        val dManhattan = abs(config.fin.fila - config.inicio.fila) + abs(config.fin.columna - config.inicio.columna) + 1
        
        val ratioOptimalidad = if (dManhattan > 0) lOptimo.toFloat() / dManhattan else 1f
        
        // Calcular ramificación (promedio de salidas > 2)
        var totalSalidas = 0
        var bifurcaciones = 0
        var paredesAbiertas = 0
        val profundidadesTrampas = mutableListOf<Int>()

        for (r in grilla.indices) {
            for (c in grilla[0].indices) {
                val celda = grilla[r][c]
                val salidas = Direccion.entries.count { !celda.tienePared(it) }
                totalSalidas += salidas
                paredesAbiertas += salidas
                if (salidas > 2) bifurcaciones++
                
                // Análisis de callejones (grado 1)
                if (salidas == 1 && Coordenada(r, c) != config.inicio && Coordenada(r, c) != config.fin) {
                    profundidadesTrampas.add(1) // Simplificado para este ejemplo
                }
            }
        }

        val factorRamificacion = if (totalCeldas > 0) totalSalidas.toFloat() / totalCeldas else 0f
        val densidadParedes = 1f - (paredesAbiertas.toFloat() / (totalCeldas * 4))

        return MetricasLaberinto(
            longitudCaminoOptimo = lOptimo,
            distanciaManhattan = dManhattan,
            ratioOptimalidad = ratioOptimalidad,
            factorRamificacion = factorRamificacion,
            profundidadMediaTrampas = profundidadesTrampas.average().toFloat().takeIf { !it.isNaN() } ?: 0f,
            ratioTrampasSimilares = 0.5f, // Placeholder para este restore
            totalCeldas = totalCeldas,
            densidadParedes = densidadParedes
        )
    }
}
