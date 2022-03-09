package com.kaancelen.firestoresecrets.local

import android.content.SharedPreferences
import androidx.core.content.edit

private const val SERVICE_ONE_SECRET = "service_one_secret"
private const val SERVICE_ONE_CLIENT_ID = "service_one_client_id"

class ServiceOneSecretsProviderImpl(
    private val secretPreferences: SharedPreferences
): ServiceOneSecretsProvider {

    override fun setSecrets(clientId: String, secret: String) {
        secretPreferences.edit {
            putString(SERVICE_ONE_CLIENT_ID, clientId)
            putString(SERVICE_ONE_SECRET, secret)
        }
    }

    override fun getClientId() = secretPreferences.getString(SERVICE_ONE_CLIENT_ID, null)

    override fun getSecret() = secretPreferences.getString(SERVICE_ONE_SECRET, null)

    override fun hasSecrets() = secretPreferences.contains(SERVICE_ONE_CLIENT_ID) && secretPreferences.contains(SERVICE_ONE_SECRET)
}