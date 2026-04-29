package com.uns.backtracker.service

import com.uns.backtracker.model.*
import kotlin.math.max
import kotlin.math.min

class EvaluadorLaberinto {

    fun evaluar(laberinto: Laberinto, inicio: Coordenada, objetivo: Coordenada, config: ConfiguracionLaberinto): ResultadoEvaluacion {
        val totalCeldas = laberinto.ancho * laberinto.alto

        val tamano = config.tamanoCuartoCentro
        val cx = config.ancho / 2
        val cy = config.alto / 2
        val startX = cx - tamano / 2
        val endX = startX + tamano - 1
        val startY = cy - tamano / 2
        val endY = startY + tamano - 1

        val rutaOptima = caminoMasCorto(laberinto, inicio, startX, endX, startY, endY, objetivo)
        // Restamos 1 para contar movimientos (pasos) entre celdas, no el número de celdas.
        val longitudSolucion = (rutaOptima.size - 1).coerceAtLeast(0)

        var callejones = 0
        var bifurcaciones = 0
        var pasillos = 0

        for (celda in laberinto.todasLasCeldas()) {
            val p = celda.pasajes()
            when (p) {
                1 -> callejones++
                2 -> pasillos++
                else -> if (p >= 3) bifurcaciones++
            }
        }

        val pasilloMasLargo = cadenaPasilloMasLarga(laberinto)
        val complejidadSolucion = longitudSolucion.toDouble() / totalCeldas
        val proporcionCallejones = callejones.toDouble() / totalCeldas
        val densidadIntersecciones = bifurcaciones.toDouble() / totalCeldas
        val coeficienteSinuosidad = calcularCoeficienteSinuosidad(laberinto)

        // --- NUEVAS MÉTRICAS ---
        val decisionesEnRuta = rutaOptima.count { laberinto.celdaEn(it).pasajes() >= 3 }
        val sinuosidadRutaOptima = calcularSinuosidadRuta(rutaOptima)
        val profundidadMediaFalsos = calcularProfundidadMediaFalsos(laberinto, rutaOptima)

        val dificultad = calcularPuntajeDificultad(
            complejidadSolucion,
            proporcionCallejones,
            densidadIntersecciones,
            coeficienteSinuosidad,
            sinuosidadRutaOptima,
            decisionesEnRuta,
            profundidadMediaFalsos,
            longitudSolucion
        )

        return ResultadoEvaluacion(
            totalCeldas, longitudSolucion, callejones, bifurcaciones, pasillos, pasilloMasLargo,
            complejidadSolucion, proporcionCallejones, densidadIntersecciones, coeficienteSinuosidad,
            sinuosidadRutaOptima, decisionesEnRuta, profundidadMediaFalsos, dificultad,
            rutaOptima
        )
    }

    private fun caminoMasCorto(
        laberinto: Laberinto,
        inicio: Coordenada,
        startX: Int,
        endX: Int,
        startY: Int,
        endY: Int,
        objetivoRespaldo: Coordenada
    ): List<Coordenada> {
        val predecesor = mutableMapOf<Coordenada, Coordenada?>()
        val cola = ArrayDeque<Coordenada>()
        cola.addLast(inicio)
        predecesor[inicio] = null

        while (cola.isNotEmpty()) {
            val actual = cola.removeFirst()

            // Verificamos si alcanzamos CUALQUIER celda del cuarto centro o el objetivo
            val enCuarto = actual.x in startX..endX && actual.y in startY..endY
            if (enCuarto || actual == objetivoRespaldo) {
                val camino = mutableListOf<Coordenada>()
                var p: Coordenada? = actual
                while (p != null) {
                    camino.add(0, p)
                    p = predecesor[p]
                }
                return camino
            }
            val celda = laberinto.celdaEn(actual)
            for (dir in Direccion.entries) {
                if (celda.estaAbiertaHacia(dir)) {
                    val siguiente = actual.mover(dir)
                    if (!predecesor.containsKey(siguiente) && laberinto.enLimites(siguiente)) {
                        predecesor[siguiente] = actual
                        cola.addLast(siguiente)
                    }
                }
            }
        }
        return emptyList()
    }

    private fun cadenaPasilloMasLarga(laberinto: Laberinto): Int {
        var maxLen = 0
        val vistas = mutableSetOf<Coordenada>()
        for (celda in laberinto.todasLasCeldas()) {
            if (celda.pasajes() == 2 && !vistas.contains(celda.coordenada)) {
                val longitud = trazarCadenaPasillo(laberinto, celda, vistas)
                maxLen = max(maxLen, longitud)
            }
        }
        return maxLen
    }

    private fun trazarCadenaPasillo(laberinto: Laberinto, inicio: Celda, vistas: MutableSet<Coordenada>): Int {
        val pila = ArrayDeque<Coordenada>()
        pila.addLast(inicio.coordenada)
        var longitud = 0

        while (pila.isNotEmpty()) {
            val p = pila.removeLast()
            if (vistas.contains(p)) continue

            val c = laberinto.celdaEn(p)
            if (c.pasajes() != 2) continue

            vistas.add(p)
            longitud++

            for (d in Direccion.entries) {
                if (c.estaAbiertaHacia(d)) {
                    val n = p.mover(d)
                    if (laberinto.enLimites(n) && !vistas.contains(n) && laberinto.celdaEn(n).pasajes() == 2) {
                        pila.addLast(n)
                    }
                }
            }
        }
        return longitud
    }

    private fun calcularCoeficienteSinuosidad(laberinto: Laberinto): Double {
        var giros = 0
        var rectos = 0

        for (celda in laberinto.todasLasCeldas()) {
            if (celda.pasajes() != 2) continue
            val tieneNorte = celda.estaAbiertaHacia(Direccion.ARRIBA)
            val tieneSur = celda.estaAbiertaHacia(Direccion.ABAJO)
            val tieneEste = celda.estaAbiertaHacia(Direccion.DERECHA)
            val tieneOeste = celda.estaAbiertaHacia(Direccion.IZQUIERDA)
            val esRecto = (tieneNorte && tieneSur) || (tieneEste && tieneOeste)
            if (esRecto) rectos++
            else giros++
        }
        val total = giros + rectos
        return if (total == 0) 0.0 else giros.toDouble() / total
    }

    private fun calcularSinuosidadRuta(ruta: List<Coordenada>): Double {
        if (ruta.size < 3) return 0.0
        var giros = 0
        for (i in 1 until ruta.size - 1) {
            val ant = ruta[i - 1]
            val actual = ruta[i]
            val sig = ruta[i + 1]

            val mismoEjeX = ant.x == actual.x && actual.x == sig.x
            val mismoEjeY = ant.y == actual.y && actual.y == sig.y

            if (!mismoEjeX && !mismoEjeY) {
                giros++
            }
        }
        return giros.toDouble() / (ruta.size - 2)
    }

    private fun calcularProfundidadMediaFalsos(laberinto: Laberinto, rutaOptima: List<Coordenada>): Double {
        val enRuta = rutaOptima.toSet()
        val profundidades = mutableListOf<Int>()

        for (coord in rutaOptima) {
            val celda = laberinto.celdaEn(coord)
            for (dir in Direccion.entries) {
                if (celda.estaAbiertaHacia(dir)) {
                    val vecina = coord.mover(dir)
                    if (laberinto.enLimites(vecina) && !enRuta.contains(vecina)) {
                        profundidades.add(explorarProfundidadRama(laberinto, vecina, enRuta))
                    }
                }
            }
        }

        return if (profundidades.isEmpty()) 0.0 else profundidades.average()
    }

    private fun explorarProfundidadRama(laberinto: Laberinto, inicio: Coordenada, bloqueadas: Set<Coordenada>): Int {
        val vistas = bloqueadas.toMutableSet()
        var maxDepth = 0
        val stack = mutableListOf(inicio to 1)

        while (stack.isNotEmpty()) {
            val (actual, depth) = stack.removeAt(stack.size - 1)
            if (actual in vistas) continue
            vistas.add(actual)
            maxDepth = max(maxDepth, depth)

            val celda = laberinto.celdaEn(actual)
            for (dir in Direccion.entries) {
                if (celda.estaAbiertaHacia(dir)) {
                    val sig = actual.mover(dir)
                    if (laberinto.enLimites(sig) && sig !in vistas) {
                        stack.add(sig to depth + 1)
                    }
                }
            }
        }
        return maxDepth
    }

    private fun calcularPuntajeDificultad(
        complejidadSolucion: Double,
        proporcionCallejones: Double,
        densidadIntersecciones: Double,
        coeficienteSinuosidad: Double,
        sinuosidadRuta: Double,
        decisionesEnRuta: Int,
        profundidadFalsos: Double,
        longitudRuta: Int
    ): Double {
        val ratioDecisiones = if (longitudRuta > 0) decisionesEnRuta.toDouble() / longitudRuta else 0.0
        val profundidadNormalizada = min(profundidadFalsos / 15.0, 1.0)

        return (ratioDecisiones * 0.35) +
                (complejidadSolucion * 0.25) +
                (profundidadNormalizada * 0.25) +
                (sinuosidadRuta * 0.10) +
                (densidadIntersecciones * 0.05)
    }
}
