package com.samcod3.pruebaaaa.di

import com.samcod3.pruebaaaa.data.remote.AemetApi
import com.samcod3.pruebaaaa.data.repository.ApiKeyRepository
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private const val BASE_URL = "https://opendata.aemet.es/opendata/"

    @Provides
    @Singleton
    fun provideMoshi(): Moshi {
        return Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(apiKeyRepository: ApiKeyRepository): OkHttpClient {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val authInterceptor = Interceptor { chain ->
            val apiKey = apiKeyRepository.getApiKey()
            
            val request = chain.request().newBuilder()
                .addHeader("api_key", apiKey)
                .addHeader("Accept", "application/json")
                .build()
            
            val response = chain.proceed(request)
            
            // Fix para AEMET content-type text/plain y codificación
            val contentType = response.header("Content-Type")
            if (contentType != null && (contentType.contains("text/plain") || contentType.contains("ISO-8859-15"))) {
                // 1. Leemos el contenido respetando el charset original (OkHttp lo hace automático con body.string())
                val contentString = response.body?.string() ?: ""
                
                // 2. Creamos un nuevo body explícitamente en UTF-8 y tipo JSON
                val newBody = ResponseBody.create(
                    "application/json; charset=utf-8".toMediaTypeOrNull(),
                    contentString
                )
                
                return@Interceptor response.newBuilder()
                    .body(newBody)
                    .header("Content-Type", "application/json; charset=utf-8")
                    .build()
            }
            
            response
        }

        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(logging)
            .build()
    }

    @Provides
    @Singleton
    fun provideAemetApi(moshi: Moshi, okHttpClient: OkHttpClient): AemetApi {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .client(okHttpClient)
            .build()
            .create(AemetApi::class.java)
    }
}