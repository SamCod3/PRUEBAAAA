package com.samcod3.pruebaaaa.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.samcod3.pruebaaaa.domain.model.City
import com.samcod3.pruebaaaa.ui.viewmodel.SearchViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    onNavigateBack: () -> Unit,
    onNavigateToDetail: (String) -> Unit,
    viewModel: SearchViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            SearchBar(
                query = uiState.query,
                onQueryChange = viewModel::onQueryChange,
                onNavigateBack = onNavigateBack
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            if (uiState.isLoading) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }

            LazyColumn {
                items(uiState.results) { city ->
                    CityItem(
                        city = city,
                        onClick = { onNavigateToDetail(city.id) },
                        onFavoriteClick = { viewModel.toggleFavorite(city) }
                    )
                }
            }
            
            if (uiState.results.isEmpty() && uiState.query.length >= 3 && !uiState.isLoading) {
                Text(
                    text = "No se encontraron resultados",
                    modifier = Modifier.align(Alignment.Center),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onNavigateBack: () -> Unit
) {
    TopAppBar(
        title = {
            TextField(
                value = query,
                onValueChange = onQueryChange,
                placeholder = { Text("Buscar ciudad...") },
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                modifier = Modifier.fillMaxWidth()
            )
        },
        navigationIcon = {
            IconButton(onClick = onNavigateBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Atrás")
            }
        },
        actions = {
            if (query.isNotEmpty()) {
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(Icons.Default.Close, contentDescription = "Borrar")
                }
            } else {
                Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.padding(end = 16.dp))
            }
        }
    )
}

@Composable
fun CityItem(
    city: City,
    onClick: () -> Unit,
    onFavoriteClick: () -> Unit
) {
    ListItem(
        headlineContent = { Text(city.nombre) },
        supportingContent = { Text(city.provincia) },
        trailingContent = {
            IconButton(onClick = onFavoriteClick) {
                Icon(
                    imageVector = if (city.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = if (city.isFavorite) "Quitar de favoritos" else "Añadir a favoritos",
                    tint = if (city.isFavorite) Color.Red else MaterialTheme.colorScheme.onSurface
                )
            }
        },
        modifier = Modifier.clickable { onClick() }
    )
    Divider()
}
