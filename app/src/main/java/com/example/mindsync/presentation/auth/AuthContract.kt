package com.example.mindsync.presentation.auth

import com.google.firebase.auth.FirebaseUser

// State
sealed class AuthState : MviState {
    data object Idle : AuthState()
    data object Loading : AuthState()
    data class Success(val user: FirebaseUser?) : AuthState()
    data class Error(val message: String) : AuthState()
}

// Intent
sealed class AuthIntent : MviIntent {
    data class SignInWithEmail(val email: String, val password: String) : AuthIntent()
    data class SignUpWithEmail(val email: String, val password: String) : AuthIntent()
    data class SignInWithGoogle(val idToken: String) : AuthIntent()
    data object SignOut : AuthIntent()
    data class ResetPassword(val email: String) : AuthIntent()
}

// Effect
sealed class AuthEffect : MviEffect {
    data class ShowError(val message: String) : AuthEffect()
    data object NavigateToDashboard : AuthEffect()
    data object NavigateToLogin : AuthEffect()
    data object ShowPasswordResetSent : AuthEffect()
}
