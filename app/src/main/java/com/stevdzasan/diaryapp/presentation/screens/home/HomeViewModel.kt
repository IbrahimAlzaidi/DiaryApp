package com.stevdzasan.diaryapp.presentation.screens.home

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stevdzasan.diaryapp.data.repository.Diaries
import com.stevdzasan.diaryapp.data.repository.MongoDB
import com.stevdzasan.diaryapp.util.RequestState
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {

    var diaries: MutableState<Diaries> = mutableStateOf(RequestState.Idle)

    init {
        observeAllDiaries()
    }

    private fun observeAllDiaries() {
        viewModelScope.launch {
            MongoDB.getAllDiaries().collect { result ->
                diaries.value = result
                Log.d("ViewModel", "observeAllDiaries: $result")
            }
        }
    }

}