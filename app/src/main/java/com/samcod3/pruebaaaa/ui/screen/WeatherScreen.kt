package com.samcod3.pruebaaaa.ui.screen

import android.Manifest
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.WbSunny
// import androidx.compose.material.icons.filled.Thermostat // Puede fallar si la version compose es vieja
import androidx.compose.material.icons.filled.DeviceThermostat
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.samcod3.pruebaaaa.domain.model.DailyForecast
import com.samcod3.pruebaaaa.domain.model.HourlyForecast
import com.samcod3.pruebaaaa.domain.model.Weather
import com.samcod3.pruebaaaa.ui.viewmodel.WeatherUiState
import com.samcod3.pruebaaaa.ui.viewmodel.WeatherViewModel

@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun WeatherScreen(
    viewModel: WeatherViewModel = hiltViewModel()
) {
    // ... (logic remains same) ...
    val uiState by viewModel.uiState.collectAsState()
    val showApiKeyDialog by viewModel.showApiKeyDialog.collectAsState()
    val isFavorite by viewModel.isFavorite.collectAsState()
    
    // Estado para el Bottom Sheet de detalle diario
    var selectedDailyForecast by remember { mutableStateOf<DailyForecast?>(null) }
    val sheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember { mutableStateOf(false) }

    val locationPermissionState = rememberPermissionState(android.Manifest.permission.ACCESS_FINE_LOCATION)
    val lifecycleOwner = androidx.compose.ui.platform.LocalLifecycleOwner.current
    
    // Bottom Sheet para detalle del día
    if (showBottomSheet && selectedDailyForecast != null) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet = false },
            sheetState = sheetState
        ) {
            DailyDetailContent(daily = selectedDailyForecast!!)
        }
    }

    DisposableEffect(lifecycleOwner) {
        val observer = androidx.lifecycle.LifecycleEventObserver { _, event ->
            if (event == androidx.lifecycle.Lifecycle.Event.ON_RESUME) {
                if (locationPermissionState.status.isGranted) {
                    viewModel.checkRefreshOnResume()
                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    // Solo pedimos permiso si no está concedido, pero NO llamamos a fetchLocationAndWeather
    // El ViewModel ya maneja la lógica de geolocalización solo cuando es "current_location"
    LaunchedEffect(Unit) {
        if (!locationPermissionState.status.isGranted) {
            locationPermissionState.launchPermissionRequest()
        }
    }

    if (showApiKeyDialog) {
        ApiKeyDialog(
            onApiKeyEntered = { viewModel.saveApiKey(it) },
            onDismiss = { viewModel.closeApiKeyDialog() }
        )
    }

    Scaffold(
        topBar = {
            if (uiState is WeatherUiState.Success) {
                TopAppBar(
                    title = { Text((uiState as WeatherUiState.Success).weather.nombreMunicipio) },
                    actions = {
                        IconButton(onClick = { viewModel.refreshData() }) {
                            Icon(Icons.Default.Refresh, contentDescription = "Actualizar")
                        }
                        IconButton(onClick = { viewModel.toggleFavorite() }) {
                            Icon(
                                imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = "Favorito",
                                tint = if (isFavorite) Color.Red else MaterialTheme.colorScheme.onSurface
                            )
                        }
                        IconButton(onClick = { viewModel.openApiKeyDialog() }) {
                            Icon(Icons.Default.Settings, contentDescription = "Configurar API Key")
                        }
                    }
                )
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when (val state = uiState) {
                is WeatherUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is WeatherUiState.Error -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Error: ${state.message}",
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(16.dp)
                        )
                        Button(onClick = { viewModel.fetchLocationAndWeather() }) {
                            Text("Reintentar")
                        }
                    }
                }
                is WeatherUiState.Success -> {
                    WeatherContent(
                        weather = state.weather,
                        onDailyClick = { daily ->
                            selectedDailyForecast = daily
                            showBottomSheet = true
                        }
                    )
                }
            }
        }
    }
}

// ... (ApiKeyDialog remains same) ...

@Composable
fun ApiKeyDialog(
    onApiKeyEntered: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var text by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Configurar AEMET API Key") },
        text = {
            Column {
                Text("Introduce tu API Key de AEMET OpenData:")
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = text,
                    onValueChange = { text = it },
                    label = { Text("API Key") },
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { 
                    if (text.isNotBlank()) onApiKeyEntered(text) 
                }
            ) {
                Text("Guardar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}


@Composable
fun WeatherContent(weather: Weather, onDailyClick: (DailyForecast) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Cabecera Principal con tarjeta
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
            ),
            shape = RoundedCornerShape(24.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = weather.provincia,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                
                // Icono y Temperatura Actual
                Icon(
                    imageVector = getWeatherIcon(weather.descripcionCielo),
                    contentDescription = null,
                    modifier = Modifier.size(80.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "${weather.temperaturaActual}º",
                    style = MaterialTheme.typography.displayLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = weather.descripcionCielo,
                    style = MaterialTheme.typography.titleLarge
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Detalles rápidos en fila
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    WeatherDetailItem(Icons.Default.WaterDrop, "Lluvia", "${weather.probabilidadLluvia}%")
                    WeatherDetailItem(Icons.Default.Air, "Viento", "${weather.vientoVelocidad} km/h")
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Predicción por Horas
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Por horas",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(12.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(weather.hourlyForecast) { item ->
                        HourlyForecastItem(item)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Predicción por Días
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Próximos días",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                weather.dailyForecast.forEach { item ->
                    DailyForecastItem(item, onClick = { onDailyClick(item) })
                }
            }
        }
        
        // Nota sobre días disponibles
        if (weather.dailyForecast.size <= 3) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "* AEMET proporciona ${weather.dailyForecast.size} días con predicción horaria",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
            )
        }
        
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun HourlyForecastItem(item: HourlyForecast) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = item.hour, style = MaterialTheme.typography.labelMedium)
            Spacer(modifier = Modifier.height(4.dp))
            Icon(
                imageVector = getWeatherIcon(item.description),
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "${item.temperature}º", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            if (item.rainProbability > 0) {
                 Text(text = "${item.rainProbability}%", style = MaterialTheme.typography.labelSmall, color = Color.Blue)
                 if (item.precipitation > 0) {
                     Text(text = "${item.precipitation}mm", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                 }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DailyForecastItem(item: DailyForecast, onClick: () -> Unit) {
    // Formatear fecha: si viene como YYYY-MM-DD, convertir a "LUN\n18"
    val (dayName, dayNumber) = remember(item.date) {
        formatDateForDisplayCompact(item.date)
    }
    
    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp, horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
        // Día (columna fija)
        Column(
            modifier = Modifier.width(40.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = dayName,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = dayNumber,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        
        Spacer(modifier = Modifier.width(8.dp))
        
        // Icono
        Icon(
            imageVector = getWeatherIcon(item.description),
            contentDescription = null,
            modifier = Modifier.size(28.dp),
            tint = MaterialTheme.colorScheme.onSurface
        )
        
        Spacer(modifier = Modifier.width(12.dp))
        
        // Temperaturas min/max
        Text(
            text = "${item.minTemp}º",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.width(32.dp),
            textAlign = androidx.compose.ui.text.style.TextAlign.End
        )
        
        Text(
            text = "${item.maxTemp}º",
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.width(36.dp),
            textAlign = androidx.compose.ui.text.style.TextAlign.End
        )
        
        Spacer(modifier = Modifier.weight(1f))
        
        // Lluvia
        val rainColor = if (item.rainProbability > 0) Color(0xFF42A5F5) else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.width(50.dp),
            horizontalArrangement = Arrangement.End
        ) {
            Icon(
                Icons.Default.WaterDrop, 
                contentDescription = null, 
                modifier = Modifier.size(12.dp), 
                tint = rainColor
            )
            Text(
                text = "${item.rainProbability}%", 
                style = MaterialTheme.typography.labelSmall, 
                color = rainColor,
                maxLines = 1
            )
        }
    }
    }
    Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
}


@Composable
fun WeatherDetailItem(icon: ImageVector, label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(imageVector = icon, contentDescription = null)
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = label, style = MaterialTheme.typography.labelMedium)
        Text(text = value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun DailyDetailContent(daily: DailyForecast) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Título del día
        Text(
            text = daily.dateFormatted.ifEmpty { daily.date },
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = daily.description,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // --- Grid de Detalles ---
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Fila 1: Temp y Lluvia
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    WeatherDetailItem(Icons.Default.DeviceThermostat, "Temp", "${daily.minTemp}º / ${daily.maxTemp}º")
                    WeatherDetailItem(Icons.Default.WaterDrop, "Lluvia", "${daily.rainProbability}%")
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f))
                Spacer(modifier = Modifier.height(16.dp))

                // Fila 2: Sensación y Humedad
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    WeatherDetailItem(Icons.Default.DeviceThermostat, "Sensación", "${daily.minFeelTemp}º / ${daily.maxFeelTemp}º")
                    WeatherDetailItem(Icons.Default.WaterDrop, "Humedad", "${daily.minHumidity}% - ${daily.maxHumidity}%")
                }

                // Fila 3 (Opcional): Viento y UV
                if (daily.windSpeed > 0 || daily.uvIndex > 0) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f))
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        if (daily.windSpeed > 0) {
                            WeatherDetailItem(Icons.Default.Air, "Viento", "${daily.windSpeed} km/h ${daily.windDirection}")
                        } else {
                            Spacer(modifier = Modifier.width(1.dp)) // Placeholder para alineación
                        }
                        
                        if (daily.uvIndex > 0) {
                            WeatherDetailItem(Icons.Default.WbSunny, "Índice UV", "${daily.uvIndex}")
                        }
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Text(
            text = "Predicción por horas",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.Start)
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        if (daily.hourlyDetails.isNotEmpty()) {
            daily.hourlyDetails.forEach { hourly ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = hourly.hour,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.width(60.dp)
                    )
                    
                    Icon(
                        imageVector = getWeatherIcon(hourly.description),
                        contentDescription = null,
                        modifier = Modifier.size(24.dp),
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                    
                    Spacer(modifier = Modifier.width(16.dp))
                    
                    Text(
                        text = "${hourly.temperature}º",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Spacer(modifier = Modifier.weight(1f))
                    
                    // Columna de precipitaciones y lluvia
                    Column(horizontalAlignment = Alignment.End) {
                        if (hourly.rainProbability > 0) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.WaterDrop, null, modifier = Modifier.size(12.dp), tint = Color(0xFF42A5F5))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("${hourly.rainProbability}%", style = MaterialTheme.typography.bodySmall, color = Color(0xFF42A5F5))
                            }
                        }
                        if (hourly.precipitation > 0.0) {
                            Text("${hourly.precipitation} mm", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
                Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f))
            }
        } else {
             Text(
                text = "No hay datos horarios detallados disponibles para este día más allá de los generales",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(vertical = 16.dp),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
        
        Spacer(modifier = Modifier.height(32.dp))
    }
}

fun getWeatherIcon(descripcion: String): ImageVector {
    return when {
        descripcion.contains("Despejado", ignoreCase = true) -> Icons.Default.WbSunny
        descripcion.contains("Lluvia", ignoreCase = true) -> Icons.Default.WaterDrop
        descripcion.contains("Cubierto", ignoreCase = true) -> Icons.Default.Cloud
        descripcion.contains("Nuboso", ignoreCase = true) -> Icons.Default.Cloud
        else -> Icons.Default.Cloud // Fallback
    }
}

/**
 * Formatea una fecha a un formato legible "Lun 18"
 * Soporta: YYYY-MM-DD, YYYY-MM-DDTHH:MM:SS
 */
private fun formatDateForDisplay(dateStr: String): String {
    // Si no contiene guiones, ya está formateada
    if (!dateStr.contains("-")) return dateStr
    
    return try {
        // Extraer solo la parte de fecha (antes de la T si existe)
        val datePart = if (dateStr.contains("T")) {
            dateStr.substringBefore("T")
        } else {
            dateStr
        }
        
        val parts = datePart.split("-")
        if (parts.size != 3) return dateStr
        
        val year = parts[0].toInt()
        val month = parts[1].toInt() - 1 // Calendar months are 0-indexed
        val day = parts[2].toInt()
        
        val calendar = java.util.Calendar.getInstance().apply {
            set(year, month, day)
        }
        
        val dayOfWeek = when (calendar.get(java.util.Calendar.DAY_OF_WEEK)) {
            java.util.Calendar.MONDAY -> "Lun"
            java.util.Calendar.TUESDAY -> "Mar"
            java.util.Calendar.WEDNESDAY -> "Mié"
            java.util.Calendar.THURSDAY -> "Jue"
            java.util.Calendar.FRIDAY -> "Vie"
            java.util.Calendar.SATURDAY -> "Sáb"
            java.util.Calendar.SUNDAY -> "Dom"
            else -> ""
        }
        
        "$dayOfWeek $day"
    } catch (e: Exception) {
        dateStr
    }
}

/**
 * Formatea una fecha a un par (día de semana, número)
 * Ejemplo: "2025-12-18" -> ("JUE", "18")
 */
private fun formatDateForDisplayCompact(dateStr: String): Pair<String, String> {
    if (!dateStr.contains("-")) return Pair(dateStr, "")
    
    return try {
        val datePart = if (dateStr.contains("T")) {
            dateStr.substringBefore("T")
        } else {
            dateStr
        }
        
        val parts = datePart.split("-")
        if (parts.size != 3) return Pair(dateStr, "")
        
        val year = parts[0].toInt()
        val month = parts[1].toInt() - 1
        val day = parts[2].toInt()
        
        val calendar = java.util.Calendar.getInstance().apply {
            set(year, month, day)
        }
        
        val dayOfWeek = when (calendar.get(java.util.Calendar.DAY_OF_WEEK)) {
            java.util.Calendar.MONDAY -> "LUN"
            java.util.Calendar.TUESDAY -> "MAR"
            java.util.Calendar.WEDNESDAY -> "MIÉ"
            java.util.Calendar.THURSDAY -> "JUE"
            java.util.Calendar.FRIDAY -> "VIE"
            java.util.Calendar.SATURDAY -> "SÁB"
            java.util.Calendar.SUNDAY -> "DOM"
            else -> ""
        }
        
        Pair(dayOfWeek, day.toString())
    } catch (e: Exception) {
        Pair(dateStr, "")
    }
}
