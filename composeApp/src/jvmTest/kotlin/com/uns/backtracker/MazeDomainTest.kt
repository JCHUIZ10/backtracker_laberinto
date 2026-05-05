package com.uns.backtracker

import com.uns.backtracker.dominio.model.*
import com.uns.backtracker.usercase.*
import kotlin.test.Test
import kotlin.test.assertTrue
import kotlin.test.assertNotNull

class MazeDomainTest {

    @Test
    fun verificargeneracionLaberinto() {
        val config = ConfiguracionLaberinto(
            filas = 10,
            columnas = 10,
            inicio = Coordenada(0, 0),
            fin = Coordenada(9, 9),
            dificultad = Dificultad.FACIL,
            semillaValue = 12345L
        )

        val facade = GeneradorLaberintoFacade(
            UserCaseGeneradorDFS(kotlin.random.Random(12345)),
            UserCaseInyectorCiclos(),
            UserCaseSolucionadorBFS(),
            UserCaseCalculadorMetricas()
        )

        val laberinto = facade.generar(config)

        // Verificar que todas las celdas (excepto bordes) tengan al menos una pared abierta
        laberinto.grilla.flatten().forEach { celda ->
            val paredesAbiertas = Direccion.entries.count { !celda.tienePared(it) }
            assertTrue(paredesAbiertas > 0, "La celda en [${celda.fila}, ${celda.columna}] está aislada")
        }
    }

    @Test
    fun verificarBFS() {
        val config = ConfiguracionLaberinto(
            filas = 5,
            columnas = 5,
            inicio = Coordenada(0, 0),
            fin = Coordenada(4, 4),
            dificultad = Dificultad.FACIL,
            semillaValue = 42L
        )

        val facade = GeneradorLaberintoFacade(
            UserCaseGeneradorDFS(kotlin.random.Random(42)),
            UserCaseInyectorCiclos(),
            UserCaseSolucionadorBFS(),
            UserCaseCalculadorMetricas()
        )

        val laberinto = facade.generar(config)
        
        assertNotNull(laberinto.caminoOptimo)
        assertTrue(laberinto.caminoOptimo.isNotEmpty(), "No se encontró camino óptimo")
        assertTrue(laberinto.caminoOptimo.first() == config.inicio)
        assertTrue(laberinto.caminoOptimo.last() == config.fin)
    }
}
