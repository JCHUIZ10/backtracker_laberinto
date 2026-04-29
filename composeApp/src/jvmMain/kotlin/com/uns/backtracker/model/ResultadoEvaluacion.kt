package com.uns.backtracker.model

data class ResultadoEvaluacion(
    val totalCeldas: Int,
    val longitudSolucion: Int,
    val callejones: Int,
    val bifurcaciones: Int,
    val pasillos: Int,
    val pasilloMasLargo: Int,
    val complejidadSolucion: Double,
    val proporcionCallejones: Double,
    val densidadIntersecciones: Double,
    val coeficienteSinuosidad: Double,
    val sinuosidadRutaOptima: Double,
    val decisionesEnRuta: Int,
    val profundidadMediaFalsos: Double,
    val dificultad: Double,
    val rutaOptima: List<Coordenada>
) {
    fun nivelDificultad(): String {
        return when {
            dificultad < 0.3 -> "Fácil"
            dificultad < 0.6 -> "Medio"
            dificultad < 0.85 -> "Difícil"
            else -> "Extremo"
        }
    }
}
