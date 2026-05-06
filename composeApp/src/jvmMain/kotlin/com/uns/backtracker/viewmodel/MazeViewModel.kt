package com.uns.backtracker.viewmodel

import com.uns.backtracker.dominio.eventos.EventoLaberinto
import com.uns.backtracker.dominio.eventos.EventoBot
import com.uns.backtracker.dominio.model.*
import com.uns.backtracker.observador.IObservadorLaberinto
import com.uns.backtracker.usercase.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlin.random.Random

class MazeViewModel : IObservadorLaberinto {
    private val _state = MutableStateFlow(MazeState())
    val state: StateFlow<MazeState> = _state.asStateFlow()

    private val viewModelScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private var playbackJob: Job? = null
    private var botPlaybackJob: Job? = null

    private val facade = GeneradorLaberintoFacade(
        generadorDFS = UserCaseGeneradorDFS(),
        inyectorCiclos = UserCaseInyectorCiclos(),
        solucionadorBFS = UserCaseSolucionadorBFS(),
        calculadorMetricas = UserCaseCalculadorMetricas()
    ).also { it.agregarObservador(this) }

    init {
        _state.update { it.copy(animLaberinto = Laberinto.vacio(it.config)) }
    }

    fun setModoInteraccion(modo: ModoInteraccion) {
        _state.update { it.copy(modoActual = modo) }
    }

    fun onCeldaClickeada(coordenada: Coordenada) {
        val estadoActual = _state.value
        val config = estadoActual.config
        if (estadoActual.modoActual == ModoInteraccion.SELECCIONANDO_INICIO) {
            if (coordenada != config.fin) {
                _state.update { it.copy(config = config.copy(inicio = coordenada), modoActual = ModoInteraccion.NINGUNO) }
            }
        } else if (estadoActual.modoActual == ModoInteraccion.SELECCIONANDO_FIN) {
            if (coordenada != config.inicio) {
                _state.update { it.copy(config = config.copy(fin = coordenada), modoActual = ModoInteraccion.NINGUNO) }
            }
        }
    }

    fun updateConfigManual(ancho: Int, alto: Int, semilla: Long, dificultad: Dificultad, inicio: Coordenada, fin: Coordenada) {
        _state.update { 
            val nuevaConfig = it.config.copy(
                filas = alto,
                columnas = ancho,
                semillaValue = semilla,
                inicio = inicio,
                fin = fin,
                dificultad = dificultad
            )
            it.copy(
                config = nuevaConfig,
                animLaberinto = if (it.laberintoFinal == null) Laberinto.vacio(nuevaConfig) else it.animLaberinto
            )
        }
    }

    fun generar() {
        stopPlayback()
        stopBotPlayback()
        _state.update { 
            it.copy(
                eventos = emptyList(),
                eventoActualIndex = -1,
                visitadas = emptySet(),
                mineroPos = null,
                caminoOptimoVisual = emptyList(),
                laberintoFinal = null,
                eventosBot = emptyList(),
                eventoBotActualIndex = -1,
                isBotPlaying = false,
                botPosActual = null
            )
        }

        viewModelScope.launch(Dispatchers.Default) {
            val laberinto = facade.generar(_state.value.config)
            
            val botSimulador = UserCaseSimuladorBot()
            val botEvents = botSimulador.simular(laberinto)
            
            _state.update { 
                it.copy(
                    laberintoFinal = laberinto,
                    eventosBot = botEvents
                ) 
            }
            startPlayback()
        }
    }

    override fun onEvento(evento: EventoLaberinto) {
        _state.update { it.copy(eventos = it.eventos + evento) }
    }

    fun togglePlayPause() {
        if (_state.value.isPlaying) stopPlayback() else startPlayback()
    }

    fun avanzarPaso() = procesarSiguienteEvento()

    fun updateSpeed(speed: Long) {
        _state.update { it.copy(velocidadMs = speed) }
    }

    private fun startPlayback() {
        if (playbackJob?.isActive == true) return
        _state.update { it.copy(isPlaying = true) }
        playbackJob = viewModelScope.launch {
            while (isActive && _state.value.eventoActualIndex < _state.value.eventos.size - 1) {
                procesarSiguienteEvento()
                delay(_state.value.velocidadMs)
            }
            _state.update { it.copy(isPlaying = false) }
        }
    }

    private fun stopPlayback() {
        playbackJob?.cancel()
        _state.update { it.copy(isPlaying = false) }
    }

    private fun procesarSiguienteEvento() {
        val nextIndex = _state.value.eventoActualIndex + 1
        if (nextIndex >= _state.value.eventos.size) return
        
        val evento = _state.value.eventos[nextIndex]
        _state.update { current ->
            when (evento) {
                is EventoLaberinto.Iniciado -> {
                    current.copy(
                        eventoActualIndex = nextIndex,
                        animLaberinto = Laberinto.vacio(evento.configuracion)
                    )
                }
                is EventoLaberinto.Cavado -> {
                    val nuevaGrilla = abrirPared(current.animLaberinto!!.grilla, evento.desde, evento.hacia)
                    current.copy(
                        eventoActualIndex = nextIndex,
                        visitadas = current.visitadas + evento.hacia,
                        mineroPos = evento.hacia,
                        animLaberinto = current.animLaberinto?.copy(grilla = nuevaGrilla)
                    )
                }
                is EventoLaberinto.Retroceso -> {
                    current.copy(eventoActualIndex = nextIndex, mineroPos = evento.hacia)
                }
                is EventoLaberinto.CicloCreado -> {
                    val nuevaGrilla = abrirPared(current.animLaberinto!!.grilla, evento.desde, evento.hacia)
                    current.copy(
                        eventoActualIndex = nextIndex,
                        animLaberinto = current.animLaberinto?.copy(grilla = nuevaGrilla)
                    )
                }
                is EventoLaberinto.CaminoActualizado -> {
                    current.copy(eventoActualIndex = nextIndex, caminoOptimoVisual = evento.camino)
                }
                is EventoLaberinto.Finalizado -> {
                    current.copy(
                        eventoActualIndex = nextIndex,
                        animLaberinto = evento.laberinto,
                        caminoOptimoVisual = evento.laberinto.caminoOptimo
                    )
                }
                else -> current.copy(eventoActualIndex = nextIndex)
            }
        }
    }

    private fun abrirPared(grilla: List<List<Celda>>, desde: Coordenada, hacia: Coordenada): List<List<Celda>> {
        val dir = Direccion.entries.find { desde.fila + it.dx == hacia.fila && desde.columna + it.dy == hacia.columna }
            ?: return grilla
            
        return grilla.mapIndexed { r, fila ->
            fila.mapIndexed { c, celda ->
                when {
                    r == desde.fila && c == desde.columna -> celda.copy(paredes = celda.paredes - dir)
                    r == hacia.fila && c == hacia.columna -> celda.copy(paredes = celda.paredes - dir.opuesta())
                    else -> celda
                }
            }
        }
    }

    // --- Controladores de reproducción del Bot Explorador ---
    
    fun toggleBotPlayPause() {
        if (_state.value.isBotPlaying) stopBotPlayback() else startBotPlayback()
    }

    fun avanzarPasoBot() {
        procesarSiguienteEventoBot()
    }

    private fun startBotPlayback() {
        if (botPlaybackJob?.isActive == true) return
        _state.update { it.copy(isBotPlaying = true) }
        botPlaybackJob = viewModelScope.launch {
            while (isActive && _state.value.eventoBotActualIndex < _state.value.eventosBot.size - 1) {
                procesarSiguienteEventoBot()
                delay(_state.value.velocidadMs)
            }
            _state.update { it.copy(isBotPlaying = false) }
        }
    }

    private fun stopBotPlayback() {
        botPlaybackJob?.cancel()
        _state.update { it.copy(isBotPlaying = false) }
    }

    private fun procesarSiguienteEventoBot() {
        val nextIndex = _state.value.eventoBotActualIndex + 1
        if (nextIndex >= _state.value.eventosBot.size) return
        
        val evento = _state.value.eventosBot[nextIndex]
        _state.update { current ->
            when (evento) {
                is EventoBot.Iniciar -> {
                    current.copy(
                        eventoBotActualIndex = nextIndex,
                        botPosActual = evento.inicio
                    )
                }
                is EventoBot.Avanzar -> {
                    current.copy(
                        eventoBotActualIndex = nextIndex,
                        botPosActual = evento.hacia
                    )
                }
                is EventoBot.Retroceder -> {
                    current.copy(
                        eventoBotActualIndex = nextIndex,
                        botPosActual = evento.hacia
                    )
                }
                is EventoBot.Finalizar -> {
                    current.copy(
                        eventoBotActualIndex = nextIndex,
                        botPosActual = evento.fin
                    )
                }
            }
        }
    }

    fun toggleMostrarRutaOptima() {
        _state.update { it.copy(mostrarRutaOptima = !it.mostrarRutaOptima) }
    }

    fun toggleMostrarRutaBot() {
        _state.update { it.copy(mostrarRutaBot = !it.mostrarRutaBot) }
    }
}

