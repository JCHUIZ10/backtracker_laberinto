package com.uns.backtracker.viewmodel

import com.uns.backtracker.generador.GeneradorBacktrackingRecursivo
import com.uns.backtracker.model.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class MazeState(
    val config: ConfiguracionLaberinto = ConfiguracionLaberinto(
        15, 15, 100,
        EsquinaInicial.SUPERIOR_IZQ,
        3
    ),
    val animLaberinto: Laberinto? = null,
    val visitadas: Set<Coordenada> = emptySet(),
    val huellas: List<Coordenada> = emptyList(),
    val mineroPos: Coordenada? = null,
    val totalEventos: Int = 0,
    val eventoActual: Int = 0,
    val isPlaying: Boolean = false,
    val velocidadMs: Long = 30L,
    val evaluacion: ResultadoEvaluacion? = null,
    val eventosHistory: List<EventoGeneracion> = emptyList()
)

class MazeViewModel {
    private val _state = MutableStateFlow(MazeState())
    val state: StateFlow<MazeState> = _state.asStateFlow()
    private var currentData: DataLaberinto? = null
    private var jobAnimacion: Job? = null
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    fun updateConfig(ancho: Int, alto: Int, semilla: Long, cuarto: Int) {
        _state.update { it.copy(config = it.config.copy(ancho = ancho, alto = alto, semilla = semilla, tamanoCuartoCentro = cuarto)) }
    }

    fun updateSpeed(speed: Long) {
        _state.update { it.copy(velocidadMs = speed) }
    }

    fun generar() {
        pausar()
        val cfg = _state.value.config
        val generador = GeneradorBacktrackingRecursivo()
        val resultado = generador.generar(cfg)
        currentData = resultado
        
        _state.update { it.copy(evaluacion = null, eventosHistory = resultado.eventos) }
        reiniciarAnimacion()
    }

    fun reiniciarAnimacion() {
        pausar()
        val data = currentData ?: return
        val nuevoLab = Laberinto(data.laberinto.alto, data.laberinto.ancho)
        
        _state.update { 
            it.copy(
                animLaberinto = nuevoLab,
                visitadas = emptySet(),
                huellas = emptyList(),
                mineroPos = null,
                eventoActual = 0,
                totalEventos = data.eventos.size
            )
        }
    }

    fun togglePlayPause() {
        if (_state.value.isPlaying) {
            pausar()
        } else {
            reproducir()
        }
    }

    private fun pausar() {
        _state.update { it.copy(isPlaying = false) }
        jobAnimacion?.cancel()
    }

    private fun reproducir() {
        if (currentData == null || _state.value.eventoActual >= (currentData?.eventos?.size ?: 0)) return
        _state.update { it.copy(isPlaying = true) }
        
        jobAnimacion = scope.launch {
            while (_state.value.isPlaying && _state.value.eventoActual < (currentData?.eventos?.size ?: 0)) {
                avanzarPaso()
                delay(_state.value.velocidadMs)
            }
            if (_state.value.eventoActual >= (currentData?.eventos?.size ?: 0)) {
                _state.update { it.copy(isPlaying = false) }
            }
        }
    }

    fun avanzarPaso() {
        val data = currentData ?: return
        val idx = _state.value.eventoActual
        if (idx >= data.eventos.size) {
            pausar()
            return
        }

        val evento = data.eventos[idx]
        val lab = _state.value.animLaberinto ?: return

        val nuevasVisitadas = _state.value.visitadas.toMutableSet()
        val nuevasHuellas = _state.value.huellas.toMutableList()
        var nuevoMinero: Coordenada? = _state.value.mineroPos

        when (evento) {
            is EventoGeneracion.Iniciado -> {
                nuevoMinero = evento.posicionInicial
                nuevasVisitadas.add(evento.posicionInicial)
                nuevasHuellas.add(evento.posicionInicial)
            }
            is EventoGeneracion.Cavado -> {
                lab.cavarCamino(evento.desde, evento.direccion)
                nuevasVisitadas.add(evento.hasta)
                nuevoMinero = evento.hasta
                nuevasHuellas.add(evento.hasta)
            }
            is EventoGeneracion.Retrocedido -> {
                if (nuevasHuellas.isNotEmpty()) nuevasHuellas.removeLast()
                nuevoMinero = evento.hasta
            }
            is EventoGeneracion.Finalizado -> {
                nuevoMinero = data.inicio
                // Al finalizar, forzamos que se use el laberinto completo de 'data' 
                // para que se vean las puertas del centro abiertas
                _state.update { it.copy(animLaberinto = data.laberinto) }
            }
        }

        _state.update { 
            it.copy(
                visitadas = nuevasVisitadas,
                huellas = nuevasHuellas,
                mineroPos = nuevoMinero,
                eventoActual = idx + 1
            )
        }
    }

    fun evaluarLaberinto() {
        val data = currentData ?: return
        val evaluador = com.uns.backtracker.service.EvaluadorLaberinto()
        val resultado = evaluador.evaluar(data.laberinto, data.inicio, data.objetivo, _state.value.config)
        _state.update { it.copy(evaluacion = resultado) }
    }
}
