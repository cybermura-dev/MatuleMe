package ru.takeshiko.matuleme.data.remote

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.storage.Storage
import io.github.jan.supabase.storage.storage

class SupabaseClientManager(
    url: String,
    key: String
) {

    val client: SupabaseClient = createSupabaseClient(
        supabaseUrl = url,
        supabaseKey = key
    ) {
        install(Auth) {
            autoSaveToStorage = true
            autoLoadFromStorage = true
        }
        install(Postgrest)
        install(Storage)
    }

    val auth get() = client.auth
    val postgrest get() = client.postgrest
    val storage get() = client.storage
}