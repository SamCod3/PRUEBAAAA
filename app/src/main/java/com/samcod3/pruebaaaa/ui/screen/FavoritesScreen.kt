package com.samcod3.pruebaaaa.ui.screen

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.samcod3.pruebaaaa.domain.model.City
import com.samcod3.pruebaaaa.ui.theme.*
import com.samcod3.pruebaaaa.ui.viewmodel.FavoritesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(
    onNavigateToSearch: () -> Unit,
    onNavigateToDetail: (String) -> Unit,
    onNavigateToCurrentLocation: () -> Unit,
    viewModel: FavoritesViewModel = hiltViewModel()
) {
    val favorites by viewModel.favorites.collectAsState()

    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = { 
                    Text(
                        "Mis Ciudades",
                        fontWeight = FontWeight.Bold
                    ) 
                },
                actions = {
                    FilledIconButton(
                        onClick = onNavigateToCurrentLocation,
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    ) {
                        Icon(
                            Icons.Default.LocationOn, 
                            contentDescription = "Ubicación Actual",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                colors = TopAppBarDefaults.largeTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onNavigateToSearch,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text("Añadir Ciudad") }
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        if (favorites.isEmpty()) {
            EmptyFavoritesContent(
                modifier = Modifier.padding(padding),
                onNavigateToSearch = onNavigateToSearch
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                itemsIndexed(favorites) { index, city ->
                    PremiumCityCard(
                        city = city,
                        index = index,
                        onClick = { onNavigateToDetail(city.id) }
                    )
                }
                // Espaciado para el FAB
                item { Spacer(modifier = Modifier.height(80.dp)) }
            }
        }
    }
}

@Composable
private fun EmptyFavoritesContent(
    modifier: Modifier = Modifier,
    onNavigateToSearch: () -> Unit
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Icono decorativo
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(RoundedCornerShape(32.dp))
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primaryContainer,
                                MaterialTheme.colorScheme.secondaryContainer
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.WbSunny,
                    contentDescription = null,
                    modifier = Modifier.size(56.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            
            Text(
                "No tienes ciudades favoritas",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Text(
                "Añade ciudades para ver el tiempo rápidamente",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Button(
                onClick = onNavigateToSearch,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                contentPadding = PaddingValues(horizontal = 32.dp, vertical = 16.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Buscar Ciudad", fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
fun PremiumCityCard(
    city: City,
    index: Int,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "scale"
    )
    
    // Gradientes diferentes para cada tarjeta
    val gradientColors = when (index % 4) {
        0 -> listOf(SkyBlue, SkyBlueDark)
        1 -> listOf(SunnyGradientStart, SunnyGradientEnd)
        2 -> listOf(AccentGreen, Color(0xFF059669))
        else -> listOf(AccentOrange, Color(0xFFEA580C))
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(20.dp),
                ambientColor = gradientColors[0].copy(alpha = 0.3f),
                spotColor = gradientColors[0].copy(alpha = 0.3f)
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.horizontalGradient(gradientColors)
                )
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = city.nombre,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = city.provincia,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }
                
                // Icono decorativo de clima
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (index % 2 == 0) Icons.Default.WbSunny else Icons.Default.Cloud,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        }
    }
}
