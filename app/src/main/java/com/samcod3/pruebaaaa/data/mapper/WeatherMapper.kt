package com.samcod3.pruebaaaa.data.mapper

import com.samcod3.pruebaaaa.data.local.entity.WeatherEntity
import com.samcod3.pruebaaaa.data.remote.model.DiaDto
import com.samcod3.pruebaaaa.data.remote.model.TownForecastDto
import com.samcod3.pruebaaaa.domain.model.DailyForecast
import com.samcod3.pruebaaaa.domain.model.HourlyForecast
import java.util.Calendar

/**
 * Convierte TownForecastDto a WeatherEntity con datos de viento opcionales externos
 */
import com.samcod3.pruebaaaa.data.remote.model.DailyForecastDto

/**
 * Convierte TownForecastDto a WeatherEntity con datos diarios externos opcionales
 */
fun TownForecastDto.toEntity(
    municipioId: String, 
    dailyForecastDto: DailyForecastDto? = null
): WeatherEntity? {
    val prediccionHoy = this.prediccion.dia.firstOrNull() ?: return null
    val todayDate = prediccionHoy.fecha

    // Extraer datos del objeto diario (si existe) para Hoy
    val dailyToday = dailyForecastDto?.prediccion?.dia?.find { it.fecha == todayDate }

    // --- Datos Actuales ---
    val temperaturas = prediccionHoy.temperatura?.mapNotNull { it.value?.toIntOrNull() } ?: emptyList()
    val maxTemp = temperaturas.maxOrNull() ?: 0
    val minTemp = temperaturas.minOrNull() ?: 0
    
    val currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    val tempActualDto = prediccionHoy.temperatura?.firstOrNull { 
        val periodoInt = it.periodo?.toIntOrNull() ?: -1
        periodoInt == currentHour 
    }
    val currentTemp = tempActualDto?.value?.toIntOrNull() ?: temperaturas.lastOrNull() ?: 0

    val estadoCielo = prediccionHoy.estadoCielo?.firstOrNull { 
         val periodoInt = it.periodo?.toIntOrNull() ?: -1
         periodoInt >= currentHour 
    } ?: prediccionHoy.estadoCielo?.firstOrNull()

    val maxLluvia = prediccionHoy.probPrecipitacion?.mapNotNull { it.value?.toIntOrNull() }?.maxOrNull() ?: 0
    
    // Viento: Intentar sacar del diario (más preciso) o fallback al horario
    var vientoVel = 0
    var vientoDir = ""
    
    if (dailyToday != null && dailyToday.viento != null) {
        // Lógica de periodo para viento diario (00-06, 06-12, etc)
        val currentPeriod = when {
            currentHour < 6 -> "00-06"
            currentHour < 12 -> "06-12"
            currentHour < 18 -> "12-18"
            else -> "18-24"
        }
        val v = dailyToday.viento.find { it.periodo == currentPeriod } 
             ?: dailyToday.viento.find { it.velocidad != null && it.velocidad > 0 }
             ?: dailyToday.viento.firstOrNull()
             
        vientoVel = v?.velocidad ?: 0
        vientoDir = v?.direccion ?: ""
    } else {
        // Fallback horario
        val vientoActual = prediccionHoy.viento?.firstOrNull {
            val periodoInt = it.periodo?.toIntOrNull() ?: -1
            periodoInt >= currentHour
        } ?: prediccionHoy.viento?.firstOrNull()
        
        vientoVel = vientoActual?.velocidad?.toIntOrNull() 
            ?: prediccionHoy.viento?.mapNotNull { it.velocidad?.toIntOrNull() }?.maxOrNull() ?: 0
        vientoDir = vientoActual?.direccion 
            ?: prediccionHoy.viento?.mapNotNull { it.direccion }?.firstOrNull() ?: ""
    }

    // --- Mapeo de Listas ---
    
    // 1. Hourly Forecast
    val hourlyList = mutableListOf<HourlyForecast>()
    this.prediccion.dia.take(2).forEachIndexed { index, diaDto ->
        val isToday = index == 0
        diaDto.temperatura?.forEach { tempItem ->
            val horaStr = tempItem.periodo ?: return@forEach
            val hora = horaStr.toIntOrNull() ?: return@forEach
            if (isToday && hora < currentHour) return@forEach
            
            val tempVal = tempItem.value?.toIntOrNull() ?: 0
            val cieloItem = diaDto.estadoCielo?.find { it.periodo == horaStr }
            val descCielo = cieloItem?.descripcion ?: ""
            val lluviaItem = diaDto.probPrecipitacion?.find { it.periodo == horaStr }
            val lluviaVal = lluviaItem?.value?.toIntOrNull() ?: 0
            val precipItem = diaDto.precipitacion?.find { it.periodo == horaStr }
            val precipVal = precipItem?.value?.toDoubleOrNull() ?: 0.0
            
            hourlyList.add(
                HourlyForecast(
                    hour = "$horaStr:00",
                    temperature = tempVal,
                    description = descCielo,
                    rainProbability = lluviaVal,
                    precipitation = precipVal
                )
            )
        }
    }

    // 2. Daily Forecast
    val dailyList = this.prediccion.dia.map { diaDto ->
        val temps = diaDto.temperatura?.mapNotNull { it.value?.toIntOrNull() } ?: emptyList()
        val dMax = temps.maxOrNull() ?: 0
        val dMin = temps.minOrNull() ?: 0
        
        val dCielo = diaDto.estadoCielo?.find { it.periodo == "12" } ?: diaDto.estadoCielo?.firstOrNull()
        val dDesc = dCielo?.descripcion ?: ""
        val dLluvia = diaDto.probPrecipitacion?.mapNotNull { it.value?.toIntOrNull() }?.maxOrNull() ?: 0

        // Datos extra del DTO diario
        val dExtra = dailyForecastDto?.prediccion?.dia?.find { it.fecha == diaDto.fecha }
        val dUv = dExtra?.uvMax ?: 0
        
        // Viento del día (buscamos el de las 12-18 o el maximo)
        val dVientoItem = dExtra?.viento?.find { it.periodo == "12-18" } 
             ?: dExtra?.viento?.maxByOrNull { it.velocidad ?: 0 }
        val dVientoVel = dVientoItem?.velocidad ?: 0
        val dVientoDir = dVientoItem?.direccion ?: ""

        // Sensación térmica y humedad (Calculado del horario)
        val sensList = diaDto.sensTermica?.mapNotNull { it.value?.toIntOrNull() } ?: emptyList()
        val dMaxSens = sensList.maxOrNull() ?: 0
        val dMinSens = sensList.minOrNull() ?: 0
        
        val humList = diaDto.humedadRelativa?.mapNotNull { it.value?.toIntOrNull() } ?: emptyList()
        val dMaxHum = humList.maxOrNull() ?: 0
        val dMinHum = humList.minOrNull() ?: 0

        val formattedDate = formatDate(diaDto.fecha)
        
        val dayHourlyList = mutableListOf<HourlyForecast>()
        diaDto.temperatura?.forEach { tempItem ->
            val horaStr = tempItem.periodo ?: return@forEach
            val tempVal = tempItem.value?.toIntOrNull() ?: 0
            val cieloItem = diaDto.estadoCielo?.find { it.periodo == horaStr }
            val descCielo = cieloItem?.descripcion ?: ""
            val lluviaItem = diaDto.probPrecipitacion?.find { it.periodo == horaStr }
            val lluviaVal = lluviaItem?.value?.toIntOrNull() ?: 0
            val precipItem = diaDto.precipitacion?.find { it.periodo == horaStr }
            val precipVal = precipItem?.value?.toDoubleOrNull() ?: 0.0
            
            dayHourlyList.add(HourlyForecast("$horaStr:00", tempVal, descCielo, lluviaVal, precipVal))
        }

        DailyForecast(
            date = diaDto.fecha,
            dateFormatted = formattedDate,
            maxTemp = dMax,
            minTemp = dMin,
            description = dDesc,
            rainProbability = dLluvia,
            windSpeed = dVientoVel,
            windDirection = dVientoDir,
            uvIndex = dUv,
            maxHumidity = dMaxHum,
            minHumidity = dMinHum,
            maxFeelTemp = dMaxSens,
            minFeelTemp = dMinSens,
            hourlyDetails = dayHourlyList
        )
    }

    return WeatherEntity(
        municipioId = municipioId,
        nombreMunicipio = this.nombre,
        provincia = this.provincia,
        lastUpdated = System.currentTimeMillis(),
        fecha = prediccionHoy.fecha,
        temperaturaMax = maxTemp,
        temperaturaMin = minTemp,
        temperaturaActual = currentTemp, 
        descripcionCielo = estadoCielo?.descripcion ?: "",
        estadoCieloCode = estadoCielo?.value,
        probabilidadLluvia = maxLluvia,
        vientoVelocidad = vientoVel,
        vientoDireccion = vientoDir,
        hourlyForecast = hourlyList,
        dailyForecast = dailyList
    )
}

/**
 * Formatea una fecha YYYY-MM-DD a un formato legible "Día DD" (ej: "Lunes 18")
 */
private fun formatDate(dateStr: String): String {
    return try {
        // Limpiar posible timestamp (ej. 2025-12-18T00:00:00)
        val cleanDate = dateStr.substringBefore("T")
        val parts = cleanDate.split("-")
        if (parts.size != 3) return dateStr
        
        val year = parts[0].toInt()
        val month = parts[1].toInt() - 1 // Calendar months are 0-indexed
        val day = parts[2].toInt()
        
        val calendar = Calendar.getInstance().apply {
            set(year, month, day)
        }
        
        val dayOfWeek = when (calendar.get(Calendar.DAY_OF_WEEK)) {
            Calendar.MONDAY -> "Lun"
            Calendar.TUESDAY -> "Mar"
            Calendar.WEDNESDAY -> "Mié"
            Calendar.THURSDAY -> "Jue"
            Calendar.FRIDAY -> "Vie"
            Calendar.SATURDAY -> "Sáb"
            Calendar.SUNDAY -> "Dom"
            else -> ""
        }
        
        "$dayOfWeek $day"
    } catch (e: Exception) {
        dateStr
    }
}
