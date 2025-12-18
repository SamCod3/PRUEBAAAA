package com.samcod3.pruebaaaa.data.local

import androidx.room.TypeConverter
import com.samcod3.pruebaaaa.domain.model.DailyForecast
import com.samcod3.pruebaaaa.domain.model.HourlyForecast
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

class Converters {
    private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

    @TypeConverter
    fun fromHourlyList(value: List<HourlyForecast>?): String {
        if (value == null) return "[]"
        val type = Types.newParameterizedType(List::class.java, HourlyForecast::class.java)
        return moshi.adapter<List<HourlyForecast>>(type).toJson(value)
    }

    @TypeConverter
    fun toHourlyList(value: String?): List<HourlyForecast> {
        if (value.isNullOrEmpty()) return emptyList()
        val type = Types.newParameterizedType(List::class.java, HourlyForecast::class.java)
        return moshi.adapter<List<HourlyForecast>>(type).fromJson(value) ?: emptyList()
    }

    @TypeConverter
    fun fromDailyList(value: List<DailyForecast>?): String {
        if (value == null) return "[]"
        val type = Types.newParameterizedType(List::class.java, DailyForecast::class.java)
        return moshi.adapter<List<DailyForecast>>(type).toJson(value)
    }

    @TypeConverter
    fun toDailyList(value: String?): List<DailyForecast> {
        if (value.isNullOrEmpty()) return emptyList()
        val type = Types.newParameterizedType(List::class.java, DailyForecast::class.java)
        return moshi.adapter<List<DailyForecast>>(type).fromJson(value) ?: emptyList()
    }
}
