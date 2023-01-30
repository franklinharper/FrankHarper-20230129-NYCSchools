package com.franklinharper.jpmc.nycschools.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.franklinharper.jpmc.nycschools.NycOpenDataRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: NycOpenDataRepository
) : ViewModel() {

    fun loadData() {
        CoroutineScope(Dispatchers.IO).launch {
            runCatching {
                repository.loadSchoolsWithSatScores(viewModelScope)
            }.onFailure { throwable ->
                Timber.d(throwable, "FAIL: couldn't get the list of schools")
            }.onSuccess { schoolsWithSatScores ->
//                schoolsWithSatScores?.forEach {school ->
//                    Timber.d("schoolsWithSatScores: $school")
//                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
    }
}