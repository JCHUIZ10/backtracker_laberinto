package com.uns.backtracker.usercase

import com.uns.backtracker.dominio.eventos.EventoLaberinto
import com.uns.backtracker.dominio.model.*
import com.uns.backtracker.observador.IObservadorLaberinto
import kotlin.random.Random

class UserCaseInyectorCiclos {
    private val observadores = mutableListOf<IObservadorLaberinto>()
    fun agregarObservador(obs: IObservadorLaberinto) = observadores.add(obs)
    private fun emitir(evento: EventoLaberinto) = observadores.forEach { it.onEvento(evento) }

    fun inyectar(grilla: List<List<Celda>>, config: ConfiguracionLaberinto): List<List<Celda>> {
        val mutableGrilla = grilla.map { it.toMutableList() }.toMutableList()
        val random = Random(config.semillaValue)
        val filas = config.filas
        val columnas = config.columnas
        
        // Calcular cuántas paredes abrir según la dificultad
        val totalParedesInternas = (filas * (columnas - 1)) + (columnas * (filas - 1))
        val aAbrir = (totalParedesInternas * config.dificultad.tasaAperturaCiclos).toInt()

        repeat(aAbrir) {
            val r = random.nextInt(filas)
            val c = random.nextInt(columnas)
            val dir = Direccion.entries.random(random)
            
            val nr = r + dir.dx
            val nc = c + dir.dy
            
            if (nr in 0 until filas && nc in 0 until columnas) {
                val actual = mutableGrilla[r][c]
                val vecino = mutableGrilla[nr][nc]
                
                if (actual.tienePared(dir)) {
                    mutableGrilla[r][c] = actual.copy(paredes = actual.paredes - dir)
                    mutableGrilla[nr][nc] = vecino.copy(paredes = vecino.paredes - dir.opuesta())
                    emitir(EventoLaberinto.CicloCreado(Coordenada(r, c), Coordenada(nr, nc)))
                }
            }
        }

        emitir(EventoLaberinto.FaseFinalizada("Inyección de Ciclos"))
        return mutableGrilla.map { it.toList() }
    }
}
