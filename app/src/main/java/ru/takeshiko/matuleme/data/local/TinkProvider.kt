package ru.takeshiko.matuleme.data.local

import android.content.Context
import com.google.crypto.tink.Aead
import com.google.crypto.tink.aead.AeadConfig
import com.google.crypto.tink.aead.AesGcmKeyManager
import com.google.crypto.tink.integration.android.AndroidKeysetManager

object TinkProvider {
    private const val KEYSET_NAME = "master_keyset"
    private const val PREFERENCE_FILE = "tink_key_prefs"
    private const val MASTER_KEY_URI = "android-keystore://tink_master_key"

    init {
        AeadConfig.register()
    }

    fun getAead(context: Context): Aead {
        val keysetHandle = AndroidKeysetManager.Builder()
            .withSharedPref(context, KEYSET_NAME, PREFERENCE_FILE)
            .withKeyTemplate(AesGcmKeyManager.aes256GcmTemplate())
            .withMasterKeyUri(MASTER_KEY_URI)
            .build()
            .keysetHandle

        return keysetHandle.getPrimitive(Aead::class.java)
    }
}