package com.example.playlistmaker.ui.alltracks

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.playlistmaker.PlaylistMakerApp
import com.example.playlistmaker.domain.interactor.TracksInteractor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.IOException

class AllTracksViewModel(
    private val tracksInteractor: TracksInteractor
) : ViewModel() {

    private val _allTracksScreenState = MutableStateFlow<AllTracksState>(AllTracksState.Initial)
    val allTracksScreenState = _allTracksScreenState.asStateFlow()

    fun fetchData() {
        if (_allTracksScreenState.value is AllTracksState.Success) return

        viewModelScope.launch(Dispatchers.IO) {
            try {
                _allTracksScreenState.update { AllTracksState.Loading }
                val list = tracksInteractor.getAllTracks()
                _allTracksScreenState.update { AllTracksState.Success(foundList = list) }
            } catch (e: IOException) {
                Log.e("AllTracksViewModel", "Failed to load tracks", e)
                _allTracksScreenState.update { AllTracksState.Error(e.message.orEmpty()) }
            }
        }
    }

    companion object {
        fun getViewModelFactory(): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val interactor = (this[APPLICATION_KEY] as PlaylistMakerApp).provideTracksInteractor()
                AllTracksViewModel(tracksInteractor = interactor)
            }
        }
    }
}
