package com.example.mindsync.data.local.security

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class SecurePreferences(context: Context) {

    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val encryptedPrefs: SharedPreferences = EncryptedSharedPreferences.create(
        context,
        PREFS_NAME,
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    private val _userEmail = MutableStateFlow(getString(KEY_USER_EMAIL))
    val userEmail: Flow<String?> = _userEmail.asStateFlow()

    private val _isLoggedIn = MutableStateFlow(getBoolean(KEY_IS_LOGGED_IN, false))
    val isLoggedIn: Flow<Boolean> = _isLoggedIn.asStateFlow()

    private val _userId = MutableStateFlow(getString(KEY_USER_ID))
    val userId: Flow<String?> = _userId.asStateFlow()

    private val _userName = MutableStateFlow(getString(KEY_USER_NAME))
    val userName: Flow<String?> = _userName.asStateFlow()

    private val _phoneNumber = MutableStateFlow(getString(KEY_PHONE_NUMBER))
    val phoneNumber: Flow<String?> = _phoneNumber.asStateFlow()

    fun saveUserEmail(email: String) {
        putString(KEY_USER_EMAIL, email)
        _userEmail.value = email
    }

    fun setLoggedIn(loggedIn: Boolean) {
        putBoolean(KEY_IS_LOGGED_IN, loggedIn)
        _isLoggedIn.value = loggedIn
    }

    fun saveUserId(userId: String) {
        putString(KEY_USER_ID, userId)
        _userId.value = userId
    }

    fun saveUserName(name: String) {
        putString(KEY_USER_NAME, name)
        _userName.value = name
    }

    fun savePhoneNumber(phone: String) {
        putString(KEY_PHONE_NUMBER, phone)
        _phoneNumber.value = phone
    }

    fun saveAuthToken(token: String) {
        putString(KEY_AUTH_TOKEN, token)
    }

    fun getAuthToken(): String? {
        return getString(KEY_AUTH_TOKEN)
    }

    fun saveRefreshToken(token: String) {
        putString(KEY_REFRESH_TOKEN, token)
    }

    fun getRefreshToken(): String? {
        return getString(KEY_REFRESH_TOKEN)
    }

    fun saveLastSyncTime(timestamp: Long) {
        putLong(KEY_LAST_SYNC, timestamp)
    }

    fun getLastSyncTime(): Long {
        return getLong(KEY_LAST_SYNC, 0L)
    }

    fun saveDatabasePassphrase(passphrase: String) {
        putString(KEY_DB_PASSPHRASE, passphrase)
    }

    fun getDatabasePassphrase(): String? {
        return getString(KEY_DB_PASSPHRASE)
    }

    fun clear() {
        encryptedPrefs.edit().clear().apply()
        _userEmail.value = null
        _isLoggedIn.value = false
        _userId.value = null
        _userName.value = null
        _phoneNumber.value = null
    }

    private fun getString(key: String): String? {
        return encryptedPrefs.getString(key, null)
    }

    private fun putString(key: String, value: String) {
        encryptedPrefs.edit().putString(key, value).apply()
    }

    private fun getBoolean(key: String, defaultValue: Boolean): Boolean {
        return encryptedPrefs.getBoolean(key, defaultValue)
    }

    private fun putBoolean(key: String, value: Boolean) {
        encryptedPrefs.edit().putBoolean(key, value).apply()
    }

    private fun getLong(key: String, defaultValue: Long): Long {
        return encryptedPrefs.getLong(key, defaultValue)
    }

    private fun putLong(key: String, value: Long) {
        encryptedPrefs.edit().putLong(key, value).apply()
    }

    private fun getInt(key: String, defaultValue: Int): Int {
        return encryptedPrefs.getInt(key, defaultValue)
    }

    private fun putInt(key: String, value: Int) {
        encryptedPrefs.edit().putInt(key, value).apply()
    }

    companion object {
        private const val PREFS_NAME = "mindsync_secure_prefs"
        private const val KEY_USER_EMAIL = "user_email"
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_USER_NAME = "user_name"
        private const val KEY_PHONE_NUMBER = "phone_number"
        private const val KEY_AUTH_TOKEN = "auth_token"
        private const val KEY_REFRESH_TOKEN = "refresh_token"
        private const val KEY_LAST_SYNC = "last_sync"
        private const val KEY_DB_PASSPHRASE = "db_passphrase"
    }
}
