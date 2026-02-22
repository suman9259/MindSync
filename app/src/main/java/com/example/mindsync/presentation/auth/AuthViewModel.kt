package com.example.mindsync.presentation.auth

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mindsync.data.local.UserPreferences
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.concurrent.TimeUnit

class AuthViewModel(
    private val userPreferences: UserPreferences
) : ViewModel() {
    
    private val auth = FirebaseAuth.getInstance()
    
    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()
    
    private val _effect = Channel<AuthEffect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()
    
    private var storedVerificationId: String? = null
    private var resendToken: PhoneAuthProvider.ForceResendingToken? = null
    
    fun processIntent(intent: AuthIntent) {
        when (intent) {
            is AuthIntent.UpdatePhoneNumber -> {
                _uiState.update { it.copy(phoneNumber = intent.phoneNumber, error = null) }
            }
            is AuthIntent.UpdateCountryCode -> {
                _uiState.update { it.copy(countryCode = intent.countryCode) }
            }
            is AuthIntent.UpdateOtpCode -> {
                _uiState.update { it.copy(otpCode = intent.otpCode, error = null) }
            }
            is AuthIntent.SendOtp -> {
                // This needs Activity context, handled in UI
            }
            is AuthIntent.VerifyOtp -> verifyOtp(intent.otp)
            is AuthIntent.SignInWithGoogle -> signInWithGoogle(intent.idToken)
            is AuthIntent.SignOut -> signOut()
            is AuthIntent.ClearError -> {
                _uiState.update { it.copy(error = null) }
            }
        }
    }
    
    fun sendOtp(activity: Activity) {
        val phoneNumber = "${_uiState.value.countryCode}${_uiState.value.phoneNumber}"
        
        if (_uiState.value.phoneNumber.length < 10) {
            _uiState.update { it.copy(error = "Please enter a valid phone number") }
            return
        }
        
        _uiState.update { it.copy(isLoading = true, error = null) }
        
        val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                signInWithPhoneCredential(credential)
            }
            
            override fun onVerificationFailed(e: FirebaseException) {
                _uiState.update { 
                    it.copy(
                        isLoading = false, 
                        error = e.message ?: "Verification failed"
                    ) 
                }
            }
            
            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                storedVerificationId = verificationId
                resendToken = token
                _uiState.update { 
                    it.copy(
                        isLoading = false, 
                        verificationId = verificationId,
                        isOtpSent = true
                    ) 
                }
                viewModelScope.launch {
                    _effect.send(AuthEffect.NavigateToOtp)
                }
            }
        }
        
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(activity)
            .setCallbacks(callbacks)
            .build()
        
        PhoneAuthProvider.verifyPhoneNumber(options)
    }
    
    private fun verifyOtp(otp: String) {
        val verificationId = storedVerificationId
        if (verificationId == null) {
            _uiState.update { it.copy(error = "Verification ID not found. Please try again.") }
            return
        }
        
        if (otp.length != 6) {
            _uiState.update { it.copy(error = "Please enter a valid 6-digit OTP") }
            return
        }
        
        _uiState.update { it.copy(isLoading = true, error = null) }
        
        val credential = PhoneAuthProvider.getCredential(verificationId, otp)
        signInWithPhoneCredential(credential)
    }
    
    private fun signInWithPhoneCredential(credential: PhoneAuthCredential) {
        viewModelScope.launch {
            try {
                val result = auth.signInWithCredential(credential).await()
                result.user?.let { user ->
                    userPreferences.saveUserEmail(user.phoneNumber ?: "")
                    userPreferences.setLoggedIn(true)
                    _uiState.update { it.copy(isLoading = false, user = user) }
                    _effect.send(AuthEffect.NavigateToDashboard)
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isLoading = false, 
                        error = e.message ?: "Invalid OTP. Please try again."
                    ) 
                }
            }
        }
    }
    
    private fun signInWithGoogle(idToken: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val credential = GoogleAuthProvider.getCredential(idToken, null)
                val result = auth.signInWithCredential(credential).await()
                result.user?.let { user ->
                    userPreferences.saveUserEmail(user.email ?: "")
                    userPreferences.setLoggedIn(true)
                    _uiState.update { it.copy(isLoading = false, user = user) }
                    _effect.send(AuthEffect.NavigateToDashboard)
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isLoading = false, 
                        error = e.message ?: "Google sign in failed"
                    ) 
                }
            }
        }
    }
    
    private fun signOut() {
        viewModelScope.launch {
            try {
                auth.signOut()
                userPreferences.clear()
                _uiState.update { AuthUiState() }
                _effect.send(AuthEffect.NavigateToLogin)
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message ?: "Sign out failed") }
            }
        }
    }
}