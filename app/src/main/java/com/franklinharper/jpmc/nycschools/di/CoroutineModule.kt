package com.franklinharper.jpmc.nycschools.di

import com.franklinharper.jpmc.nycschools.coroutine.CoroutineDispatchers
import com.franklinharper.jpmc.nycschools.coroutine.CoroutineDispatchersImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class CoroutineModule {

    @Binds
    @Singleton
    abstract fun bindCoroutineDispatchers(
        coroutineDispatchersImpl: CoroutineDispatchersImpl,
    ): CoroutineDispatchers

}
