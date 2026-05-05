package com.uns.backtracker.usercase

import com.uns.backtracker.dominio.eventos.EventoLaberinto
import com.uns.backtracker.dominio.model.*
import com.uns.backtracker.observador.IObservadorLaberinto

class GeneradorLaberintoFacade(
    private val generadorDFS: UserCaseGeneradorDFS,
    private val inyectorCiclos: UserCaseInyectorCiclos,
    private val solucionadorBFS: UserCaseSolucionadorBFS,
    private val calculadorMetricas: UserCaseCalculadorMetricas
) {
    private val observadores = mutableListOf<IObservadorLaberinto>()

    fun agregarObservador(obs: IObservadorLaberinto) {
        observadores.add(obs)
        generadorDFS.agregarObservador(obs)
        inyectorCiclos.agregarObservador(obs)
        solucionadorBFS.agregarObservador(obs)
    }

    private fun emitir(evento: EventoLaberinto) = observadores.forEach { it.onEvento(evento) }

    fun generar(config: ConfiguracionLaberinto): Laberinto {
        // Fase 1: Árbol de expansión mínimo vía DFS
        val grillaBase = generadorDFS.generar(config)
        
        // Fase 2: Inyección de ciclos (Braid Maze) según dificultad
        val grillaConCiclos = inyectorCiclos.inyectar(grillaBase, config)
        
        // Fase 3: Cálculo de la ruta óptima post-ciclos
        val caminoOptimo = solucionadorBFS.resolver(grillaConCiclos, config)
        
        // Fase 4: Evaluación científica del resultado
        val metricas = calculadorMetricas.calcular(grillaConCiclos, caminoOptimo, config)
        
        val resultado = Laberinto(
            configuracion = config,
            grilla = grillaConCiclos,
            caminoOptimo = caminoOptimo,
            dificultadEfectiva = config.dificultad,
            metricas = metricas
        )

        emitir(EventoLaberinto.Finalizado(resultado))
        return resultado
    }
}
