package com.uns.backtracker.model

data class Coordenada( val x:Int, val y:Int) {

    fun mover(direccion:Direccion):Coordenada {
        return Coordenada(x + direccion.dx, y + direccion.dy)
    }

}