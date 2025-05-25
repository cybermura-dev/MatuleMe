package ru.takeshiko.matuleme.data.local

import android.content.Context
import com.google.crypto.tink.Aead
import com.google.crypto.tink.subtle.Base64
import androidx.core.content.edit

class AppPreferencesManager(
    context: Context
) {
    private val prefs = context.getSharedPreferences("matuleme_prefs", Context.MODE_PRIVATE)
    private val aead: Aead = TinkProvider.getAead(context)

    companion object {
        private const val KEY_FIRST_LAUNCH = "first_launch"
    }

    var isFirstLaunch: Boolean
        get() = prefs.getBoolean(KEY_FIRST_LAUNCH, true)
        set(v) = prefs.edit { putBoolean(KEY_FIRST_LAUNCH, v) }

    fun setFirstLaunchCompleted() {
        isFirstLaunch = false
    }

    private fun putSecureString(key: String, value: String?) {
        if (value == null) {
            prefs.edit { remove(key) }
            return
        }
        val cipherText = aead.encrypt(value.toByteArray(), null)
        val encoded = Base64.encodeToString(cipherText, Base64.NO_WRAP)
        prefs.edit {putString(key, encoded) }
    }

    private fun getSecureString(key: String): String? {
        val encoded = prefs.getString(key, null) ?: return null
        val cipher = Base64.decode(encoded, Base64.NO_WRAP)
        val plain = aead.decrypt(cipher, null)
        return String(plain)
    }
}