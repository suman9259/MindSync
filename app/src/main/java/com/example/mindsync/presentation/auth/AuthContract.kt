package com.example.mindsync.presentation.auth

import com.example.mindsync.presentation.base.MviEffect
import com.example.mindsync.presentation.base.MviIntent
import com.example.mindsync.presentation.base.MviState
import com.google.firebase.auth.FirebaseUser

// State
data class AuthUiState(
    val isLoading: Boolean = false,
    val phoneNumber: String = "",
    val countryCode: String = "+91",
    val verificationId: String? = null,
    val otpCode: String = "",
    val user: FirebaseUser? = null,
    val error: String? = null,
    val isOtpSent: Boolean = false
) : MviState

// Intent
sealed class AuthIntent : MviIntent {
    data class UpdatePhoneNumber(val phoneNumber: String) : AuthIntent()
    data class UpdateCountryCode(val countryCode: String) : AuthIntent()
    data class UpdateOtpCode(val otpCode: String) : AuthIntent()
    data object SendOtp : AuthIntent()
    data class VerifyOtp(val otp: String) : AuthIntent()
    data class SignInWithGoogle(val idToken: String) : AuthIntent()
    data object SignOut : AuthIntent()
    data object ClearError : AuthIntent()
}

// Effect
sealed class AuthEffect : MviEffect {
    data class ShowError(val message: String) : AuthEffect()
    data object NavigateToDashboard : AuthEffect()
    data object NavigateToOtp : AuthEffect()
    data object NavigateToLogin : AuthEffect()
}

// Keep legacy AuthState for backward compatibility during migration
sealed class AuthState {
    data object Idle : AuthState()
    data object Loading : AuthState()
    data class Success(val user: FirebaseUser?) : AuthState()
    data class Error(val message: String) : AuthState()
}
