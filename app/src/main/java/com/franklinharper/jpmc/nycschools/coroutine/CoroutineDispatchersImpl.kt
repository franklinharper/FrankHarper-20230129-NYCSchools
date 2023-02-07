@file:Suppress("unused")

package com.franklinharper.jpmc.nycschools.coroutine

import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

class CoroutineDispatchersImpl @Inject constructor(): CoroutineDispatchers {
    override val io = Dispatchers.IO
}
