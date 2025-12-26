package com.example.fitdiary.ui.viewmodel

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModel
import com.example.fitdiary.data.local.SessionStore
import com.example.fitdiary.data.local.UserSession
import kotlinx.coroutines.flow.Flow

class MainViewModel(
    sessionStore: SessionStore,
) : ViewModel() {
    val sessionFlow: Flow<UserSession?> = sessionStore.sessionFlow
}

class MainViewModelFactory(
    private val sessionStore: SessionStore,
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel(sessionStore) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
