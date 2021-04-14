package com.bytepoets.sample.androidtesting.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ConfigurationModule {
    @Singleton
    @Provides
    fun provideApiConfiguration(): ApiConfiguration =
        ApiConfiguration("https://www.json-generator.com/")
}