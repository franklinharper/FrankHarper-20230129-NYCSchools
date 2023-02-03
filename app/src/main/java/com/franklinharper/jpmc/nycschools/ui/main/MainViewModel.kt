package com.franklinharper.jpmc.nycschools.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.franklinharper.jpmc.nycschools.HighSchoolWithSatScores
import com.franklinharper.jpmc.nycschools.NycOpenDataRepository
import com.laimiux.lce.UCE
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


// The purpose of the "plumbing" code below is only to make the "application level" code
// easier to read. In a real app the plumbing code would NOT be repeated in each ViewModel.

// The UCE type used below represents the following states.
//   * Loading,
//   * Content,
//   * Error
//
//   In this case:
//   * the loading type is Unit (no extra data is required for the loading state).
//   * the content type is HighSchoolWithSatScores,
//   * the error type is TypeOfError.
//
// This generic type isn't as easy to read as I would like. And having to add explanatory
// comments like the one above can be a "code smell".
//
// In a real app I'd implement a "loading content error" type within the app.
// But to save time I'll just pull in a library and clean up the application
// level code using a typealias.
typealias HighSchoolWithSatScoresResult = UCE<List<HighSchoolWithSatScores>, TypeOfError>

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: NycOpenDataRepository
) : ViewModel() {

    // More hacky "plumbing" code.
    private val loadingState = UCE.loading()

    // Hack: I'm NOT even trying to figure out what the real cause of the error is.
    private val errorState = UCE.error(TypeOfError.OTHER)

    private fun contents(schoolsWithSatScores: List<HighSchoolWithSatScores>) =
        UCE.content(schoolsWithSatScores)

    //
    // This is the beginning of the "application level" code.
    //
    private val mutableSchoolResults = MutableLiveData<HighSchoolWithSatScoresResult>()
    val schoolResults: LiveData<HighSchoolWithSatScoresResult>
        get() = mutableSchoolResults

    fun loadData() {
        mutableSchoolResults.value = loadingState
        viewModelScope.launch {
            runCatching {
                repository.loadSchoolsWithSatScores(viewModelScope)
            }.onFailure {
                mutableSchoolResults.postValue(errorState)
            }.onSuccess { schoolsWithSatScores ->
                mutableSchoolResults.postValue(contents(schoolsWithSatScores))
            }
        }
    }
}