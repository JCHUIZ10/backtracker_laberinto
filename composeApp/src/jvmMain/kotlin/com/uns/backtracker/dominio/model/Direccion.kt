package com.uns.backtracker.dominio.model

enum class Direccion(val dx: Int, val dy: Int) {
    ARRIBA(-1, 0),
    ABAJO(1, 0),
    IZQUIERDA(0, -1),
    DERECHA(0, 1);

    fun opuesta(): Direccion = when (this) {
        ARRIBA -> ABAJO
        ABAJO -> ARRIBA
        IZQUIERDA -> DERECHA
        DERECHA -> IZQUIERDA
    }
}
