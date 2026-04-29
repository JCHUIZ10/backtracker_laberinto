package com.uns.backtracker.model

enum class Direccion(val dx:Int,val dy:Int) {
    ARRIBA(0, -1),
    ABAJO(0, 1),
    DERECHA(1, 0),
    IZQUIERDA(-1, 0);

    fun opuesta() : Direccion {
        return when (this) {
            ARRIBA -> ABAJO;
            ABAJO -> ARRIBA;
            DERECHA -> IZQUIERDA;
            IZQUIERDA -> DERECHA;
        };
    }
}