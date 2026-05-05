package com.uns.backtracker.dominio.model

data class Laberinto(
    val configuracion: ConfiguracionLaberinto,
    val grilla: List<List<Celda>>,
    val caminoOptimo: List<Coordenada>,
    val dificultadEfectiva: Dificultad,
    val metricas: MetricasLaberinto
) {
    fun aTexto(): String {
        val sb = StringBuilder()
        for (fila in grilla.indices) {
            for (columna in grilla[fila].indices) {
                sb.append("+")
                sb.append(if (grilla[fila][columna].tienePared(Direccion.ARRIBA)) "---" else "   ")
            }
            sb.appendLine("+")
            for (columna in grilla[fila].indices) {
                val celda = grilla[fila][columna]
                val pos = Coordenada(fila, columna)
                val simbolo = when {
                    pos == configuracion.inicio      -> " S "
                    pos == configuracion.fin         -> " E "
                    caminoOptimo.contains(pos)       -> " * "
                    else                             -> "   "
                }
                sb.append(if (celda.tienePared(Direccion.IZQUIERDA)) "|" else " ")
                sb.append(simbolo)
            }
            sb.appendLine(if (grilla[fila].last().tienePared(Direccion.DERECHA)) "|" else " ")
        }
        for (columna in grilla.last().indices) {
            sb.append("+")
            sb.append(if (grilla.last()[columna].tienePared(Direccion.ABAJO)) "---" else "   ")
        }
        sb.appendLine("+")
        return sb.toString()
    }

    companion object {
        fun vacio(config: ConfiguracionLaberinto): Laberinto {
            val grilla = List(config.filas) { r ->
                List(config.columnas) { c ->
                    Celda(r, c)
                }
            }
            return Laberinto(
                configuracion = config,
                grilla = grilla,
                caminoOptimo = emptyList(),
                dificultadEfectiva = config.dificultad,
                metricas = MetricasLaberinto(0, 0, 1f, 0f, 0f, 0f, config.filas * config.columnas, 1f)
            )
        }
    }
}
