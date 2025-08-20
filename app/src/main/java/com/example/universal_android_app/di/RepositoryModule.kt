package com.example.universal_android_app.di

import com.example.universal_android_app.algorithm.RecipeAlgorithm
import com.example.universal_android_app.algorithm.RecipeAlgorithmImpl
import com.example.universal_android_app.data.RecipeRepositoryImpl
import com.example.universal_android_app.domain.RecipeRepository
import dagger.Binds
import dagger.Module
import dagger.Reusable
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindRecipeRepository(impl: RecipeRepositoryImpl): RecipeRepository

    @Binds
    @Reusable
    abstract fun bindRecipeAlgorithm(impl: RecipeAlgorithmImpl): RecipeAlgorithm
}
