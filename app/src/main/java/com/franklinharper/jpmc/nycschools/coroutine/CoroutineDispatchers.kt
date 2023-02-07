@file:Suppress("unused")

package com.franklinharper.jpmc.nycschools.coroutine

import kotlinx.coroutines.CoroutineDispatcher

interface CoroutineDispatchers {
    val io: CoroutineDispatcher
    // Add more Dispatchers when needed.
}
