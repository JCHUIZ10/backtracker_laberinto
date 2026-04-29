package com.uns.backtracker.generador

import com.uns.backtracker.model.ConfiguracionLaberinto
import com.uns.backtracker.model.DataLaberinto

interface GeneradorLaberinto {

    fun generar(configuracion: ConfiguracionLaberinto): DataLaberinto

}