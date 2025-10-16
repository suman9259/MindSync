package com.example.mindsync.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mindsync.data.local.UserPreferences
import com.example.mindsync.presentation.base.BaseViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val userPreferences: UserPreferences
) : BaseViewModel<AuthState,AuthIntent,AuthEffect>() {
    private val auth = Firebase.auth
    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState

    init {
        viewModelScope.launch {
            userPreferences.isLoggedIn.collect { isLoggedIn ->
                if (isLoggedIn) {
                    val currentUser = auth.currentUser
                    if (currentUser != null) {
                        _authState.value = AuthState.Success(currentUser)
                    } else {
                        _authState.value = AuthState.Success(null)
                    }
                } else {
                    _authState.value = AuthState.Success(null)
                }
            }
        }
    }

    fun signInWithEmail(email: String, password: String) = viewModelScope.launch {
        signInWithEmailAndPassword(email, password)
    }

    private suspend fun signInWithEmailAndPassword(email: String, password: String) {
        try {
            _authState.value = AuthState.Loading
            val result = auth.signInWithEmailAndPassword(email, password).await()
            result.user?.let { user ->
                userPreferences.saveUserEmail(user.email ?: "")
                userPreferences.setLoggedIn(true)
                _authState.value = AuthState.Success(user)
            }
        } catch (e: Exception) {
            _authState.value = AuthState.Error(e.message ?: "Authentication failed")
        }
    }

    fun signUpWithEmail(email: String, password: String) = viewModelScope.launch {
        try {
            _authState.value = AuthState.Loading
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            _authState.value = AuthState.Success(result.user)
        } catch (e: Exception) {
            _authState.value = AuthState.Error(e.message ?: "Registration failed")
        }
    }


    private suspend fun signOut() {
        try {
            auth.signOut()
            userPreferences.clear()
            _authState.value = AuthState.Success(null)
        } catch (e: Exception) {
            _authState.value = AuthState.Error(e.message ?: "Sign out failed")
        }
    }

    private fun resetPassword(email: String) = viewModelScope.launch {
        try {
            _authState.value = AuthState.Loading
            auth.sendPasswordResetEmail(email).await()
            _authState.value = AuthState.Success(null)
        } catch (e: Exception) {
            _authState.value = AuthState.Error(e.message ?: "Google sign in failed")
        }
    }

    fun handleGoogleSignInResult(idToken: String) = viewModelScope.launch {
        try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val result = auth.signInWithCredential(credential).await()
            _authState.value = AuthState.Success(result.user)
        } catch (e: Exception) {
            _authState.value = AuthState.Error(e.message ?: "Google sign in failed")
        }
    }

    override fun processIntent(intent: AuthIntent) {
    }
}