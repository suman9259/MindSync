package com.example.mindsync.data.local.security

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import android.util.Base64

class CryptoManager {

    private val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE).apply {
        load(null)
    }

    private fun getKey(): SecretKey {
        val existingKey = keyStore.getEntry(KEY_ALIAS, null) as? KeyStore.SecretKeyEntry
        return existingKey?.secretKey ?: createKey()
    }

    private fun createKey(): SecretKey {
        return KeyGenerator.getInstance(ALGORITHM).apply {
            init(
                KeyGenParameterSpec.Builder(
                    KEY_ALIAS,
                    KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
                )
                    .setBlockModes(BLOCK_MODE)
                    .setEncryptionPaddings(PADDING)
                    .setUserAuthenticationRequired(false)
                    .setRandomizedEncryptionRequired(true)
                    .build()
            )
        }.generateKey()
    }

    fun encrypt(plainText: String): EncryptedData {
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.ENCRYPT_MODE, getKey())
        
        val encryptedBytes = cipher.doFinal(plainText.toByteArray(Charsets.UTF_8))
        
        return EncryptedData(
            cipherText = Base64.encodeToString(encryptedBytes, Base64.DEFAULT),
            iv = Base64.encodeToString(cipher.iv, Base64.DEFAULT)
        )
    }

    fun decrypt(encryptedData: EncryptedData): String {
        val cipher = Cipher.getInstance(TRANSFORMATION)
        val iv = Base64.decode(encryptedData.iv, Base64.DEFAULT)
        val spec = GCMParameterSpec(TAG_LENGTH, iv)
        
        cipher.init(Cipher.DECRYPT_MODE, getKey(), spec)
        
        val cipherText = Base64.decode(encryptedData.cipherText, Base64.DEFAULT)
        val decryptedBytes = cipher.doFinal(cipherText)
        
        return String(decryptedBytes, Charsets.UTF_8)
    }

    fun encryptToString(plainText: String): String {
        val encrypted = encrypt(plainText)
        return "${encrypted.iv}$SEPARATOR${encrypted.cipherText}"
    }

    fun decryptFromString(encryptedString: String): String {
        val parts = encryptedString.split(SEPARATOR)
        if (parts.size != 2) {
            throw IllegalArgumentException("Invalid encrypted string format")
        }
        val encryptedData = EncryptedData(
            iv = parts[0],
            cipherText = parts[1]
        )
        return decrypt(encryptedData)
    }

    fun generateDatabasePassphrase(): ByteArray {
        val key = getKey()
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.ENCRYPT_MODE, key)
        
        val passphraseBytes = "MindSyncSecureDB_${System.currentTimeMillis()}".toByteArray(Charsets.UTF_8)
        return cipher.doFinal(passphraseBytes)
    }

    companion object {
        private const val ANDROID_KEYSTORE = "AndroidKeyStore"
        private const val KEY_ALIAS = "MindSyncSecureKey"
        private const val ALGORITHM = KeyProperties.KEY_ALGORITHM_AES
        private const val BLOCK_MODE = KeyProperties.BLOCK_MODE_GCM
        private const val PADDING = KeyProperties.ENCRYPTION_PADDING_NONE
        private const val TRANSFORMATION = "AES/GCM/NoPadding"
        private const val TAG_LENGTH = 128
        private const val SEPARATOR = "::"
    }
}

data class EncryptedData(
    val cipherText: String,
    val iv: String
)
