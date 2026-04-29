package com.uns.backtracker

import com.uns.backtracker.generador.GeneradorBacktrackingRecursivo
import com.uns.backtracker.model.ConfiguracionLaberinto
import com.uns.backtracker.model.EsquinaInicial
import kotlin.test.Test
import kotlin.test.assertEquals

class ComposeAppDesktopTest {

    @Test
    fun example() {
        val config =  ConfiguracionLaberinto(
            15,
            15,
            100,
            EsquinaInicial.INFERIOR_IZQ,
            3
        )

        val resultado = GeneradorBacktrackingRecursivo().generar(config)
    }
}