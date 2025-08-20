package com.example.universal_android_app.data.remote

import com.example.universal_android_app.data.RecipeListResponse
import retrofit2.http.GET

interface RecipeApiService {
    @GET("fakepath/get")
    suspend fun getRecipes(): RecipeListResponse
}
