package com.samcod3.pruebaaaa.domain.model

data class City(
    val id: String,
    val nombre: String,
    val provincia: String,
    val isFavorite: Boolean = false
)
