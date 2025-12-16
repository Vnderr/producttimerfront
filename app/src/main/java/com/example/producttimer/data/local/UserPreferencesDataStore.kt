package com.example.producttimer.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey // Importar intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "user_prefs")

object UserPrefKeys {
    val FIREBASE_UID = stringPreferencesKey("firebase_uid")
    val NEON_USER_ID = intPreferencesKey("neon_user_id")
    val USER_NAME = stringPreferencesKey("user_name")
}

class UserPreferencesDataStore(private val context: Context) {

    val currentFirebaseUIDFlow: Flow<String?> = context.dataStore.data
        .map { prefs -> prefs[UserPrefKeys.FIREBASE_UID] }

    val currentNeonUserIdFlow: Flow<Int?> = context.dataStore.data
        .map { prefs -> prefs[UserPrefKeys.NEON_USER_ID] }

    val currentUserNameFlow: Flow<String?> = context.dataStore.data
        .map { prefs -> prefs[UserPrefKeys.USER_NAME] }

    suspend fun saveFirebaseUID(uid: String) {
        context.dataStore.edit { prefs -> prefs[UserPrefKeys.FIREBASE_UID] = uid }
    }

    suspend fun saveNeonUserId(id: Int) {
        context.dataStore.edit { prefs -> prefs[UserPrefKeys.NEON_USER_ID] = id }
    }

    suspend fun saveUserName(nombre: String) {
        context.dataStore.edit { prefs -> prefs[UserPrefKeys.USER_NAME] = nombre }
    }

    suspend fun clearSession() {
        context.dataStore.edit { prefs ->
            prefs.remove(UserPrefKeys.FIREBASE_UID)
            prefs.remove(UserPrefKeys.NEON_USER_ID)
            prefs.remove(UserPrefKeys.USER_NAME)
        }
    }
}