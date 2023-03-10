package com.franklinharper.jpmc.nycschools.di

import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.franklinharper.jpmc.nycschools.Database
import com.franklinharper.jpmc.nycschools.data.restapi.ApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
// In this "toy" app we can instantiate all of the dependencies for the entire
// App lifecycle. But in a large scale app we would want to use finer grained scoping.
@InstallIn(SingletonComponent::class)
object DataModule {

    @Provides
    @Singleton
    fun provideNycOpenDataService(): ApiService =
        Retrofit
            .Builder()
            .baseUrl("https://data.cityofnewyork.us/resource/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): Database {
        val driver: SqlDriver = AndroidSqliteDriver(Database.Schema, context, name = "nyc-schools.db")
        return Database(driver)
    }

}
