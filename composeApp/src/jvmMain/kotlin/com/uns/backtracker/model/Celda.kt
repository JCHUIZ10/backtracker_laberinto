package com.uns.backtracker.model

import java.util.EnumSet;

class Celda(
    val coordenada: Coordenada,
    val paredesAbiertas: EnumSet<Direccion> = EnumSet.noneOf(Direccion::class.java),
    var visitado: Boolean = false
) {

    fun abrirPared(direccion:Direccion) : Unit{
        paredesAbiertas.add(direccion);
    }

    fun estaAbiertaHacia(direccion:Direccion) : Boolean{
        return paredesAbiertas.contains(direccion);
    }

    fun marcarVisitada() {
        this.visitado = true;
    }

    fun pasajes() : Int {
        return paredesAbiertas.size;
    }
}